package models.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import models.*;
import models.services.DatabaseConnection;
import java.util.logging.*;

public class SqlShoppingListDAO implements ShoppingListDAO {

    private static final Logger logger = Logger.getLogger(SqlShoppingListDAO.class.getName());
    public SqlShoppingListDAO() {
        //Creatore vuoto in quanto non servirà inizializzare l'oggetto
    }

    //Metodo per recuperare la lista delle ShoppingList create dall'utente, composte solo dai dati della lista e non anche dai prodotti inseriti
    @Override
    public List<ShoppingList> findAllSummariesByOwner(String ownerEmail) {
        List<ShoppingList> summaries = new ArrayList<>();
        String query = "SELECT id, list_name, creation_date, supermarket, owner_email, items_quantity, total_price FROM shopping_lists WHERE owner_email = ? ORDER BY creation_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, ownerEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ShoppingList list = mapRowToShoppingList(rs);

                    summaries.add(list);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante caricamento summaries delle liste della spesa create", e);
        }
        return summaries;
    }


    @Override
    public void loadDetails(ShoppingList list) {

        if (list == null || list.getListId() == null) {
            logger.severe("Impossibile caricare dettagli. Lista nulla o ID mancante.");
            return;
        }

        //Elimino vecchi elementi della lista se presenti in memoria per sostituirli con quelli presi da DB
        list.getItems().clear();

        String sql = "SELECT id, list_id, product_name, product_category, product_price, product_weight, val_kcal, val_prot, val_carb, val_sug, val_fat, val_fib, quantity, is_for_diet FROM shopping_items WHERE list_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, list.getListId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    NutritionalValues values = new NutritionalValues(
                            rs.getDouble("val_kcal"),
                            rs.getDouble("val_carb"),
                            rs.getDouble("val_sug"),
                            rs.getDouble("val_fat"),
                            rs.getDouble("val_fib"),
                            rs.getDouble("val_prot")
                    );

                    String pName = rs.getString("product_name");
                    double pPrice = rs.getDouble("product_price");
                    double pWeight = rs.getDouble("product_weight");

                    AppCategory pCat = parseCategory(rs.getString("product_category"));

                    CommercialProduct prod = new CommercialProduct(
                            pName,
                            pPrice,
                            pWeight,
                            pCat,
                            values,
                            false
                    );

                    int qty = rs.getInt("quantity");
                    boolean isDiet = rs.getBoolean("is_for_diet");

                    list.addItem(prod, qty, isDiet);
                }
            }

            logger.info(() -> "Caricati " + list.getItems().size() + " prodotti per lista: " + list.getListId());

        } catch (SQLException e) {
            logger.log(Level.SEVERE, e, () -> "Errore durante caricamento dettagli della lista: " + list.getListId());
        }
    }

    //Metodo interno per gestire meglio le possibili eccezioni
    private AppCategory parseCategory(String catStr) {
        if (catStr == null) {
            return AppCategory.SCONOSCIUTO;
        }
        try {
            return AppCategory.valueOf(catStr);
        } catch (IllegalArgumentException _) {
            logger.warning("Categoria sconosciuta trovata nel DB: " + catStr);
            return AppCategory.SCONOSCIUTO;
        }
    }

    //Metodo che utilizza logica transazionale
    @Override
    public void save(ShoppingList list, String ownerEmail) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();

            //INIZIO TRANSAZIONE
            conn.setAutoCommit(false);

            if (list.getListId() == null) {
                //INSERT
                String sql = "INSERT INTO shopping_lists (list_name, creation_date, supermarket, owner_email, items_quantity, total_price) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, list.getListName());
                    stmt.setDate(2, java.sql.Date.valueOf(list.getCreationDate()));
                    stmt.setString(3, list.getSupermarket().name());
                    stmt.setString(4, ownerEmail);
                    stmt.setInt(5, list.getTotalItemsCount());
                    stmt.setDouble(6, list.getTotalCost());

                    stmt.executeUpdate();

                    //Recupero ID generato dal DB
                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            list.setListId(keys.getInt(1));
                        }
                    }
                }
            } else {
                //UPDATE
                String sql = "UPDATE shopping_lists SET list_name = ?, supermarket = ?, items_quantity = ?, total_price = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, list.getListName());
                    stmt.setString(2, list.getSupermarket().name());
                    stmt.setInt(3, list.getListId());
                    stmt.setInt(4, list.getTotalItemsCount());
                    stmt.setDouble(5, list.getTotalCost());
                    stmt.executeUpdate();
                }

                deleteItemsByListId(list.getListId(), conn);
            }

            saveItems(list, conn);

            //COMMIT
            conn.commit();
            logger.info("Lista salvata correttamente (ID: " + list.getListId() + ")");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante salvataggio lista nel DB, procedo con Rollback", e);
            //ROLLBACK
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.warning("Rollback eseguito.");}
                catch (SQLException ex1) {
                    logger.log(Level.SEVERE, "Errore durante Rollback, alcuni dati potrebbero trovarsi nel DB", ex1); }
            }
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { logger.log(Level.SEVERE, "Errore durante ripristino autocommit", e); }
            }
        }
    }

    //Metodo per eliminare una ShoppingList, grazie a ON DELETE CASCADE nel DB basta eliminare la lista da shopping_lists
    @Override
    public void delete(ShoppingList list, String email) {
        if (list == null || list.getListId() == null) return;

        String sql = "DELETE FROM shopping_lists WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, list.getListId());
            stmt.executeUpdate();
            logger.info("Eliminata da DB lista ID " + list.getListId());

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante eliminazione lista da DB", e);
        }
    }


    //Metodo privato per creare una ShoppingList dal risultato di una query
    private ShoppingList mapRowToShoppingList(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("list_name");
        SupermarketName market = SupermarketName.valueOf(rs.getString("supermarket"));
        LocalDate date = rs.getDate("creation_date").toLocalDate();
        int itemsQuantity = rs.getInt("items_quantity");
        double totalPrice = rs.getDouble("total_price");

        return new ShoppingList(id, name, market, date, itemsQuantity, totalPrice);
    }

    //Metodo privato per salvare gli shoppingItems all'interno del DB collegandoli alla lista di appartenenza
    private void saveItems(ShoppingList list, Connection conn) throws SQLException {
        String sql = "INSERT INTO shopping_items (" +
                "list_id, product_name, product_category, product_price, product_weight, " +
                "val_kcal, val_prot, val_carb, val_sug, val_fat, val_fib, " +
                "quantity, is_for_diet) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (ShoppingItem item : list.getItems()) {
                CommercialProduct prod = item.getProduct();
                NutritionalValues val = prod.getNutritionalValues();

                stmt.setInt(1, list.getListId());
                stmt.setString(2, prod.getName());
                stmt.setString(3, prod.getCategory().name());
                stmt.setDouble(4, prod.getPrice());
                stmt.setDouble(5, prod.getWeightInGrams());

                stmt.setDouble(6, val.getKcal());
                stmt.setDouble(7, val.getProteins());
                stmt.setDouble(8, val.getCarbs());
                stmt.setDouble(9, val.getSugar());
                stmt.setDouble(10, val.getFats());
                stmt.setDouble(11, val.getFibers());

                stmt.setInt(12, item.getQuantity());
                stmt.setBoolean(13, item.isForDiet());

                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }


    //Metodo privato per eliminare tutti gli ShoppingItems di una lista (usato per un update)
    private void deleteItemsByListId(int listId, Connection conn) throws SQLException {
        String sql = "DELETE FROM shopping_items WHERE list_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listId);
            stmt.executeUpdate();
        }
    }

}

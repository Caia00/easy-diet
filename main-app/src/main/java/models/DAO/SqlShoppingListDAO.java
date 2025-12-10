package models.DAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import models.*;
import models.services.DatabaseConnection;

public class SqlShoppingListDAO implements ShoppingListDAO {
    public SqlShoppingListDAO() { }

    //Metodo per recuperare la lista delle ShoppingList create dall'utente, composte solo dai dati della lista e non anche dai prodotti inserit
    @Override
    public List<ShoppingList> findAllSummariesByOwner(String ownerEmail) {
        List<ShoppingList> summaries = new ArrayList<>();
        String query = "SELECT * FROM shopping_lists WHERE owner_email = ? ORDER BY creation_date DESC";

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
            e.printStackTrace();
        }
        return summaries;
    }

    //Metodo per recuperare tutti i prodotti contenuti in una ShoppingList
    @Override
    public void loadDetails(ShoppingList list) {

        if (list == null || list.getListId() == null) {
            System.err.println("DAO ERROR: Impossibile caricare dettagli. Lista nulla o ID mancante.");
            return;
        }

        //Elimino vecchi elementi della lista se presenti in memoria per sostiuirli con quelli presi da DB
        list.getItems().clear();

        String sql = "SELECT * FROM shopping_items WHERE list_id = ?";

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

                    String catStr = rs.getString("product_category");
                    AppCategory pCat = AppCategory.SCONOSCIUTO;
                    if (catStr != null) {
                        try {
                            pCat = AppCategory.valueOf(catStr);
                        } catch (IllegalArgumentException e) {
                            System.err.println("WARNING: Categoria sconosciuta nel DB: " + catStr);
                        }
                    }

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

            System.out.println("DAO: Caricati " + list.getItems().size() + " prodotti per lista " + list.getListId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //Metodo per salvare una lista della spesa nel DB, transazionale
    @Override
    public void save(ShoppingList list, String ownerEmail) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();

            //INIZIO TRANSAZIONE
            conn.setAutoCommit(false);

            if (list.getListId() == null) {
                //INSERT
                String sql = "INSERT INTO shopping_lists (list_name, creation_date, supermarket, owner_email) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, list.getListName());
                    stmt.setDate(2, java.sql.Date.valueOf(list.getCreationDate()));
                    stmt.setString(3, list.getSupermarket().name());
                    stmt.setString(4, ownerEmail);

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
                String sql = "UPDATE shopping_lists SET list_name = ?, supermarket = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, list.getListName());
                    stmt.setString(2, list.getSupermarket().name());
                    stmt.setInt(3, list.getListId());
                    stmt.executeUpdate();
                }

                deleteItemsByListId(list.getListId(), conn);
            }

            saveItems(list, conn);

            //COMMIT
            conn.commit();
            System.out.println("LOG: Lista salvata correttamente (ID: " + list.getListId() + ")");

        } catch (SQLException e) {
            e.printStackTrace();
            //ROLLBACK
            if (conn != null) {
                try { conn.rollback(); System.err.println("LOG: Rollback eseguito."); }
                catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    //Metodo per eliminare una ShoppingList, grazie a ON DELETE CASCADE nel DB basta eliminare la lista da shopping_lists
    @Override
    public void delete(ShoppingList list) {
        if (list == null || list.getListId() == null) return;

        String sql = "DELETE FROM shopping_lists WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, list.getListId());
            stmt.executeUpdate();
            System.out.println("LOG: Eliminata da DB lista ID " + list.getListId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //Metodo privato per creare una ShoppingList dal risultato di una query
    private ShoppingList mapRowToShoppingList(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("list_name");
        SupermarketName market = SupermarketName.valueOf(rs.getString("supermarket"));
        LocalDate date = rs.getDate("creation_date").toLocalDate();

        return new ShoppingList(id, name, market, date);
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

                stmt.executeUpdate();
            }
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

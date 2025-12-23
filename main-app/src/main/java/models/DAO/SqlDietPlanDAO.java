package models.DAO;

import models.*;
import models.services.DatabaseConnection;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

public class SqlDietPlanDAO implements DietPlanDAO {

    private static final Logger logger = Logger.getLogger(SqlDietPlanDAO.class.getName());

    public SqlDietPlanDAO() {}

    //Metodo usato da user per trovare la dieta che gli è stata assegnata
    @Override
    public DietPlan findByOwner(String userEmail) {
        String query = "SELECT dp.* FROM diet_plans dp " +
                "JOIN users u ON u.current_diet_id = dp.id " +
                "WHERE u.email = ?";

        DietPlan plan = null;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    plan = new DietPlan(rs.getString("name"));
                    plan.setDietId(rs.getInt("id"));

                    loadMealsForPlan(plan, conn);
                }else{
                    logger.info("Nessuna dieta trovata nel DB");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante metodo ricerca DietPlan tramite user email", e);
        }
        return plan;
    }

    //Metodo usato dal nutrizionista per caricare dal DB una sua dieta specifica
    @Override
    public void loadPlanDetails(DietPlan plan) {
        if (plan == null || plan.getDietId() == null) {
            logger.severe("Impossibile caricare dettagli dieta, piano nullo o id mancante");
            return;
        }

        for (List<Meal> dayMeals : plan.getWeeklySchedule().values()) {
            dayMeals.clear();
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {

            loadMealsForPlan(plan, conn);

            logger.info("Dettagli caricati per dieta: " + plan.getDietName());

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante caricamento dettagli dieta: " + plan.getDietName(), e);
        }
    }

    //Metodo usato all'avvio della home del nutrizionista per caricare la lista delle diete da lui create, solo però i nomi e gli ID non direttamente tutti gli oggetti contenuti
    @Override
    public List<DietPlan> findAllSummariesByCreator(String nutritionistEmail) {
        List<DietPlan> summaries = new ArrayList<>();

        String query = "SELECT id, name FROM diet_plans WHERE creator_email = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nutritionistEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DietPlan summary = new DietPlan(rs.getString("name"));
                    summary.setDietId(rs.getInt("id"));

                    summaries.add(summary);
                }
                logger.info(String.format("Caricate da DB %d diete", summaries.size()));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante caricamento summaries dal DB", e);
        }
        return summaries;
    }



    //Metodo di salvataggio di un DietPlan all'interno del DB, utilizza logica transazionale
    @Override
    public void save(DietPlan plan, String creatorEmail) {
        Connection conn = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();

            //INIZIO TRANSAZIONE
            conn.setAutoCommit(false);

            if (plan.getDietId() == null) {
                //INSERT di una nuova dieta nella tabella diet_plans
                String sql = "INSERT INTO diet_plans (name, creator_email) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, plan.getDietName());
                    stmt.setString(2, creatorEmail);
                    stmt.executeUpdate();

                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            plan.setDietId(keys.getInt(1)); //Aggiorno l'ID dell'oggetto Java
                        }
                    }
                }
            } else {
                //UPDATE aggiorno solo il nome della dieta, l'ID non cambia
                String sql = "UPDATE diet_plans SET name = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, plan.getDietName());
                    stmt.setInt(2, plan.getDietId());
                    stmt.executeUpdate();
                }

                //Elimino tutto il contenuto della dieta che verrà ricreato così aggiornato
                deleteMealsByDietId(plan.getDietId(), conn);
            }

            //Salvataggio del contenuto del DietPlan
            saveMeals(plan, conn);

            //Commit della transazione
            conn.commit();
            logger.info("DietPlan salvato con successo (ID: " + plan.getDietId() + ")");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante transazione salvataggio DietPlan nel DB, procede rollback", e);
            //Rollback in caso di errore
            if (conn != null) {
                try {
                    logger.severe("Rollback eseguito, nessun dato salvato");
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Rollback non andato a buon fine", ex);
                }
            }
        } finally {
            //Ripristino autocommit per il riutilizzo della connessione
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Errore durante ripristino autocommit", e); }
            }
        }
    }


    //Metodo per salvare tutti i dati salvati nella weeklySchedule del DietPlan
    private void saveMeals(DietPlan plan, Connection conn) throws SQLException {
        String insertMealSql = "INSERT INTO meals (diet_id, day_of_week, meal_name, meal_time) VALUES (?, ?, ?, ?)";

        for (Map.Entry<String, List<Meal>> entry : plan.getWeeklySchedule().entrySet()) {
            String day = entry.getKey();
            List<Meal> meals = entry.getValue();

            for (Meal meal : meals) {
                try (PreparedStatement stmt = conn.prepareStatement(insertMealSql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, plan.getDietId());
                    stmt.setString(2, day);
                    stmt.setString(3, meal.getName());

                    if (meal.getTime() != null) {
                        stmt.setTime(4, Time.valueOf(meal.getTime()));
                    } else {
                        stmt.setNull(4, Types.TIME);
                    }

                    stmt.executeUpdate();

                    // Recupero ID del pasto per salvare i suoi cibi
                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            int mealId = keys.getInt(1);
                            saveDietItems(mealId, meal.getFoods(), conn);
                        }
                    }
                }
            }
        }
    }

    //Salva i singoli cibi presenti in un pasto
    private void saveDietItems(int mealId, List<DietItem> items, Connection conn) throws SQLException {
        String sql = "INSERT INTO diet_items (meal_id, target_category, target_kcal, target_proteins, " +
                "target_carbs, target_sugar, target_fats, target_fibers, suggested_product_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (DietItem item : items) {
                NutritionalTarget t = item.getTarget();

                stmt.setInt(1, mealId);
                stmt.setString(2, t.getCategory().name());
                stmt.setDouble(3, t.getTargetKcal());
                stmt.setDouble(4, t.getTargetProteins());
                stmt.setDouble(5, t.getTargetCarbs());
                stmt.setDouble(6, t.getTargetSugar());
                stmt.setDouble(7, t.getTargetFats());
                stmt.setDouble(8, t.getTargetFibers());

                // Gestione prodotto suggerito (Optional)
                if (item.getSuggestedProduct().isPresent()) {
                    stmt.setString(9, item.getSuggestedProduct().get().getName());
                } else {
                    stmt.setNull(9, Types.VARCHAR);
                }

                stmt.executeUpdate(); // Eseguo per ogni item
            }
        }
    }

    private void deleteMealsByDietId(int dietId, Connection conn) throws SQLException {
        //Grazie al ON DELETE CASCADE nel DB, cancellando i pasti si cancellano anche gli items
        String sql = "DELETE FROM meals WHERE diet_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dietId);
            stmt.executeUpdate();
        }
    }


    //Metodo usato per il caricamento dei dati dal DB
    private void loadMealsForPlan(DietPlan plan, Connection conn) throws SQLException {
        String query = "SELECT * FROM meals WHERE diet_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, plan.getDietId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int mealId = rs.getInt("id");
                    String day = rs.getString("day_of_week");
                    String name = rs.getString("meal_name");
                    Time sqlTime = rs.getTime("meal_time");
                    LocalTime time = (sqlTime != null) ? sqlTime.toLocalTime() : null;

                    Meal meal = new Meal(name, time);

                    loadItemsForMeal(meal, mealId, conn);

                    plan.addMealToDay(day, meal);
                }
            }
        }
    }

    private void loadItemsForMeal(Meal meal, int mealId, Connection conn) throws SQLException {
        String query = "SELECT * FROM diet_items WHERE meal_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, mealId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    //Ricostruisco il Target
                    AppCategory cat = AppCategory.valueOf(rs.getString("target_category"));
                    double kcal = rs.getDouble("target_kcal");
                    double prot = rs.getDouble("target_proteins");
                    double carb = rs.getDouble("target_carbs");
                    double sug = rs.getDouble("target_sugar");
                    double fat = rs.getDouble("target_fats");
                    double fib = rs.getDouble("target_fibers");

                    NutritionalTarget target = new NutritionalTarget(cat, kcal, prot, carb, fat, fib, sug);

                    //Ricostruisco il prodotto suggerito prendendo il nome e valori fittizzi in quanto per la dieta basterà il nome
                    String suggName = rs.getString("suggested_product_name");
                    CommercialProduct suggestedProd = null;
                    if (suggName != null) {
                        suggestedProd = new CommercialProduct(suggName, 0, 0, cat, new NutritionalValues(), false);
                    }

                    meal.addFoodItem(new DietItem(target, suggestedProd));
                }
            }
        }
    }

    @Override
    public void delete(DietPlan plan, String email) {
        if (plan == null || plan.getDietId() == null) {
            logger.severe("Impossibile eliminare un DietPlan senza ID");
            return;
        }

        String sql = "DELETE FROM diet_plans WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, plan.getDietId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Eliminata da DB dieta: " + plan.getDietName() + " e tutti i pasti ad essa collegati");
            } else {
                logger.warning("Nessuna dieta trovata con ID " + plan.getDietId() + " (forse era già stata eliminata).");
            }

        } catch (SQLException e) {
                logger.log(Level.SEVERE, "Errore durante l'eliminazione dal DB della dieta", e);
            }
        }
    }

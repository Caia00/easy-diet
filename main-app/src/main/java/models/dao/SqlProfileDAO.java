package models.dao;

import exception.PersistenceException;
import models.*;
import models.beans.ProfileBean;
import models.services.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.logging.*;
import java.util.logging.Logger;

public class SqlProfileDAO implements ProfileDAO {

    private static final Logger logger = Logger.getLogger(SqlProfileDAO.class.getName());


    @Override
    public Profile findByEmail(String email) {
        String query = "SELECT email, password, name, surname, birth_date, role, height, weight, gender, register_id FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToProfile(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il metodo findByEmail", e);
        }
        return null;
    }


    @Override
    public void save(Profile profile) {
        String query = "REPLACE INTO users (email, password, name, surname, birth_date, role, height, weight, gender, register_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, profile.getEmail());
            stmt.setString(2, profile.getPassword());
            stmt.setString(3, profile.getName());
            stmt.setString(4, profile.getSurname());
            stmt.setDate(5, java.sql.Date.valueOf(profile.getBirthDate()));

            if (profile instanceof User u) {
                stmt.setString(6, "PATIENT");
                stmt.setDouble(7, u.getHeightCm());
                stmt.setDouble(8, u.getCurrentWeightKg());
                stmt.setString(9, u.getGender());
                stmt.setNull(10, Types.VARCHAR);
            }
            else if (profile instanceof Nutritionist n) {
                stmt.setString(6, "NUTRITIONIST");
                stmt.setNull(7, Types.DOUBLE);
                stmt.setNull(8, Types.DOUBLE);
                stmt.setNull(9, Types.VARCHAR);
                stmt.setString(10, n.getProfessionalCode());
            }

            stmt.executeUpdate();
            logger.info("Profilo salvato nel DB");

        } catch (SQLException e) {
            throw new PersistenceException("Errore durante salvataggio profilo nel DB: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean assignDiet(String patientEmail, DietPlan plan) {
        if (plan == null || plan.getDietId() == null) return false;

        String sql = "UPDATE users SET current_diet_id = ? WHERE email = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, plan.getDietId());
            stmt.setString(2, patientEmail);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante metodo assignDiet", e);
            return false;
        }
    }


    @Override
    public void delete(String email) {
        String query = "DELETE FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.executeUpdate();
            logger.info("Profilo correttamente eliminato");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il metodo delete", e);
        }
    }

    private Profile mapRowToProfile(ResultSet rs) throws SQLException {
        String role = rs.getString("role");

        String email = rs.getString("email");
        String pass = rs.getString("password");
        String name = rs.getString("name");
        String surname = rs.getString("surname");
        java.sql.Date sqlDate = rs.getDate("birth_date");
        LocalDate birth = (sqlDate != null) ? sqlDate.toLocalDate() : null;

        if ("PATIENT".equals(role)) {
            double h = rs.getDouble("height");
            double w = rs.getDouble("weight");
            String g = rs.getString("gender");
            ProfileBean bean = new ProfileBean(name, surname, email, pass, birth);
            return new User(bean, h, w, g);
        }
        else if ("NUTRITIONIST".equals(role)) {
            String regId = rs.getString("register_id");
            return new Nutritionist(name, surname, email, pass, birth, regId);
        }

        return null;
    }
}

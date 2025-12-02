package models.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    // Configura con i tuoi dati MySQL
    private final String url = "jdbc:mysql://localhost:3306/easy_diet_db";
    private final String username = "root";
    private final String password = "";

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connessione al DB MySQL riuscita!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Errore connessione Database");
        }
    }

    public static DatabaseConnection getInstance() {
        try {
            if (instance == null || instance.getConnection().isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}

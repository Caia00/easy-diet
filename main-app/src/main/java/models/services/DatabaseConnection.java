package models.services;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.*;

public class DatabaseConnection {
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static DatabaseConnection instance;
    private Connection connection;
    private String url;
    private String username;
    private String password;

    private DatabaseConnection() throws SQLException {
        try {
            loadConfig();
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
            logger.info("Connessione al DB MySQL riuscita!");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Errore connessione Database", e);
        }
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection() == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public void loadConfig() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Spiacente, non trovo config.properties");
                return;
            }
            prop.load(input);
            this.password = prop.getProperty("db.password");
            this.username = prop.getProperty("db.user");
            this.url = prop.getProperty("db.url");

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Errore nella configurazione delle properties", ex);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}

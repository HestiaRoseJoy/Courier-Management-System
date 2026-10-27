package com.courier.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DatabaseConnection Class
 * 
 * Manages the JDBC connection to MySQL.
 * Reads database credentials dynamically from 'db.properties' to ensure
 * passwords and configurations are never hardcoded in source code.
 */
public class DatabaseConnection {

    private static String url = "jdbc:mysql://localhost:3306/courier_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String user = "root";
    private static String password = "";

    static {
        // Load configurations from db.properties if available
        loadProperties();
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseConnection] MySQL JDBC Driver not found in classpath: " + e.getMessage());
        }
    }

    private static void loadProperties() {
        Properties props = new Properties();
        File propFile = new File("db.properties");
        if (propFile.exists()) {
            try (InputStream in = new FileInputStream(propFile)) {
                props.load(in);
                url = props.getProperty("db.url", url);
                user = props.getProperty("db.user", user);
                password = props.getProperty("db.password", password);
            } catch (Exception e) {
                System.err.println("[DatabaseConnection] Could not read db.properties, using default parameters: " + e.getMessage());
            }
        }
    }

    /**
     * Establishes and returns an active connection to MySQL.
     * 
     * @return Connection object
     * @throws SQLException if database connection fails
     */
    public static Connection getConnection() throws SQLException {
        // Reload properties in case user modified db.properties during runtime
        loadProperties();
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Helper method to test if database connection is successful.
     * 
     * @return true if connected, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}

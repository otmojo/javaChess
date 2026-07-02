package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static String envOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    private static final String URL = envOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/chess_db");
    private static final String USER = envOrDefault("DB_USER", "postgres");
    private static final String PASS = envOrDefault("DB_PASSWORD", "");

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("coundn't find JDBC Driver", e);
        }
    }
}
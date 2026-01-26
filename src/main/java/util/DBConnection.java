package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = System.getenv("CHESS_DB_URL");
    private static final String USER = System.getenv("CHESS_DB_USER");
    private static final String PASS = System.getenv("CHESS_DB_PASS");
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("coundn't find JDBC Driver", e);
        }
    }
}
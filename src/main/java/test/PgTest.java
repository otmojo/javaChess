package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class PgTest {
    private static String envOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    public static void main(String[] args) throws Exception {
        String url = envOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/postgres");
        String user = envOrDefault("DB_USER", "postgres");
        String pass = envOrDefault("DB_PASSWORD", "");

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("select now()")) {

            if (rs.next()) {
                System.out.println("Connected. Server time: " + rs.getString(1));
            }
        }
    }
}
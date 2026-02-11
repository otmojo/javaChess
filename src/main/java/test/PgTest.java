package test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import util.DBConnection;

public class PgTest {
    public static void main(String[] args) throws Exception {
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("select now()")) {

            if (rs.next()) {
                System.out.println("Connected. Server time: " + rs.getString(1));
            }
        }
    }
}

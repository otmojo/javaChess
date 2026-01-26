package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MoveDAO {
    // for chess_db
    private String url = "jdbc:postgresql://localhost:5432/chess_db";
    private String user = "postgres"; 
    private String password = "hal"; //delete this before sharing

    public void saveMove(String player, String piece, int fr, int fc, int tr, int tc) {
        try {
            // just in case forced load PostgreSQL driver
            Class.forName("org.postgresql.Driver");

            String sql = "INSERT INTO move_history (player, piece, from_pos, to_pos) VALUES (?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, player);
                ps.setString(2, piece);
                ps.setString(3, fr + "," + fc);
                ps.setString(4, tr + "," + tc);
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    System.out.println("DB: steps are saved in the database (player=" + player + ", piece=" + piece + ")");
                } else {
                    System.err.println("DB: 插入未生效 (executeUpdate returned 0)");
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("找不到 PostgreSQL 驱动 JAR 包，请检查 WEB-INF/lib！");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("DB错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * dont need too much history for now.
     */
    public void clearMoves() {
        String sql = "DELETE FROM move_history";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement st = conn.createStatement()) {
            int removed = st.executeUpdate(sql);
            System.out.println("DB: MOVES CLEARED，DELETED LINE=" + removed);
        } catch (SQLException e) {
            System.err.println("DB ERR CLEARED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String> getHistory() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT * FROM move_history ORDER BY id ASC";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String record = String.format("Step %d: %s moved %s from %s to %s",
                    rs.getInt("id"),
                    rs.getString("player"),
                    rs.getString("piece"),
                    rs.getString("from_pos"),
                    rs.getString("to_pos"));
                list.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Returns a structured movement record for front-end playback.
     * every record as  String[]: {id, player, piece, from_pos, to_pos}
     */
    public List<String[]> getMoves() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT * FROM move_history ORDER BY id ASC";
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("找不到 PostgreSQL 驱动 JAR 包，请检查 WEB-INF/lib！");
        }
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String[] rec = new String[5];
                rec[0] = String.valueOf(rs.getInt("id"));
                rec[1] = rs.getString("player");
                rec[2] = rs.getString("piece");
                rec[3] = rs.getString("from_pos");
                rec[4] = rs.getString("to_pos");
                list.add(rec);
            }
            System.out.println("DB: getMoves() returned " + list.size() + " records");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
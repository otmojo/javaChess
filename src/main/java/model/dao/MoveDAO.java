package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.DBConnection;

public class MoveDAO {

    public void saveMove(String player, String piece, int fr, int fc, int tr, int tc) {
        try {
            String sql = "INSERT INTO move_history (player, piece, from_pos, to_pos) VALUES (?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, player);
                ps.setString(2, piece);
                ps.setString(3, fr + "," + fc);
                ps.setString(4, tr + "," + tc);
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    System.out.println("DB: steps are saved in the database (player=" + player + ", piece=" + piece + ")");
                } else {
                    System.err.println("DB: insert did not take effect (executeUpdate returned 0)");
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * dont need too much history for now.
     */
    public void clearMoves() {
        String sql = "DELETE FROM move_history";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {
            int removed = st.executeUpdate(sql);
            System.out.println("DB: MOVES CLEARED, DELETED LINE=" + removed);
        } catch (SQLException e) {
            System.err.println("DB ERR CLEARED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String> getHistory() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT * FROM move_history ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
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
        try (Connection conn = DBConnection.getConnection();
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

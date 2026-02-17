package test;

import java.util.List;

import model.dao.MoveDAO;

public class DBTest {
    public static void main(String[] args) {
        System.out.println("DBTest: starting MoveDAO.getHistory()...");
        MoveDAO dao = new MoveDAO();
        try {
            
            dao.saveMove("test-room", "tester", "P", 6, 0, 5, 0);
            
            
            List<String[]> moves = dao.getMoves("test-room");
            
            if (moves == null || moves.isEmpty()) {
                System.out.println("DBTest: no history records returned.");
            } else {
                System.out.println("DBTest: retrieved records:");
                for (String[] m : moves) {
                    System.out.println("Step " + m[0] + ": " + m[1] + " moved " + m[2] + " from " + m[3] + " to " + m[4]);
                }
            }
        } catch (Exception e) {
            System.err.println("DBTest: error while querying DB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

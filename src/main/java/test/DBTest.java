package test;

import java.util.List;

import model.dao.MoveDAO;

public class DBTest {
    public static void main(String[] args) {
        System.out.println("DBTest: starting MoveDAO.getHistory()...");
        MoveDAO dao = new MoveDAO();
        try {
            // 插入一条测试记录，再读取全部历史验证写入
            dao.saveMove("tester", "P", 6, 0, 5, 0);
            List<String> history = dao.getHistory();
            if (history == null || history.isEmpty()) {
                System.out.println("DBTest: no history records returned.");
            } else {
                System.out.println("DBTest: retrieved records:");
                for (String r : history) System.out.println(r);
            }
        } catch (Exception e) {
            System.err.println("DBTest: error while querying DB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

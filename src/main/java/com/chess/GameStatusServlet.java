package com.chess;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import com.chess.RoomManager;

@WebServlet("/gameStatus")
public class GameStatusServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String roomId = request.getParameter("roomId");
        if (roomId == null || roomId.isEmpty()) {
            response.setStatus(400);
            response.getWriter().write("ERROR: Missing roomId");
            return;
        }
        
        // get game status
        String status = RoomManager.getGameStatus(roomId);
        String winner = RoomManager.getWinner(roomId);
        
        // return: status|winner（winner can be null）
        if (winner != null) {
            response.getWriter().write(status + "|" + winner);
        } else {
            response.getWriter().write(status + "|");
        }
    }
}

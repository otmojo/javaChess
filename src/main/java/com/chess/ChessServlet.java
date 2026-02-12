package com.chess;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import com.chess.RoomManager;

@WebServlet("/chessAction")
public class ChessServlet extends HttpServlet {

    // GET
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String roomId = request.getParameter("roomId");
        String move = RoomManager.getMove(roomId);
        response.getWriter().write(move);
    }

    // POST
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String roomId = request.getParameter("roomId");
        String move = request.getParameter("move");
        RoomManager.setMove(roomId, move);
        response.getWriter().write("ok");
    }
}

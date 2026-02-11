package com.chess;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import com.chess.RoomManager;

@WebServlet("/chessAction")
public class ChessServlet extends HttpServlet {

    // GET 用于轮询：获取对手的棋步
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String roomId = request.getParameter("roomId");
        String move = RoomManager.getMove(roomId);
        response.getWriter().write(move);
    }

    // POST 用于提交：发送我的棋步
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String roomId = request.getParameter("roomId");
        String move = request.getParameter("move");
        RoomManager.setMove(roomId, move);
        response.getWriter().write("ok");
    }
}

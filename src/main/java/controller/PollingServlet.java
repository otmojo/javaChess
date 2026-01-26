package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.google.gson.Gson;

import model.entity.Board;

@WebServlet("/polling")
public class PollingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession();
        Board board = (Board) session.getAttribute("board");
        String currentTurn = (String) session.getAttribute("turn");


        //  gameId via database
        if (board == null) {
            board = new Board(); // back to default
            currentTurn = "white";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("turn", currentTurn);
        result.put("board", board.getGrid());

        response.getWriter().write(new Gson().toJson(result));
    }
}

package com.chess;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;

@WebServlet("/createRoom")
public class CreateRoomServlet extends HttpServlet {
    
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String roomId = RoomManager.createRoom();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("roomId", roomId);
        
        response.getWriter().write(gson.toJson(result));
    }
}

package com.chess;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;

@WebServlet("/joinRoom")
public class JoinRoomServlet extends HttpServlet {
    
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String roomId = request.getParameter("roomId");
        Map<String, Object> result = new HashMap<>();
        
        if (roomId == null || roomId.isEmpty()) {
            result.put("success", false);
            result.put("message", "房间号不能为空");
            response.getWriter().write(gson.toJson(result));
            return;
        }
        
        // try to join the room
        int side = RoomManager.joinRoom(roomId);
        
        if (side > 0) {
            HttpSession session = request.getSession();
            session.setAttribute("roomId", roomId);
            session.setAttribute("mySide", side);
            
            result.put("success", true);
            result.put("side", side);
            result.put("roomId", roomId);
        } else {
            result.put("success", false);
            result.put("message", "房间已满");
        }
        
        response.getWriter().write(gson.toJson(result));
    }
}

@WebServlet("/createRoom")
class CreateRoomServlet extends HttpServlet {
    
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // create a new room
        String roomId = RoomManager.createRoom();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("roomId", roomId);
        
        response.getWriter().write(gson.toJson(result));
    }
}

package controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.MoveDAO;
import model.entity.Board;
import model.logic.RuleEngine;
import com.chess.RoomManager;

@WebServlet("/game")
public class GameServlet extends HttpServlet {
    
    private MoveDAO moveDAO = new MoveDAO();
    private RuleEngine engine = new RuleEngine();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
            if ("reset".equals(request.getParameter("action"))) {
                HttpSession session = request.getSession();
                String roomId = "game1";
                Board newBoard = new Board();
                RoomManager.setBoard(roomId, newBoard);
                RoomManager.setTurn(roomId, "white");
                RoomManager.resetGameStatus(roomId);
                // ===========================
                
                session.setAttribute("board", newBoard); 
                session.setAttribute("turn", "white");      
                // save one round
                try {
                    moveDAO.clearMoves();
                } catch (Exception e) {
                    System.err.println("error when the moves got cleared: " + e.getMessage());
                }
                // f5
                session.removeAttribute("gameMoves");
                session.removeAttribute("moveCounter");
                response.sendRedirect("game");              // f5
                return;
            }
    	
        System.out.println("go into doGet route /game");
        HttpSession session = request.getSession();

        String roomId = "game1";
        Board board = RoomManager.getBoard(roomId);
        String turn = RoomManager.getTurn(roomId);

        if (board == null) {
            board = new Board();
            RoomManager.setBoard(roomId, board);
            RoomManager.setTurn(roomId, "white");
            RoomManager.resetGameStatus(roomId);
            // ===========================
            turn = "white";
        }
        
        session.setAttribute("board", board);
        session.setAttribute("turn", turn);

        // 2. according to action to load history
        String action = request.getParameter("action");
        if ("history".equals(action)) {
            
            List<String[]> moves = moveDAO.getMoves(); 
            
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < moves.size(); i++) {
                String[] m = moves.get(i);
                // m: {id, player, piece, from_pos, to_pos}
                String id = m[0];
                String player = m[1] == null ? "" : m[1];
                String piece = m[2] == null ? "" : m[2];
                String from = m[3] == null ? "" : m[3];
                String to = m[4] == null ? "" : m[4];
                String[] fcoords = from.split(",");
                String[] tcoords = to.split(",");
                sb.append("{");
                sb.append("\"id\":").append(id).append(",");
                sb.append("\"player\":\"").append(player).append("\",");
                sb.append("\"piece\":\"").append(piece).append("\",");
                sb.append("\"from\":[").append(fcoords.length>0?fcoords[0]:"0").append(",").append(fcoords.length>1?fcoords[1]:"0").append("],");
                sb.append("\"to\":[").append(tcoords.length>0?tcoords[0]:"0").append(",").append(tcoords.length>1?tcoords[1]:"0").append("]");
                sb.append("}");
                if (i < moves.size() - 1) sb.append(",");
            }
            sb.append("]");
            request.setAttribute("moveJson", sb.toString());
            request.getRequestDispatcher("/WEB-INF/jsp/replay.jsp").forward(request, response);
            return;
        }

        // return to all the movements in JSON （for frontend AJAX）
        if ("movesJson".equals(action)) {
            
            List<String[]> moves = moveDAO.getMoves();
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < moves.size(); i++) {
                String[] m = moves.get(i);
                String id = m[0];
                String player = m[1] == null ? "" : m[1];
                String piece = m[2] == null ? "" : m[2];
                String from = m[3] == null ? "" : m[3];
                String to = m[4] == null ? "" : m[4];
                String[] fcoords = from.split(",");
                String[] tcoords = to.split(",");
                sb.append("{");
                sb.append("\"id\":").append(id).append(",");
                sb.append("\"player\":\"").append(player).append("\",");
                sb.append("\"piece\":\"").append(piece).append("\",");
                sb.append("\"from\":[").append(fcoords.length>0?fcoords[0]:"0").append(",").append(fcoords.length>1?fcoords[1]:"0").append("],");
                sb.append("\"to\":[").append(tcoords.length>0?tcoords[0]:"0").append(",").append(tcoords.length>1?tcoords[1]:"0").append("]");
                sb.append("}");
                if (i < moves.size() - 1) sb.append(",");
            }
            sb.append("]");
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(sb.toString());
            return;
        }

        // 3. 
        
        request.getRequestDispatcher("/WEB-INF/jsp/game.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Encoding settings
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        // 2. adjusting
        String fR = request.getParameter("fR");
        String fC = request.getParameter("fC");
        String tR = request.getParameter("tR");
        String tC = request.getParameter("tC");
        
        System.out.println("got demand to move -> From(" + fR + "," + fC + ") To(" + tR + "," + tC + ")");

        // 3. Parameter validation
        if (fR == null || fC == null || tR == null || tC == null) {
            System.err.println("ERROR: 参数丢失，POST 数据解析失败！");
            response.setStatus(400);
            response.getWriter().write("Error: Parameters missing.");
            return;
        }

        try {
            HttpSession session = request.getSession();
            String roomId = "game1";
            
            // ===== check: game is end or not  =====
            if (RoomManager.isGameOver(roomId)) {
                response.setStatus(403);
                response.getWriter().write("GAME_ALREADY_ENDED");
                return;
            }
            // ================================
            
            Board board = (Board) session.getAttribute("board");
            if (board == null) {
                response.setStatus(400);
                response.getWriter().write("Error: Board not initialized.");
                return;
            }
            String turn = (String) session.getAttribute("turn"); // currently which one is allowed to move

            int rF = Integer.parseInt(fR);
            int cF = Integer.parseInt(fC);
            int rT = Integer.parseInt(tR);
            int cT = Integer.parseInt(tC);

            String[][] grid = board.getGrid();
            String movingPiece = grid[rF][cF]; // the chosen one
            String targetPiece = grid[rT][cT]; // aim

            // --- 1. Basic check: The starting square must have a piece. ---
            if (movingPiece == null || movingPiece.equals("")) {
                response.setStatus(400);
                response.getWriter().write("Error: No piece at source square.");
                return;
            }

            // --- 2. Round verification ---
            boolean isWhitePiece = Character.isUpperCase(movingPiece.charAt(0));
            if (("white".equals(turn) && !isWhitePiece) || ("black".equals(turn) && isWhitePiece)) {
                response.setStatus(403); // frobid
                response.getWriter().write("对方のターンです");
                return;
            }

            boolean legal = engine.isLegalMove(board, rF, cF, rT, cT, turn);

            if (!legal) {
                response.setStatus(403);
                response.getWriter().write("不正な移動です (移动不合法)");
                return; // invalid
            }

            // --- 3. Perform a move (via Board.move Piece, handling transposition and promotion). ---
            board.movePiece(rF, cF, rT, cT);

            // Save to database (record move)
            moveDAO.saveMove(turn, movingPiece, rF, cF, rT, cT);

            // The game also records moves within the current session, making it easier to replay only the history of that session.
            @SuppressWarnings("unchecked")
            java.util.List<String[]> sessionMoves = (java.util.List<String[]>) session.getAttribute("gameMoves");
            if (sessionMoves == null) {
                sessionMoves = new java.util.ArrayList<>();
                session.setAttribute("gameMoves", sessionMoves);
                session.setAttribute("moveCounter", 0);
            }
            Integer counter = (Integer) session.getAttribute("moveCounter");
            if (counter == null) counter = 0;
            counter = counter + 1;
            session.setAttribute("moveCounter", counter);
            String[] rec = new String[5];
            rec[0] = String.valueOf(counter); // session-local id
            rec[1] = turn;
            rec[2] = movingPiece;
            rec[3] = rF + "," + cF;
            rec[4] = rT + "," + cT;
            sessionMoves.add(rec);

            // --- 4. results and status =====
            String status = "OK";
            
            // check the king
            if (targetPiece != null && !targetPiece.equals("")) {
                if (targetPiece.equalsIgnoreCase("k")) {
                    status = "GAMEOVER_" + turn;
                    // set status
                    if ("white".equals(turn)) {
                        RoomManager.setGameStatus(roomId, RoomManager.GAME_STATUS_WHITE_WON);
                    } else {
                        RoomManager.setGameStatus(roomId, RoomManager.GAME_STATUS_BLACK_WON);
                    }
                    System.out.println("Game Over! Winner: " + turn);
                }
            }
            
            // ===== unfinished: flaw =====
            // boolean isStalemate = engine.isStalemate(board, nextTurn);
            // if (isStalemate) {
            //     status = "DRAW_STALEMATE";
            //     RoomManager.setGameStatus(roomId, RoomManager.GAME_STATUS_DRAW);
            // }
            // ========================================

            // --- 5. exchange the round (only when the game ends) ---
            if (!RoomManager.isGameOver(roomId)) {
                String nextTurn = "white".equals(turn) ? "black" : "white";
                session.setAttribute("turn", nextTurn);
                RoomManager.setTurn(roomId, nextTurn);
            }

            session.setAttribute("board", board);

            // 同步到 RoomManager
            RoomManager.setBoard(roomId, board);
            RoomManager.setMove(roomId, rF + "," + cF + "-" + rT + "," + cT);

            response.setStatus(200);
            response.getWriter().write(status); // return OK or GAMEOVER

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("Server Error: " + e.getMessage());
        }
    }
}

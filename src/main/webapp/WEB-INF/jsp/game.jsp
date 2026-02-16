<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.entity.Board" %>
<%@ page import="util.PieceUI" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>Quiet Chess</title>
    <style>
        body { 
            background: #1a1a1a; 
            color: #eee; 
            font-family: sans-serif; 
            text-align: center; 
        }
        
        
        .black-view .board {
            transform: rotate(180deg);
        }
        
        
        .black-view .square {
            /*  transform: rotate(180deg); */
        }
        
        .board { 
            display: grid; 
            grid-template-columns: repeat(8, 60px); 
            width: 480px; 
            margin: 40px auto; 
            border: 4px solid #333; 
            box-shadow: 0 10px 20px rgba(0,0,0,0.5);
            transition: transform 0.3s ease;
        }
        
        .square { 
            width: 60px; 
            height: 60px; 
            display: flex; 
            align-items: center; 
            justify-content: center; 
            font-size: 42px; 
            cursor: pointer;
            transition: all 0.2s;
            user-select: none;
        }
        
        .light { background: #f0d9b5; }
        .dark { background: #b58863; }
        .selected { outline: 4px solid #ffd700; outline-offset: -4px; z-index: 10; }
        .info { margin-top: 20px; color: #aaa; }

        
        .white-piece {
            text-shadow: 0 0 4px rgba(255, 255, 255, 0.3);
            color: #ffffff;
        }
        
        .black-piece {
            text-shadow: 0 0 4px rgba(0, 0, 0, 0.3);
            color: #222222;
        }
        
        .square.dark .black-piece {
            text-shadow: 0 0 6px rgba(0, 0, 0, 0.4);
            color: #eeeeee;
        }
        
        .square.light .white-piece {
            text-shadow: 0 0 4px rgba(255, 255, 255, 0.3);
        }

        
        .back-btn {
            background: #444;
            color: #d4af37;
            border: 1px solid #d4af37;
            padding: 8px 20px;
            border-radius: 20px;
            cursor: pointer;
            margin-left: 10px;
            transition: all 0.3s;
            font-size: 14px;
        }
        .back-btn:hover {
            background: #d4af37;
            color: #1a1a1a;
        }

        .menu button {
            background: transparent;
            color: #d4af37;
            border: 1px solid #d4af37;
            padding: 8px 16px;
            margin: 0 5px;
            border-radius: 4px;
            cursor: pointer;
            transition: all 0.3s;
        }

        .menu button:hover {
            background: #d4af37;
            color: #1a1a1a;
        }

        .room-info {
            color: #888;
            font-size: 14px;
            margin-top: 10px;
        }

        .room-id {
            color: #d4af37;
            font-weight: bold;
        }

        
        .view-indicator {
            display: inline-block;
            padding: 2px 8px;
            background: #333;
            border-radius: 12px;
            font-size: 11px;
            color: #aaa;
            margin-left: 8px;
        }
        
        .black-view .view-indicator {
            background: #3a3a3a;
            color: #d4af37;
        }
    </style>
</head>
<body class="<%= session.getAttribute("mySide") != null && (Integer)session.getAttribute("mySide") == 2 ? "black-view" : "" %>">
    <h2>Quiet Chess</h2>
    
    <%
        // ========== get roomId ==========
        String roomId = request.getParameter("roomId");
        if (roomId == null) {
            roomId = (String) session.getAttribute("roomId");
        }
        
        if (roomId == null) {
            response.sendRedirect("lobby");
            return;
        }
        
        com.chess.RoomManager.updateActivity(roomId);
        
        Board b = (Board) session.getAttribute("board");
        String turn = (String) session.getAttribute("turn");

        Integer mySide = (Integer) session.getAttribute("mySide");
        if (mySide == null) {
            mySide = com.chess.RoomManager.joinRoom(roomId);
            session.setAttribute("mySide", mySide);
            session.setAttribute("roomId", roomId);
        }

        if (mySide == 0) {
            response.sendRedirect("lobby?error=full");
            return;
        }

        if (b == null) {
    %>
        <div style="color:red; margin-top:50px;">
            <h3>エラー：セッションが切れました。</h3>
            <p>ブラウザをリロードするか、ゲームにアクセスし直してください。</p>
        </div>
    <%
        } else {
            String[][] grid = b.getGrid();
    %>
    
    <div class="room-info">
        <span>部屋番号: <span class="room-id"><%= roomId %></span></span>
        <span style="margin: 0 15px;">|</span>
        <span>あなた: <%= mySide == 1 ? "先手(白)" : "後手(黒)" %></span>
        <span class="view-indicator">
            <%= mySide == 2 ? "黒側視点" : "白側" %>
        </span>
    </div>

    <div class="board">
        <% 
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    String piece = grid[r][c];
                    String color = (r + c) % 2 == 0 ? "light" : "dark";
                    
                    String pieceColorClass = "";
                    if (piece != null && !piece.isEmpty()) {
                        pieceColorClass = Character.isUpperCase(piece.charAt(0)) ? "white-piece" : "black-piece";
                    }
                    
                    
                    int displayR = r;
                    int displayC = c;
                    if (mySide != null && mySide == 2) {
                        displayR = 7 - r;
                        displayC = 7 - c;
                    }
        %>
            <div class="square <%=color%> <%=pieceColorClass%>" 
                 id="sq-<%=displayR%>-<%=displayC%>" 
                 data-piece="<%= piece %>"
                 data-original-r="<%= r %>"
                 data-original-c="<%= c %>"
                 onclick="handleClick(<%=displayR%>, <%=displayC%>)">
                <%= PieceUI.getUnicode(piece) %>
            </div>
        <% 
                }
            } 
        %>
    </div>
    
    <div style="margin-top: 20px;">
        <div class="info">手番: <%= "white".equals(turn) ? "白" : "黒" %></div>
        
        <div class="menu" style="margin-top: 15px;">
            <button onclick="location.href='game?action=reset&roomId=<%= roomId %>'">NEW GAME</button>
            <button onclick="location.href='game?action=history&roomId=<%= roomId %>'">HISTORY</button>
            <button class="back-btn" onclick="location.href='lobby'">ロビーに戻る</button>
        </div>
    </div>

    <% } %>

    <script>
    const MY_SIDE = <%= mySide %>;
    const ROOM_ID = "<%= roomId %>";
    let lastMoveFromServer = sessionStorage.getItem("lastMove_" + ROOM_ID) || "";
    let firstPos = null;

    function handleClick(displayR, displayC) {
        console.log("Clicked display position:", displayR, displayC);
        
        
        const square = document.getElementById("sq-" + displayR + "-" + displayC);
        const originalR = parseInt(square.getAttribute("data-original-r"));
        const originalC = parseInt(square.getAttribute("data-original-c"));
        
        
        const piece = square.getAttribute("data-piece");

        if (!firstPos) {
            if (!piece || piece === "") return;

            const isWhitePiece = piece[0] === piece[0].toUpperCase();
            if (MY_SIDE === 1 && !isWhitePiece) return;
            if (MY_SIDE === 2 && isWhitePiece) return;
            
            
            firstPos = {r: originalR, c: originalC, displayR: displayR, displayC: displayC};
            square.classList.add('selected');
        } else {
            if (firstPos.r === originalR && firstPos.c === originalC) {
                square.classList.remove('selected');
                firstPos = null;
                return;
            }

            
            const moveStr = firstPos.r + "," + firstPos.c + "-" + originalR + "," + originalC;
            
            const params = new URLSearchParams();
            params.append('fR', firstPos.r);
            params.append('fC', firstPos.c);
            params.append('tR', originalR);
            params.append('tC', originalC);
            params.append('roomId', ROOM_ID);

            fetch('game', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params.toString()
            })
            .then(res => res.text()) 
            .then(status => {
                if (status.includes("GAMEOVER") || status === "OK") {
                    const chessParams = new URLSearchParams();
                    chessParams.append('roomId', ROOM_ID);
                    chessParams.append('move', moveStr);
                    fetch('chessAction', { method: 'POST', body: chessParams })
                    .then(() => {
                        sessionStorage.setItem("lastMove_" + ROOM_ID, moveStr);
                        location.reload();
                    });
                } else {
                    alert(status);
                    location.reload();
                }
            })
            .catch(err => {
                console.error("Fetch Error:", err);
                alert("通信エラー");
            });
        }
    }

    setInterval(function() {
        fetch('chessAction?roomId=' + ROOM_ID)
            .then(res => res.text())
            .then(move => {
                if (move !== "" && move !== lastMoveFromServer) {
                    sessionStorage.setItem("lastMove_" + ROOM_ID, move);
                    location.reload();
                }
            });
        
        fetch('gameStatus?roomId=' + ROOM_ID)
            .then(res => res.text())
            .then(data => {
                const [status, winner] = data.split('|');
                if (status !== 'ACTIVE') {
                    if (!sessionStorage.getItem('gameEnded_' + ROOM_ID)) {
                        let message = '';
                        let isWinner = false;
                        
                        if (status === 'WHITE_WON') {
                            isWinner = (winner === 'white' && MY_SIDE === 1) || 
                                      (winner === 'black' && MY_SIDE === 2);
                            message = isWinner ? 'あなたの勝ちです！' : '白の勝ち';
                        } else if (status === 'BLACK_WON') {
                            isWinner = (winner === 'black' && MY_SIDE === 2) || 
                                      (winner === 'white' && MY_SIDE === 1);
                            message = isWinner ? 'あなたの勝ちです！' : '黒の勝ち';
                        } else if (status === 'DRAW') {
                            message = '引き分け';
                        }
                        
                        alert('対局終了！ ' + message);
                        sessionStorage.setItem('gameEnded_' + ROOM_ID, 'true');
                    }
                    window.gameEnded = true;
                }
            });
    }, 1500);
    </script>
</body>
</html>

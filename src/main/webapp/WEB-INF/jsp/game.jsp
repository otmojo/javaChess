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
        body { background: #1a1a1a; color: #eee; font-family: sans-serif; text-align: center; }
        .board { 
            display: grid; grid-template-columns: repeat(8, 60px); 
            width: 480px; margin: 40px auto; border: 4px solid #333; 
        }
        .square { 
            width: 60px; height: 60px; display: flex; 
            align-items: center; justify-content: center; font-size: 40px; cursor: pointer;
        }
        .light { background: #f0d9b5; color: #333; }
        .dark { background: #b58863; color: #eee; }
        .selected { outline: 4px solid #ffd700; outline-offset: -4px; z-index: 10; }
        .info { margin-top: 20px; color: #aaa; }
    </style>
</head>
<body>
    <h2>Quiet Chess</h2>
    
    <%
        Board b = (Board) session.getAttribute("board");
        String turn = (String) session.getAttribute("turn");

        String roomId = "game1";
        Integer mySide = (Integer) session.getAttribute("mySide");
        if (mySide == null) {
            mySide = com.chess.RoomManager.joinRoom(roomId);
            session.setAttribute("mySide", mySide);
        }

        if (mySide == 0) {
            out.println("<div style='color:red; margin-top:50px;'><h3>申し訳ございませんが、全室満室です。</h3></div>");
            return;
        }

        if (b == null) {
    %>
        <div style="color:red; margin-top:50px;">
            <h3>エラー：セッションが切れました。</h3>
            <p>ブラウザをリロードするか、/game にアクセスし直してください。</p>
        </div>
    <%
        } else {
            String[][] grid = b.getGrid();
    %>
    <div class="board">
        <% 
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    String piece = grid[r][c];
                    String color = (r + c) % 2 == 0 ? "light" : "dark";
                    // Ensure that the ID generation format is sq-r-c
        %>
            <div class="square <%=color%>" 
                 id="sq-<%=r%>-<%=c%>" 
                 data-piece="<%= piece %>"
                 onclick="handleClick(<%=r%>,<%=c%>)">
                <%= PieceUI.getUnicode(piece) %>
            </div>
        <% 
                }
            } 
        %>
    </div>
    <div class="info">手番: <%= "white".equals(turn) ? "白" : "黒" %></div>
    <div class="menu" style="margin-top: 20px;">
    <button onclick="location.href='game?action=reset'">🔄 NEW GAME</button>
    <button onclick="location.href='game?action=history'">📜 HISTORY</button>
</div>

    <% } %>

    <script>
    const MY_SIDE = <%= mySide %>; // 1=white，2=black
    const ROOM_ID = "<%= roomId %>";
    let lastMoveFromServer = sessionStorage.getItem("lastMove") || "";
    let firstPos = null;

    function handleClick(r, c) {
        console.log("Clicked:", r, c);
        const currentId = "sq-" + r + "-" + c;
        const square = document.getElementById(currentId);
        const piece = square.getAttribute("data-piece");

        if (!firstPos) {
            // Step 1: Select the starting point
            if (!piece || piece === "") return;

            // move your pieces only
            const isWhitePiece = piece[0] === piece[0].toUpperCase();
            if (MY_SIDE === 1 && !isWhitePiece) return;
            if (MY_SIDE === 2 && isWhitePiece) return;
            
            firstPos = {r: r, c: c};
            square.classList.add('selected');
        } else {
            // Step 2: Move to the end point
            if (firstPos.r === r && firstPos.c === c) {
                square.classList.remove('selected');
                firstPos = null;
                return;
            }

            const moveStr = firstPos.r + "," + firstPos.c + "-" + r + "," + c;
            
            const params = new URLSearchParams();
            params.append('fR', firstPos.r);
            params.append('fC', firstPos.c);
            params.append('tR', r);
            params.append('tC', c);

            fetch('./game', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params.toString()
            })
            .then(res => res.text()) 
            .then(status => {
                if (status.includes("GAMEOVER") || status === "OK") {
                    // 同步到 RoomManager
                    const chessParams = new URLSearchParams();
                    chessParams.append('roomId', ROOM_ID);
                    chessParams.append('move', moveStr);
                    fetch('chessAction', { method: 'POST', body: chessParams })
                    .then(() => {
                        sessionStorage.setItem("lastMove", moveStr);
                        if (status.includes("GAMEOVER")) {
                            const winner = status.split("_")[1] === "white" ? "白" : "黒";
                            alert("勝負あり！" + winner + "WON！");
                        }
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

    // move in returns
    setInterval(function() {
        fetch('chessAction?roomId=' + ROOM_ID)
            .then(res => res.text())
            .then(move => {
                if (move !== "" && move !== lastMoveFromServer) {
                    sessionStorage.setItem("lastMove", move);
                    location.reload();
                }
            });
    }, 1500);
</script>
</body>
</html>

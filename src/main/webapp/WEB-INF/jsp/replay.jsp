<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.entity.Board" %>
<%@ page import="util.PieceUI" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>Replay - Quiet Chess</title>
    <style>
        body { 
            background: #121212; 
            color: #dcdcdc; 
            font-family: 'Sawarabi Mincho', serif; 
            text-align: center;
            margin: 0;
        }

        h2 {
            color: #d4af37;
            font-weight: 300;
            letter-spacing: 2px;
            margin-top: 20px;
            margin-bottom: 20px;
        }

        .board { 
            display: grid; 
            grid-template-columns: repeat(8, 60px); 
            width: 480px; 
            margin: 20px auto; 
            border: 2px solid #333;
        }

        .square { 
            width: 60px; 
            height: 60px; 
            display: flex; 
            align-items: center; 
            justify-content: center; 
            font-size: 42px; 
        }

        .light { background: #f0d9b5; }
        .dark { background: #b58863; }
        .highlight { outline: 3px solid #ffd700; outline-offset: -3px; }

        .controls { 
            margin-top: 20px;
            margin-bottom: 20px;
        }

        .btn {
            background: transparent;
            color: #d4af37;
            border: 1px solid #d4af37;
            padding: 8px 16px;
            margin: 0 5px;
            border-radius: 4px;
            cursor: pointer;
            font-family: inherit;
            font-size: 0.9em;
            transition: all 0.3s;
        }

        .btn:hover {
            background: #d4af37;
            color: #121212;
        }

        .btn:disabled {
            border-color: #666;
            color: #666;
            cursor: not-allowed;
        }

        #speed {
            margin-left: 10px;
            padding: 4px;
            width: 150px;
            background: #1a1a1a;
            border: 1px solid #333;
            color: #d4af37;
        }

        .info-text {
            margin-top: 15px;
            color: #888;
            font-size: 0.85em;
        }
    </style>
</head>
<body>
    <h2>Replay</h2>

    <%
        // Use a brand new initial board for replay (not dependent on the current session)
        Board b = new Board();
        String[][] grid = b.getGrid();
    %>

    <div id="board" class="board">
        <% for (int r = 0; r < 8; r++) {
               for (int c = 0; c < 8; c++) {
                   String piece = grid[r][c];
                   String color = (r + c) % 2 == 0 ? "light" : "dark";
        %>
            <div class="square <%=color%>" id="sq-<%=r%>-<%=c%>"><%= PieceUI.getUnicode(piece) %></div>
        <%     }
           }
        %>
    </div>

    <div class="controls">
        <button class="btn" id="btnPrev">◀ PREV</button>
        <button class="btn" id="btnPlay">▶ PLAY</button>
        <button class="btn" id="btnPause" disabled>⏸ PAUSE</button>
        <button class="btn" id="btnNext">NEXT ▶</button>
        <button class="btn" id="btnRestart">↺ RESTART</button>
        <button class="btn" id="btnBack" onclick="location.href='<%= request.getContextPath() %>/game'">↩ BACK TO GAME</button>
        <div style="margin-top: 12px; color: #888; font-size: 0.85em;">
            SPEED: <input id="speed" type="range" min="200" max="2000" step="100" value="800">
        </div>
    </div>

    <div class="info-text">共 <span id="total">0</span> 步、当前: <span id="current">0</span></div>

    <script>
        // The basic path to the API provided for static scripting（including context path）
        window.apiBase = '<%= request.getContextPath() %>';
    </script>
    <script src="<%= request.getContextPath() %>/static/js/api.js"></script>
    <script>
        var moves = [];
        var totalEl = document.getElementById('total');
        var currentEl = document.getElementById('current');
        var idx = -1; // default board
        var timer = null;

        // Preferentially use the moveJson injected directly on the server side （from /game?action=history ），otherwise use AJAX
        var serverMoves = <%= request.getAttribute("moveJson") == null ? "null" : request.getAttribute("moveJson") %>;
        if (serverMoves) {
            try {
                moves = serverMoves;
            } catch (e) {
                console.error('Failed to parse the moveJson injected by the server', e);
                moves = [];
            }
            totalEl.innerText = moves.length;
        } else {
            // using API for movement records
            window.api.fetchMoves()
            .then(function(data){
                moves = data || [];
                totalEl.innerText = moves.length;
            })
            .catch(function(err){
                console.error('Failed to get playback data', err);
                moves = [];
                totalEl.innerText = 0;
            });
        }

        function applyMove(i) {
            // i is the step sequence number, starting with 0
            if (i < 0 || i >= moves.length) return;
            var m = moves[i];
            var from = m.from; // [r,c]
            var to = m.to;
            var src = document.getElementById('sq-' + from[0] + '-' + from[1]);
            var dst = document.getElementById('sq-' + to[0] + '-' + to[1]);
            if (!src || !dst) return;
            // Remove highlights
            document.querySelectorAll('.square').forEach(s=>s.classList.remove('highlight'));
            // move a piece from src to dst (copy characters directly)
            dst.innerText = src.innerText;
            src.innerText = '';
            dst.classList.add('highlight');
            idx = i;
            currentEl.innerText = idx+1;
        }

        function undoMove(i) {
            // Return to the state before the ith move: The easiest way is to reset the board and replay the previous i
            resetBoard();
            for (var k = 0; k < i; k++) applyMove(k);
        }

        function resetBoard() {
            
            location.reload();
        }

        // take control of the buttons
        document.getElementById('btnPlay').addEventListener('click', function(){
            document.getElementById('btnPlay').disabled = true;
            document.getElementById('btnPause').disabled = false;
            var speed = parseInt(document.getElementById('speed').value, 10);
            if (timer) clearInterval(timer);
            timer = setInterval(function(){
                if (idx+1 < moves.length) {
                    applyMove(idx+1);
                } else {
                    clearInterval(timer);
                    document.getElementById('btnPlay').disabled = false;
                    document.getElementById('btnPause').disabled = true;
                }
            }, speed);
        });

        document.getElementById('btnPause').addEventListener('click', function(){
            if (timer) clearInterval(timer);
            timer = null;
            document.getElementById('btnPlay').disabled = false;
            document.getElementById('btnPause').disabled = true;
        });

        document.getElementById('btnNext').addEventListener('click', function(){
            if (idx+1 < moves.length) applyMove(idx+1);
        });

        document.getElementById('btnPrev').addEventListener('click', function(){
            if (idx <= 0) {
                resetBoard();
            } else {
                undoMove(idx);
            }
        });

        document.getElementById('btnRestart').addEventListener('click', function(){
            resetBoard();
        });

        // Speed slider changes take effect immediately (if playing, restart timer)
        document.getElementById('speed').addEventListener('change', function(){
            if (timer) {
                clearInterval(timer);
                document.getElementById('btnPlay').click();
            }
        });
    </script>
</body>
</html>

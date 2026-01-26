<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.entity.Board" %>
<%@ page import="util.PieceUI" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>Replay - Quiet Chess</title>
    <style>
        body { background: #111; color: #eee; font-family: sans-serif; text-align: center; }
        .board { display: grid; grid-template-columns: repeat(8, 60px); width: 480px; margin: 24px auto; }
        .square { width:60px; height:60px; display:flex; align-items:center; justify-content:center; font-size:40px; }
        .light { background: #f0d9b5; color: #333; }
        .dark { background: #b58863; color: #111; }
        .controls { margin-top:12px; }
        .highlight { outline: 3px solid #00ffff; outline-offset: -3px; }
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
        <button id="btnPrev">◀ PREV</button>
        <button id="btnPlay">▶ PLAY</button>
        <button id="btnPause" disabled>⏸ PAUSE</button>
        <button id="btnNext">NEXT ▶</button>
        <button id="btnRestart">↺ RESTART</button>
        <button id="btnBack" onclick="location.href='<%= request.getContextPath() %>/game'">↩ BACK TO GAME</button>
        SPEED: <input id="speed" type="range" min="200" max="2000" step="100" value="800">
    </div>

    <div style="margin-top:10px; color:#bbb; font-size:14px;">共 <span id="total">0</span> 步，当前步: <span id="current">0</span></div>

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

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.chess.RoomManager" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>Quiet Chess - ロビー</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            background-color: #121212;
            color: #dcdcdc;
            font-family: 'Sawarabi Mincho', serif;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }

        .container {
            max-width: 900px;
            width: 100%;
            background: transparent;
            padding: 40px;
            text-align: center;
        }

        h1 {
            font-size: 2.5em;
            color: #d4af37;
            text-align: center;
            margin-bottom: 10px;
            letter-spacing: 4px;
            font-weight: 300;
        }

        .subtitle {
            text-align: center;
            color: #888;
            margin-bottom: 40px;
            font-size: 0.95em;
        }

        .room-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 20px;
            margin: 30px 0;
        }

        .room-card {
            background: #1a1a1a;
            border-radius: 8px;
            padding: 20px;
            transition: all 0.3s;
            border: 1px solid #333;
            position: relative;
            overflow: hidden;
        }

        .room-card:hover {
            border-color: #d4af37;
        }

        .room-card.waiting {
            border-left: 4px solid #4caf50;
        }

        .room-card.playing {
            border-left: 4px solid #f44336;
        }

        .room-card.empty {
            border-left: 4px solid #9e9e9e;
            opacity: 0.7;
        }

        .room-id {
            font-size: 1.2em;
            color: #d4af37;
            margin-bottom: 12px;
            font-weight: 500;
        }

        .room-info {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 16px;
            color: #aaa;
            font-size: 0.9em;
        }

        .player-count {
            background: #333;
            padding: 4px 12px;
            border-radius: 4px;
            color: #fff;
            font-size: 0.85em;
        }

        .status-badge {
            padding: 4px 12px;
            border-radius: 4px;
            font-size: 0.8em;
            font-weight: 500;
        }

        .status-waiting {
            background: #4caf50;
            color: white;
        }

        .status-playing {
            background: #f44336;
            color: white;
        }

        .status-empty {
            background: #9e9e9e;
            color: white;
        }

        .btn {
            background: transparent;
            border: 1px solid #d4af37;
            color: #d4af37;
            padding: 10px 20px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 0.95em;
            transition: all 0.3s;
            width: 100%;
            font-family: inherit;
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

        .btn-create {
            background: transparent;
            color: #d4af37;
            border: 1px solid #d4af37;
            padding: 12px 32px;
            font-size: 1em;
            width: auto;
            display: inline-block;
            margin: 20px auto;
            border-radius: 4px;
            cursor: pointer;
            transition: all 0.3s;
            font-family: inherit;
        }

        .btn-create:hover {
            background: #d4af37;
            color: #121212;
        }

        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #666;
            background: #1a1a1a;
            border-radius: 8px;
        }

        .refresh-btn {
            position: fixed;
            bottom: 30px;
            right: 30px;
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: #d4af37;
            border: none;
            color: #121212;
            font-size: 24px;
            cursor: pointer;
            box-shadow: 0 4px 12px rgba(212, 175, 55, 0.3);
            transition: all 0.3s;
            z-index: 100;
        }

        .refresh-btn:hover {
            transform: rotate(180deg);
            background: #e5c158;
        }

        .info-text {
            text-align: center;
            margin-top: 30px;
            color: #666;
            font-size: 0.85em;
        }

        .header-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .header-actions .btn {
            width: auto;
            padding: 8px 20px;
        }

        @media (max-width: 600px) {
            .container {
                padding: 20px;
            }
            
            h1 {
                font-size: 2em;
            }
            
            .room-grid {
                grid-template-columns: 1fr;
            }

            .refresh-btn {
                bottom: 20px;
                right: 20px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Quiet Chess</h1>
        <div class="subtitle">静寂の中で、自分の思考を打つ、相手の予想を打つ。</div>
        
        <div class="header-actions">
            <button class="btn" onclick="location.reload()">更新</button>
            <button class="btn btn-create" onclick="createRoom()" style="width: auto;">新しい部屋を作る</button>
        </div>
        
        <%
            // Clean inactive rooms (5 minutes)
            RoomManager.cleanupInactiveRooms(5);
            
            // Get all rooms
            Map<String, Object> rooms = RoomManager.getAllRoomsInfo();
        %>
        
        <% if (rooms.isEmpty()) { %>
            <div class="empty-state">
                <div style="font-size: 1.2em; margin-bottom: 10px;">部屋がありません</div>
                <div style="color: #888; margin-bottom: 30px;">「新しい部屋を作る」をクリックして開始してください</div>
                <button class="btn btn-create" onclick="createRoom()">新しい部屋を作る</button>
            </div>
        <% } else { %>
            <div class="room-grid">
                <% for (Map.Entry<String, Object> entry : rooms.entrySet()) { 
                    String roomId = entry.getKey();
                    Map<String, Object> info = (Map<String, Object>) entry.getValue();
                    int playerCount = (int) info.get("playerCount");
                    String status = (String) info.get("status");
                    
                    String statusText = "";
                    String statusClass = "";
                    String cardClass = "";
                    
                    if (playerCount == 0) {
                        statusText = "空き";
                        statusClass = "status-empty";
                        cardClass = "empty";
                    } else if (playerCount == 1) {
                        statusText = "待機中";
                        statusClass = "status-waiting";
                        cardClass = "waiting";
                    } else {
                        statusText = "対局中";
                        statusClass = "status-playing";
                        cardClass = "playing";
                    }
                %>
                <div class="room-card <%= cardClass %>">
                    <div class="room-id"><%= roomId %></div>
                    <div class="room-info">
                        <span class="player-count"><%= playerCount %>/2</span>
                        <span class="status-badge <%= statusClass %>"><%= statusText %></span>
                    </div>
                    <% if (!"ACTIVE".equals(status)) { %>
                        <div style="color: #888; font-size: 0.8em; margin-bottom: 10px;">対局終了</div>
                    <% } %>
                    
                    <% if (playerCount < 2) { %>
                        <button class="btn" onclick="joinRoom('<%= roomId %>')">この部屋に入る</button>
                    <% } else { %>
                        <button class="btn" disabled>満室</button>
                    <% } %>
                </div>
                <% } %>
            </div>
        <% } %>
        
        <div class="info-text">
            5分以上アクティブがない部屋は自動的に削除されます
        </div>
    </div>

    <button class="refresh-btn" onclick="location.reload()">↻</button>

    <script>
        function joinRoom(roomId) {
            const btn = event.target;
            btn.textContent = '入室中...';
            btn.disabled = true;
            
            fetch('<%= request.getContextPath() %>/joinRoom?roomId=' + roomId)
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        window.location.href = '<%= request.getContextPath() %>/game?roomId=' + roomId;
                    } else {
                        alert('入室失敗：' + data.message);
                        location.reload();
                    }
                })
                .catch(err => {
                    alert('通信エラー、再試行してください');
                    location.reload();
                });
        }
        
        function createRoom() {
            const btn = event.target;
            btn.textContent = '作成中...';
            btn.disabled = true;
            
            fetch('<%= request.getContextPath() %>/createRoom')
                .then(res => res.json())
                .then(data => {
                    window.location.href = '<%= request.getContextPath() %>/game?roomId=' + data.roomId;
                })
                .catch(err => {
                    alert('部屋の作成に失敗しました');
                    location.reload();
                });
        }
        
        // Auto refresh every 30 seconds
        setTimeout(function() {
            location.reload();
        }, 30000);
    </script>
</body>
</html>

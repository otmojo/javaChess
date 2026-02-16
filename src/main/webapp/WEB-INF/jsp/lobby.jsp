<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.chess.RoomManager" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>Quiet Chess - ロビー</title>
    <style>
        body {
            background: #1a1a1a;
            color: #eee;
            font-family: sans-serif;
            text-align: center;
            padding: 40px;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background: #2a2a2a;
            border-radius: 12px;
            padding: 30px;
            box-shadow: 0 0 20px rgba(0,0,0,0.5);
        }
        h1 {
            color: #ffd700;
            margin-bottom: 30px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
        }
        th {
            background: #333;
            color: #ffd700;
            padding: 12px;
            font-weight: normal;
        }
        td {
            padding: 12px;
            border-bottom: 1px solid #444;
        }
        tr:hover {
            background: #333;
        }
        .room-status {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 14px;
        }
        .status-waiting {
            background: #2ecc71;
            color: #fff;
        }
        .status-playing {
            background: #e74c3c;
            color: #fff;
        }
        .status-empty {
            background: #7f8c8d;
            color: #fff;
        }
        .btn {
            background: #ffd700;
            color: #1a1a1a;
            border: none;
            padding: 8px 20px;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-block;
        }
        .btn:hover {
            background: #ffed4a;
            transform: translateY(-2px);
        }
        .btn:disabled {
            background: #666;
            cursor: not-allowed;
            transform: none;
        }
        .btn-create {
            background: #3498db;
            color: white;
            font-size: 16px;
            padding: 12px 30px;
            margin-top: 20px;
        }
        .btn-create:hover {
            background: #2980b9;
        }
        .info-text {
            color: #aaa;
            font-size: 14px;
            margin: 10px 0;
        }
        .refresh-btn {
            background: transparent;
            border: 1px solid #ffd700;
            color: #ffd700;
            padding: 5px 15px;
            border-radius: 4px;
            cursor: pointer;
            margin-left: 10px;
        }
        .refresh-btn:hover {
            background: #ffd700;
            color: #1a1a1a;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>♟️ Quiet Chess ロビー</h1>
        
        <div style="text-align: right; margin-bottom: 10px;">
            <button class="refresh-btn" onclick="location.reload()">🔄 更新</button>
        </div>
        
        <%
            // Clean inactive rooms (5 minutes)
            RoomManager.cleanupInactiveRooms(5);
            
            // Get all rooms
            Map<String, Object> rooms = RoomManager.getAllRoomsInfo();
        %>
        
        <table>
            <tr>
                <th>部屋番号</th>
                <th>人数</th>
                <th>状態</th>
                <th>操作</th>
            </tr>
            
            <% if (rooms.isEmpty()) { %>
                <tr>
                    <td colspan="4" style="text-align: center; padding: 40px; color: #666;">
                        部屋がありません。「新しい部屋を作る」をクリックしてください
                    </td>
                </tr>
            <% } else { %>
                <% for (Map.Entry<String, Object> entry : rooms.entrySet()) { 
                    String roomId = entry.getKey();
                    Map<String, Object> info = (Map<String, Object>) entry.getValue();
                    int playerCount = (int) info.get("playerCount");
                    String status = (String) info.get("status");
                    
                    String statusText = "";
                    String statusClass = "";
                    if (playerCount == 0) {
                        statusText = "空き";
                        statusClass = "status-empty";
                    } else if (playerCount == 1) {
                        statusText = "待機中";
                        statusClass = "status-waiting";
                    } else {
                        statusText = "対局中";
                        statusClass = "status-playing";
                    }
                %>
                <tr>
                    <td><%= roomId %></td>
                    <td><%= playerCount %>/2</td>
                    <td>
                        <span class="room-status <%= statusClass %>"><%= statusText %></span>
                        <% if (!"ACTIVE".equals(status)) { %>
                            <span style="margin-left: 5px; font-size: 12px;">(終了)</span>
                        <% } %>
                    </td>
                    <td>
                        <% if (playerCount < 2) { %>
                            <button class="btn" onclick="joinRoom('<%= roomId %>')">🔰 入室</button>
                        <% } else { %>
                            <button class="btn" disabled>⛔ 満室</button>
                        <% } %>
                    </td>
                </tr>
                <% } %>
            <% } %>
        </table>
        
        <button class="btn btn-create" onclick="createRoom()">➕ 新しい部屋を作る</button>
        
        <div class="info-text">
            ⏰ 5分以上アクティブがない部屋は自動的に削除されます
        </div>
    </div>

    <script>
        function joinRoom(roomId) {
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
                });
        }
        
        function createRoom() {
            fetch('<%= request.getContextPath() %>/createRoom')
                .then(res => res.json())
                .then(data => {
                    window.location.href = '<%= request.getContextPath() %>/game?roomId=' + data.roomId;
                })
                .catch(err => {
                    alert('部屋の作成に失敗しました');
                });
        }
        
        // Auto refresh every 30 seconds
        setTimeout(function() {
            location.reload();
        }, 30000);
    </script>
</body>
</html>

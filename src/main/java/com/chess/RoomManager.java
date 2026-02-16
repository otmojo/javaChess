package com.chess;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.HashMap;
import model.entity.Board;

public class RoomManager {
    // Store latest moves. Key: roomId, Value: "e2-e4"
    private static final ConcurrentHashMap<String, String> latestMoves = new ConcurrentHashMap<>();
    // Store player counts. Key: roomId, Value: 0, 1, or 2
    private static final ConcurrentHashMap<String, AtomicInteger> playerCounts = new ConcurrentHashMap<>();
    // Store board objects
    private static final ConcurrentHashMap<String, Board> boards = new ConcurrentHashMap<>();
    // Store current turn
    private static final ConcurrentHashMap<String, String> turns = new ConcurrentHashMap<>();

    // Game status constants
    public static final String GAME_STATUS_ACTIVE = "ACTIVE";
    public static final String GAME_STATUS_WHITE_WON = "WHITE_WON";
    public static final String GAME_STATUS_BLACK_WON = "BLACK_WON";
    public static final String GAME_STATUS_DRAW = "DRAW";
    
    // Store game status
    private static final ConcurrentHashMap<String, String> gameStatus = new ConcurrentHashMap<>();
    
    // ========== NEW: Room list management ==========
    // Store last active time for cleanup
    private static final ConcurrentHashMap<String, Long> lastActiveTime = new ConcurrentHashMap<>();
    
    /**
     * Get all rooms information
     */
    public static Map<String, Object> getAllRoomsInfo() {
        Map<String, Object> rooms = new HashMap<>();
        
        for (String roomId : playerCounts.keySet()) {
            Map<String, Object> info = new HashMap<>();
            AtomicInteger count = playerCounts.get(roomId);
            info.put("playerCount", count.get());
            info.put("status", gameStatus.getOrDefault(roomId, GAME_STATUS_ACTIVE));
            info.put("lastActive", lastActiveTime.getOrDefault(roomId, 0L));
            info.put("hasBoard", boards.containsKey(roomId));
            
            rooms.put(roomId, info);
        }
        
        return rooms;
    }
    
    /**
     * Create new room
     */
    public static synchronized String createRoom() {
        String roomId = "room_" + System.currentTimeMillis();
        playerCounts.put(roomId, new AtomicInteger(0));
        gameStatus.put(roomId, GAME_STATUS_ACTIVE);
        turns.put(roomId, "white");
        lastActiveTime.put(roomId, System.currentTimeMillis());
        
        System.out.println("🆕 Created new room: " + roomId);
        return roomId;
    }
    
    /**
     * Get available room (waiting room)
     */
    public static synchronized String getAvailableRoom() {
        // Find waiting room first (1 player)
        for (String roomId : playerCounts.keySet()) {
            AtomicInteger count = playerCounts.get(roomId);
            if (count.get() == 1) {
                return roomId;
            }
        }
        
        // No waiting room, create new one
        return createRoom();
    }
    
    /**
     * Update room activity time
     */
    public static void updateActivity(String roomId) {
        if (roomId != null) {
            lastActiveTime.put(roomId, System.currentTimeMillis());
        }
    }
    
    /**
     * Clean up inactive rooms
     * @param maxInactiveMinutes Maximum inactive minutes
     */
    public static void cleanupInactiveRooms(int maxInactiveMinutes) {
        long now = System.currentTimeMillis();
        long maxInactiveMillis = maxInactiveMinutes * 60 * 1000;
        
        System.out.println("🧹 Cleaning inactive rooms...");
        int cleaned = 0;
        
        for (String roomId : playerCounts.keySet()) {
            Long lastActive = lastActiveTime.get(roomId);
            
            // If no activity record or exceeds max inactive time
            if (lastActive == null || (now - lastActive) > maxInactiveMillis) {
                AtomicInteger count = playerCounts.get(roomId);
                
                // Clean empty rooms or long inactive rooms
                if (count == null || count.get() == 0) {
                    cleanupRoom(roomId);
                    cleaned++;
                    System.out.println("  Cleaned room: " + roomId);
                }
            }
        }
        
        System.out.println("🧹 Cleanup completed, " + cleaned + " rooms cleaned");
    }
    
    // ===============================================

    public static Board getBoard(String roomId) {
        updateActivity(roomId);
        return boards.get(roomId);
    }

    public static void setBoard(String roomId, Board board) {
        boards.put(roomId, board);
        updateActivity(roomId);
    }

    public static String getTurn(String roomId) {
        updateActivity(roomId);
        return turns.getOrDefault(roomId, "white");
    }

    public static void setTurn(String roomId, String turn) {
        turns.put(roomId, turn);
        updateActivity(roomId);
    }

    // Try to join room, returns: 1(white), 2(black), 0(room full)
    public static synchronized int joinRoom(String roomId) {
        playerCounts.putIfAbsent(roomId, new AtomicInteger(0));
        AtomicInteger count = playerCounts.get(roomId);
        if (count.get() < 2) {
            int side = count.incrementAndGet();
            
            // Initialize game status when first player joins
            if (side == 1) {
                gameStatus.putIfAbsent(roomId, GAME_STATUS_ACTIVE);
            }
            
            updateActivity(roomId);
            System.out.println("👤 Player joined room " + roomId + ", now " + count.get() + " players");
            return side;
        }
        return 0; // Room full
    }

    public static void setMove(String roomId, String move) {
        latestMoves.put(roomId, move);
        updateActivity(roomId);
    }

    public static String getMove(String roomId) {
        updateActivity(roomId);
        return latestMoves.getOrDefault(roomId, "");
    }
    
    /**
     * Get current game status
     */
    public static String getGameStatus(String roomId) {
        return gameStatus.getOrDefault(roomId, GAME_STATUS_ACTIVE);
    }
    
    /**
     * Set game status
     */
    public static void setGameStatus(String roomId, String status) {
        gameStatus.put(roomId, status);
        updateActivity(roomId);
    }
    
    /**
     * Check if game is over
     */
    public static boolean isGameOver(String roomId) {
        String status = getGameStatus(roomId);
        return !GAME_STATUS_ACTIVE.equals(status);
    }
    
    /**
     * Get winner
     */
    public static String getWinner(String roomId) {
        String status = getGameStatus(roomId);
        if (GAME_STATUS_WHITE_WON.equals(status)) {
            return "white";
        } else if (GAME_STATUS_BLACK_WON.equals(status)) {
            return "black";
        }
        return null;
    }
    
    /**
     * Reset game status for new game
     */
    public static void resetGameStatus(String roomId) {
        gameStatus.put(roomId, GAME_STATUS_ACTIVE);
        updateActivity(roomId);
    }
    
    /**
     * Clean up room data (called when players leave)
     */
    public static void cleanupRoom(String roomId) {
        latestMoves.remove(roomId);
        playerCounts.remove(roomId);
        boards.remove(roomId);
        turns.remove(roomId);
        gameStatus.remove(roomId);
        lastActiveTime.remove(roomId);
    }
}

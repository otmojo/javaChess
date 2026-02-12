package com.chess;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import model.entity.Board;

public class RoomManager {
    // 存储房间最新棋步。Key: roomId, Value: "e2-e4"
    private static final ConcurrentHashMap<String, String> latestMoves = new ConcurrentHashMap<>();
    // 存储房间当前人数。Key: roomId, Value: 0, 1, 或 2
    private static final ConcurrentHashMap<String, AtomicInteger> playerCounts = new ConcurrentHashMap<>();
    // 存储房间共享的棋盘对象
    private static final ConcurrentHashMap<String, Board> boards = new ConcurrentHashMap<>();
    // 存储房间当前回合
    private static final ConcurrentHashMap<String, String> turns = new ConcurrentHashMap<>();

    
    // 游戏状态常量
    public static final String GAME_STATUS_ACTIVE = "ACTIVE";
    public static final String GAME_STATUS_WHITE_WON = "WHITE_WON";
    public static final String GAME_STATUS_BLACK_WON = "BLACK_WON";
    public static final String GAME_STATUS_DRAW = "DRAW";
    
    // 存储房间游戏状态
    private static final ConcurrentHashMap<String, String> gameStatus = new ConcurrentHashMap<>();
    // ========================================

    public static Board getBoard(String roomId) {
        return boards.get(roomId);
    }

    public static void setBoard(String roomId, Board board) {
        boards.put(roomId, board);
    }

    public static String getTurn(String roomId) {
        return turns.getOrDefault(roomId, "white");
    }

    public static void setTurn(String roomId, String turn) {
        turns.put(roomId, turn);
    }

    // 尝试加入房间，返回分配的颜色：1(白), 2(黑), 0(房间满)
    public static synchronized int joinRoom(String roomId) {
        playerCounts.putIfAbsent(roomId, new AtomicInteger(0));
        AtomicInteger count = playerCounts.get(roomId);
        if (count.get() < 2) {
            int side = count.incrementAndGet();
            
            // ===== 新增：第一个玩家加入时初始化游戏状态 =====
            if (side == 1) {
                gameStatus.putIfAbsent(roomId, GAME_STATUS_ACTIVE);
            }
            // ==========================================
            
            return side; // 返回 1 或 2
        }
        return 0; // 房间已满
    }

    public static void setMove(String roomId, String move) {
        latestMoves.put(roomId, move);
    }

    public static String getMove(String roomId) {
        return latestMoves.getOrDefault(roomId, "");
    }
    
    /**
     * 获取房间当前游戏状态
     * @param roomId 房间ID
     * @return 游戏状态常量
     */
    public static String getGameStatus(String roomId) {
        return gameStatus.getOrDefault(roomId, GAME_STATUS_ACTIVE);
    }
    
    /**
     * 设置房间游戏状态
     * @param roomId 房间ID
     * @param status 游戏状态常量
     */
    public static void setGameStatus(String roomId, String status) {
        gameStatus.put(roomId, status);
    }
    
    /**
     * 检查游戏是否已结束
     * @param roomId 房间ID
     * @return true: 游戏已结束, false: 游戏进行中
     */
    public static boolean isGameOver(String roomId) {
        String status = getGameStatus(roomId);
        return !GAME_STATUS_ACTIVE.equals(status);
    }
    
    /**
     * 获取获胜方
     * @param roomId 房间ID
     * @return "white", "black", 或 null（无获胜方/游戏未结束）
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
     * 重置房间游戏状态（用于新对局）
     * @param roomId 房间ID
     */
    public static void resetGameStatus(String roomId) {
        gameStatus.put(roomId, GAME_STATUS_ACTIVE);
    }
    
    /**
     * 清理房间所有数据（玩家离开时调用）
     * @param roomId 房间ID
     */
    public static void cleanupRoom(String roomId) {
        latestMoves.remove(roomId);
        playerCounts.remove(roomId);
        boards.remove(roomId);
        turns.remove(roomId);
        gameStatus.remove(roomId);
    }
    // ==============================================
}

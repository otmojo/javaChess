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
            return count.incrementAndGet(); // 返回 1 或 2
        }
        return 0; // 房间已满
    }

    public static void setMove(String roomId, String move) {
        latestMoves.put(roomId, move);
    }

    public static String getMove(String roomId) {
        return latestMoves.getOrDefault(roomId, "");
    }
}

package model.entity;

public class Move {
    private int moveNumber;
    private String fromPos;
    private String toPos;
    private int reflectionLevel;
    
    // Legacy fields for time tracking (optional, keeping for compatibility if needed)
    private long startTime;
    private long endTime;
    private String thoughtLog;

    public Move(String from, String to, int level) {
        this.fromPos = from;
        this.toPos = to;
        this.reflectionLevel = level;
    }

    public Move(String from, String to, long start) {
        this.fromPos = from;
        this.toPos = to;
        this.startTime = start;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public String getFromPos() {
        return fromPos;
    }

    public String getToPos() {
        return toPos;
    }

    public int getReflectionLevel() {
        return reflectionLevel;
    }

    public void setReflectionLevel(int reflectionLevel) {
        this.reflectionLevel = reflectionLevel;
    }
    
    // Legacy methods
    public void complete(long end, String log) {
        this.endTime = end;
        this.thoughtLog = log;
    }
}

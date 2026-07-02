package util;

public class PieceUI {
    public static String getUnicode(String p) {
        if (p == null || p.trim().isEmpty()) return "";
        switch(p) {
            // white
            case "K": return "&#9812;"; // ♔
            case "Q": return "&#9813;"; // ♕
            case "R": return "&#9814;"; // ♖
            case "B": return "&#9815;"; // ♗
            case "N": return "&#9816;"; // ♘
            case "P": return "&#9817;"; // ♙
            // black
            case "k": return "&#9818;"; // ♚
            case "q": return "&#9819;"; // ♛
            case "r": return "&#9820;"; // ♜
            case "b": return "&#9821;"; // ♝
            case "n": return "&#9822;"; // ♞
            case "p": return "&#9823;"; // ♟
            default: return "";
        }
    }
}
package util;

public class PieceUI {
    public static String getUnicode(String p) {
        if (p == null || p.trim().isEmpty()) return "";
        
        // use Emoji 
        switch(p) {
            // white
            case "K": return "♔";
            case "Q": return "♕";
            case "R": return "♖";
            case "B": return "♗";
            case "N": return "♘";
            case "P": return "♙";
            // black
            case "k": return "♚";
            case "q": return "♛";
            case "r": return "♜";
            case "b": return "♝";
            case "n": return "♞";
            case "p": return "♟";
            default: return "";
        }
    }
}

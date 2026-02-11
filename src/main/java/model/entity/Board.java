package model.entity;

/**
 * 8x8 board
 */
public class Board {
    // binary expression：WHITE=BIG CAPITAL，BLACK-SMALL CAPITAL。R=Rook, N=Knight, B=Bishop, Q=Queen, K=King, P=Pawn
    private String[][] grid;
    // moved flags for castling
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean whiteRookAMoved = false; // a-file rook (queen-side)
    private boolean whiteRookHMoved = false; // h-file rook (king-side)
    private boolean blackRookAMoved = false;
    private boolean blackRookHMoved = false;

    public Board() {
        grid = new String[8][8];
        setupStandardBoard();
    }

    private void setupStandardBoard() {
        // black
        grid[0] = new String[]{"r", "n", "b", "q", "k", "b", "n", "r"};
        grid[1] = new String[]{"p", "p", "p", "p", "p", "p", "p", "p"};

        // board
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) grid[i][j] = "";
        }

        // white
        grid[6] = new String[]{"P", "P", "P", "P", "P", "P", "P", "P"};
        grid[7] = new String[]{"R", "N", "B", "Q", "K", "B", "N", "R"};
    }

    public String[][] getGrid() { return grid; }
    
    // Getters for moved flags (used by RuleEngine)
    public boolean isWhiteKingMoved() { return whiteKingMoved; }
    public boolean isBlackKingMoved() { return blackKingMoved; }
    public boolean isWhiteRookAMoved() { return whiteRookAMoved; }
    public boolean isWhiteRookHMoved() { return whiteRookHMoved; }
    public boolean isBlackRookAMoved() { return blackRookAMoved; }
    public boolean isBlackRookHMoved() { return blackRookHMoved; }

    /**
     * Move a piece on the board, handling special cases: castling and pawn promotion.
     * This updates moved flags for king/rooks as needed.
     */
    public void movePiece(int fRow, int fCol, int tRow, int tCol) {
        String piece = grid[fRow][fCol];
        if (piece == null || piece.equals("")) return;

        char p = piece.charAt(0);
        boolean isWhite = Character.isUpperCase(p);

        // Handle castling: king moves two squares horizontally
        if (Character.toLowerCase(p) == 'k' && fRow == tRow && Math.abs(tCol - fCol) == 2) {
            // King-side or queen-side
            if (isWhite) whiteKingMoved = true; else blackKingMoved = true;

            if (tCol > fCol) {
                // king-side: rook from h-file to f-file
                int rookRow = fRow;
                int rookFromCol = 7;
                int rookToCol = fCol + 1;
                grid[tRow][tCol] = grid[fRow][fCol];
                grid[fRow][fCol] = "";
                grid[rookRow][rookToCol] = grid[rookRow][rookFromCol];
                grid[rookRow][rookFromCol] = "";
                if (isWhite) whiteRookHMoved = true; else blackRookHMoved = true;
                return;
            } else {
                // queen-side: rook from a-file to d-file
                int rookRow = fRow;
                int rookFromCol = 0;
                int rookToCol = fCol - 1;
                grid[tRow][tCol] = grid[fRow][fCol];
                grid[fRow][fCol] = "";
                grid[rookRow][rookToCol] = grid[rookRow][rookFromCol];
                grid[rookRow][rookFromCol] = "";
                if (isWhite) whiteRookAMoved = true; else blackRookAMoved = true;
                return;
            }
        }

        // Normal move: move piece and clear source
        grid[tRow][tCol] = grid[fRow][fCol];
        grid[fRow][fCol] = "";

        // Update moved flags for king and rooks
        if (Character.toLowerCase(p) == 'k') {
            if (isWhite) whiteKingMoved = true; else blackKingMoved = true;
        }
        if (Character.toLowerCase(p) == 'r') {
            // If rook moved from original squares, mark corresponding flag
            if (isWhite) {
                if (fRow == 7 && fCol == 0) whiteRookAMoved = true;
                if (fRow == 7 && fCol == 7) whiteRookHMoved = true;
            } else {
                if (fRow == 0 && fCol == 0) blackRookAMoved = true;
                if (fRow == 0 && fCol == 7) blackRookHMoved = true;
            }
        }

        // Pawn promotion: auto-promote to queen
        if (Character.toLowerCase(p) == 'p') {
            if (isWhite && tRow == 0) {
                grid[tRow][tCol] = "Q";
            } else if (!isWhite && tRow == 7) {
                grid[tRow][tCol] = "q";
            }
        }
    }
}

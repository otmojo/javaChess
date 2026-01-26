package model.logic;

import model.entity.Board;

public class RuleEngine {

    /**
     *  checking if the movement is valid or not
     * @param board the current statement of the board
     * @param fRow the row when it starts, fCol the col when it starts
     * @param tRow  the row when it ends, tCol  the col when it ends
     * @param turn  ("white" or "black") current turn
     * @return boolean  allowed to move or not?
     */
    public boolean isLegalMove(Board board, int fRow, int fCol, int tRow, int tCol, String turn) {
        String[][] grid = board.getGrid();
        String piece = grid[fRow][fCol];

        // 1. Basic check: The starting point cannot be empty
        if (piece == null || piece.equals("")) return false;

        // 2. Basic check: dont eat yourself
        String targetPiece = grid[tRow][tCol];
        if (!targetPiece.equals("")) {
            boolean isWhiteAttacker = Character.isUpperCase(piece.charAt(0));
            boolean isWhiteTarget = Character.isUpperCase(targetPiece.charAt(0));
            if (isWhiteAttacker == isWhiteTarget) return false;
        }

        // 3. different pieces
        char type = Character.toLowerCase(piece.charAt(0));
        switch (type) {
            case 'n': // knight
                return validateKnight(fRow, fCol, tRow, tCol);
            case 'r': // rook
                return validateRook(fRow, fCol, tRow, tCol, grid);
            case 'b': // bishop
                return validateBishop(fRow, fCol, tRow, tCol, grid);
            case 'q': // queen
                return validateQueen(fRow, fCol, tRow, tCol, grid);
            case 'p': // pawn
                return validatePawn(fRow, fCol, tRow, tCol, piece, grid);
            case 'k': // king(including translocation)
                return validateKing(board, fRow, fCol, tRow, tCol);
        }

        return false;
    }

    private boolean validateKnight(int fRow, int fCol, int tRow, int tCol) {
        int rowDiff = Math.abs(fRow - tRow);
        int colDiff = Math.abs(fCol - tCol);
        // knight
        return (rowDiff == 1 && colDiff == 2) || (rowDiff == 2 && colDiff == 1);
    }

    // --- no obstacle ---
    private boolean validateRook(int fRow, int fCol, int tRow, int tCol, String[][] grid) {
        // rook in a straight line: the same row or column with no other pieces on the path
        if (fRow != tRow && fCol != tCol) return false;
        return isPathClear(grid, fRow, fCol, tRow, tCol);
    }

    private boolean validateBishop(int fRow, int fCol, int tRow, int tCol, String[][] grid) {
        // bishop slash: The absolute value of the row difference is equal, and the path is empty
        if (Math.abs(fRow - tRow) != Math.abs(fCol - tCol)) return false;
        return isPathClear(grid, fRow, fCol, tRow, tCol);
    }

    private boolean validateQueen(int fRow, int fCol, int tRow, int tCol, String[][] grid) {
        // queen = rook + bishop
        if (fRow == tRow || fCol == tCol) return validateRook(fRow, fCol, tRow, tCol, grid);
        if (Math.abs(fRow - tRow) == Math.abs(fCol - tCol)) return validateBishop(fRow, fCol, tRow, tCol, grid);
        return false;
    }

    /** Check that the time between the start point and the end point (excluding the end point) is empty */
    private boolean isPathClear(String[][] grid, int fRow, int fCol, int tRow, int tCol) {
        int rowStep = Integer.compare(tRow, fRow); // -1,0,1
        int colStep = Integer.compare(tCol, fCol);

        int r = fRow + rowStep;
        int c = fCol + colStep;
        while (r != tRow || c != tCol) {
            if (grid[r][c] != null && !grid[r][c].equals("")) return false;
            r += rowStep;
            c += colStep;
        }
        return true;
    }

    // ---king ---
    private boolean validateKing(Board board, int fRow, int fCol, int tRow, int tCol) {
        String[][] grid = board.getGrid();
        int rowDiff = Math.abs(fRow - tRow);
        int colDiff = Math.abs(fCol - tCol);

        // basic
        if (rowDiff <= 1 && colDiff <= 1) return true;

        // Translocation: The king walks two squares horizontally in the same row
        if (fRow == tRow && Math.abs(colDiff) == 2) {
            boolean isWhite = Character.isUpperCase(grid[fRow][fCol].charAt(0));

            // cheaking: the king has moved
            if (isWhite && board.isWhiteKingMoved()) return false;
            if (!isWhite && board.isBlackKingMoved()) return false;

            // checking the rook
            if (colDiff == 2 && tCol > fCol) {
                // King side translocation (right)
                int rookCol = 7;
                String rook = grid[fRow][rookCol];
                if (rook == null || rook.equals("") || Character.toLowerCase(rook.charAt(0)) != 'r') return false;
                if (isWhite && board.isWhiteRookHMoved()) return false;
                if (!isWhite && board.isBlackRookHMoved()) return false;

                // There are no obstacles in between
                return isPathClear(grid, fRow, fCol, fRow, rookCol);
            } else {
                // queen side translocation (left)
                int rookCol = 0;
                String rook = grid[fRow][rookCol];
                if (rook == null || rook.equals("") || Character.toLowerCase(rook.charAt(0)) != 'r') return false;
                if (isWhite && board.isWhiteRookAMoved()) return false;
                if (!isWhite && board.isBlackRookAMoved()) return false;

                return isPathClear(grid, fRow, fCol, fRow, rookCol);
            }
        }

        return false;
    }

	 private boolean validatePawn(int fRow, int fCol, int tRow, int tCol, String piece, String[][] grid) {
	     boolean isWhite = Character.isUpperCase(piece.charAt(0));
	     int direction = isWhite ? -1 : 1; // 白兵向上(行减少)，黑兵向下(行增加)
	     int startRow = isWhite ? 6 : 1;   // 白兵初始行是6，黑兵是1
	
	     int rowDiff = tRow - fRow;
	     int colDiff = tCol - fCol;
	     String targetPiece = grid[tRow][tCol];
	
	     // --- A. 直走 (移动) ---
	     if (colDiff == 0) {
	         // 1. 走一格：前方必须为空
	         if (rowDiff == direction && targetPiece.equals("")) {
	             return true;
	         }
	         // 2. 初始走两格：前方两格都必须为空，且在起始行
	         if (fRow == startRow && rowDiff == 2 * direction) {
	             String stepOverPiece = grid[fRow + direction][fCol]; // 经过的那一格
	             if (targetPiece.equals("") && stepOverPiece.equals("")) {
	                 return true;
	             }
	         }
	     } 
	     // --- B. 斜走 (吃子) ---
	     else if (Math.abs(colDiff) == 1 && rowDiff == direction) {
	         // 目标格必须有对方棋子
	         if (!targetPiece.equals("")) {
	             return true; // Basic eating logic (not considering "passing soldiers" eating children for the time being)
	         }
	     }
	
	     return false;
	 }
	}
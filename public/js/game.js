
class ChessGame {
    constructor() {
        this.reset();
    }

    reset() {
        this.grid = this.createInitialGrid();
        this.turn = 'white';
        this.whiteKingMoved = false;
        this.blackKingMoved = false;
        this.whiteRookAMoved = false; // Queen-side
        this.whiteRookHMoved = false; // King-side
        this.blackRookAMoved = false;
        this.blackRookHMoved = false;
        this.moveHistory = [];
        this.gameOver = false;
        this.winner = null;
    }

    createInitialGrid() {
        const grid = Array(8).fill(null).map(() => Array(8).fill(""));
        
        // Black pieces
        grid[0] = ["r", "n", "b", "q", "k", "b", "n", "r"];
        grid[1] = ["p", "p", "p", "p", "p", "p", "p", "p"];

        // White pieces
        grid[6] = ["P", "P", "P", "P", "P", "P", "P", "P"];
        grid[7] = ["R", "N", "B", "Q", "K", "B", "N", "R"];

        return grid;
    }

    getPiece(r, c) {
        return this.grid[r][c];
    }

    isWhite(piece) {
        return piece === piece.toUpperCase();
    }

    // --- Rule Engine Port ---

    isLegalMove(fRow, fCol, tRow, tCol) {
        const piece = this.grid[fRow][fCol];

        // 1. Basic check
        if (!piece) return false;

        // 2. Don't capture own pieces
        const targetPiece = this.grid[tRow][tCol];
        if (targetPiece) {
            const isWhiteAttacker = this.isWhite(piece);
            const isWhiteTarget = this.isWhite(targetPiece);
            if (isWhiteAttacker === isWhiteTarget) return false;
        }

        // 3. Piece specific logic
        const type = piece.toLowerCase();
        switch (type) {
            case 'n': return this.validateKnight(fRow, fCol, tRow, tCol);
            case 'r': return this.validateRook(fRow, fCol, tRow, tCol);
            case 'b': return this.validateBishop(fRow, fCol, tRow, tCol);
            case 'q': return this.validateQueen(fRow, fCol, tRow, tCol);
            case 'p': return this.validatePawn(fRow, fCol, tRow, tCol, piece);
            case 'k': return this.validateKing(fRow, fCol, tRow, tCol);
        }
        return false;
    }

    validateKnight(fRow, fCol, tRow, tCol) {
        const rowDiff = Math.abs(fRow - tRow);
        const colDiff = Math.abs(fCol - tCol);
        return (rowDiff === 1 && colDiff === 2) || (rowDiff === 2 && colDiff === 1);
    }

    validateRook(fRow, fCol, tRow, tCol) {
        if (fRow !== tRow && fCol !== tCol) return false;
        return this.isPathClear(fRow, fCol, tRow, tCol);
    }

    validateBishop(fRow, fCol, tRow, tCol) {
        if (Math.abs(fRow - tRow) !== Math.abs(fCol - tCol)) return false;
        return this.isPathClear(fRow, fCol, tRow, tCol);
    }

    validateQueen(fRow, fCol, tRow, tCol) {
        if (fRow === tRow || fCol === tCol) return this.validateRook(fRow, fCol, tRow, tCol);
        if (Math.abs(fRow - tRow) === Math.abs(fCol - tCol)) return this.validateBishop(fRow, fCol, tRow, tCol);
        return false;
    }

    isPathClear(fRow, fCol, tRow, tCol) {
        const rowStep = Math.sign(tRow - fRow);
        const colStep = Math.sign(tCol - fCol);

        let r = fRow + rowStep;
        let c = fCol + colStep;
        while (r !== tRow || c !== tCol) {
            if (this.grid[r][c] !== "") return false;
            r += rowStep;
            c += colStep;
        }
        return true;
    }

    validateKing(fRow, fCol, tRow, tCol) {
        const rowDiff = Math.abs(fRow - tRow);
        const colDiff = Math.abs(fCol - tCol);
        const piece = this.grid[fRow][fCol];
        const isWhite = this.isWhite(piece);

        // Basic move
        if (rowDiff <= 1 && colDiff <= 1) return true;

        // Castling
        if (fRow === tRow && Math.abs(colDiff) === 2) {
            if (isWhite && this.whiteKingMoved) return false;
            if (!isWhite && this.blackKingMoved) return false;

            if (tCol > fCol) { // King-side (right)
                const rookCol = 7;
                const rook = this.grid[fRow][rookCol];
                if (!rook || rook.toLowerCase() !== 'r') return false;
                if (isWhite && this.whiteRookHMoved) return false;
                if (!isWhite && this.blackRookHMoved) return false;
                return this.isPathClear(fRow, fCol, fRow, rookCol);
            } else { // Queen-side (left)
                const rookCol = 0;
                const rook = this.grid[fRow][rookCol];
                if (!rook || rook.toLowerCase() !== 'r') return false;
                if (isWhite && this.whiteRookAMoved) return false;
                if (!isWhite && this.blackRookAMoved) return false;
                return this.isPathClear(fRow, fCol, fRow, rookCol);
            }
        }
        return false;
    }

    validatePawn(fRow, fCol, tRow, tCol, piece) {
        const isWhite = this.isWhite(piece);
        const direction = isWhite ? -1 : 1;
        const startRow = isWhite ? 6 : 1;
        const rowDiff = tRow - fRow;
        const colDiff = tCol - fCol;
        const targetPiece = this.grid[tRow][tCol];

        if (colDiff === 0) { // Straight
            if (rowDiff === direction && targetPiece === "") return true;
            if (fRow === startRow && rowDiff === 2 * direction) {
                const stepOver = this.grid[fRow + direction][fCol];
                if (targetPiece === "" && stepOver === "") return true;
            }
        } else if (Math.abs(colDiff) === 1 && rowDiff === direction) { // Diagonal Capture
            if (targetPiece !== "") return true;
        }
        return false;
    }

    // --- Action ---

    makeMove(fRow, fCol, tRow, tCol) {
        const piece = this.grid[fRow][fCol];
        if (!piece) return { success: false, error: "No piece" };

        const isWhitePiece = this.isWhite(piece);
        if ((this.turn === 'white' && !isWhitePiece) || (this.turn === 'black' && isWhitePiece)) {
            return { success: false, error: "Not your turn" };
        }

        if (!this.isLegalMove(fRow, fCol, tRow, tCol)) {
            return { success: false, error: "Illegal move" };
        }

        const targetPiece = this.grid[tRow][tCol];
        let status = "OK";
        if (targetPiece.toLowerCase() === 'k') {
            status = `GAMEOVER_${this.turn}`;
            this.gameOver = true;
            this.winner = this.turn;
        }

        // Apply Move
        this.updateBoardState(fRow, fCol, tRow, tCol, piece, isWhitePiece);
        
        // Switch Turn
        this.turn = this.turn === 'white' ? 'black' : 'white';

        return { success: true, status: status, turn: this.turn, grid: this.grid };
    }

    updateBoardState(fRow, fCol, tRow, tCol, piece, isWhite) {
        // Handle Castling
        if (piece.toLowerCase() === 'k' && Math.abs(tCol - fCol) === 2) {
            if (isWhite) this.whiteKingMoved = true; else this.blackKingMoved = true;
            
            if (tCol > fCol) { // King-side
                this.grid[tRow][tCol] = piece;
                this.grid[fRow][fCol] = "";
                // Move Rook
                this.grid[tRow][fCol + 1] = this.grid[tRow][7];
                this.grid[tRow][7] = "";
                if (isWhite) this.whiteRookHMoved = true; else this.blackRookHMoved = true;
            } else { // Queen-side
                this.grid[tRow][tCol] = piece;
                this.grid[fRow][fCol] = "";
                // Move Rook
                this.grid[tRow][fCol - 1] = this.grid[tRow][0];
                this.grid[tRow][0] = "";
                if (isWhite) this.whiteRookAMoved = true; else this.blackRookAMoved = true;
            }
            return;
        }

        // Normal Move
        this.grid[tRow][tCol] = piece;
        this.grid[fRow][fCol] = "";

        // Flags Update
        if (piece.toLowerCase() === 'k') {
            if (isWhite) this.whiteKingMoved = true; else this.blackKingMoved = true;
        }
        if (piece.toLowerCase() === 'r') {
             if (isWhite) {
                if (fRow === 7 && fCol === 0) this.whiteRookAMoved = true;
                if (fRow === 7 && fCol === 7) this.whiteRookHMoved = true;
            } else {
                if (fRow === 0 && fCol === 0) this.blackRookAMoved = true;
                if (fRow === 0 && fCol === 7) this.blackRookHMoved = true;
            }
        }

        // Pawn Promotion
        if (piece.toLowerCase() === 'p') {
            if (isWhite && tRow === 0) this.grid[tRow][tCol] = "Q";
            else if (!isWhite && tRow === 7) this.grid[tRow][tCol] = "q";
        }
    }
}

const PieceUI = {
    getUnicode: (p) => {
        switch(p) {
            case "K": return "\u2654"; // ♔
            case "Q": return "\u2655"; // ♕
            case "R": return "\u2656"; // ♖
            case "B": return "\u2657"; // ♗
            case "N": return "\u2658"; // ♘
            case "P": return "\u2659"; // ♙
            case "k": return "\u265A"; // ♚
            case "q": return "\u265B"; // ♛
            case "r": return "\u265C"; // ♜
            case "b": return "\u265D"; // ♝
            case "n": return "\u265E"; // ♞
            case "p": return "\u265F"; // ♟
            default: return "";
        }
    }
};

window.ChessGame = ChessGame;
window.PieceUI = PieceUI;

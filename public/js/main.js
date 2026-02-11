
document.addEventListener('DOMContentLoaded', () => {
    const game = new ChessGame();
    const boardEl = document.getElementById('board');
    const turnInfoEl = document.getElementById('turn-info');
    const startScreen = document.getElementById('start-screen');
    const startBtn = document.getElementById('start-btn');
    let selectedSquare = null;

    // Start Game
    startBtn.addEventListener('click', (e) => {
        e.preventDefault();
        startScreen.classList.add('hidden');
        renderBoard();
        updateInfo();
    });

    // Reset Game
    window.resetGame = () => {
        if(confirm("Start a new game?")) {
            game.reset();
            selectedSquare = null;
            renderBoard();
            updateInfo();
        }
    };

    window.showHistory = () => {
        alert("History is not available in offline mode.");
    };

    function renderBoard() {
        boardEl.innerHTML = '';
        const grid = game.grid;

        for (let r = 0; r < 8; r++) {
            for (let c = 0; c < 8; c++) {
                const square = document.createElement('div');
                square.className = `square ${(r + c) % 2 === 0 ? 'light' : 'dark'}`;
                square.id = `sq-${r}-${c}`;
                square.dataset.r = r;
                square.dataset.c = c;
                
                const piece = grid[r][c];
                square.innerHTML = PieceUI.getUnicode(piece);

                if (selectedSquare && selectedSquare.r === r && selectedSquare.c === c) {
                    square.classList.add('selected');
                }

                square.onclick = () => handleClick(r, c);
                boardEl.appendChild(square);
            }
        }
    }

    function handleClick(r, c) {
        if (game.gameOver) {
            alert("Game Over! " + (game.winner === 'white' ? "White" : "Black") + " won.");
            return;
        }

        const piece = game.grid[r][c];

        // 1. Select Source
        if (!selectedSquare) {
            if (piece === "") return; // Clicked empty square
            
            // Check turn
            const isWhite = game.isWhite(piece);
            if ((game.turn === 'white' && !isWhite) || (game.turn === 'black' && isWhite)) {
                // Not your piece
                return;
            }

            selectedSquare = { r, c };
            renderBoard(); // Re-render to show selection
        } 
        // 2. Move to Target
        else {
            // Deselect if clicked same square
            if (selectedSquare.r === r && selectedSquare.c === c) {
                selectedSquare = null;
                renderBoard();
                return;
            }

            // Try Move
            const result = game.makeMove(selectedSquare.r, selectedSquare.c, r, c);
            
            if (result.success) {
                selectedSquare = null;
                renderBoard();
                updateInfo();
                
                if (result.status.startsWith("GAMEOVER")) {
                    setTimeout(() => {
                        const winner = result.status.split("_")[1];
                        alert("Checkmate! " + (winner === 'white' ? "White" : "Black") + " won!");
                    }, 100);
                }
            } else {
                // If clicked another own piece, switch selection
                const targetPiece = game.grid[r][c];
                if (targetPiece && game.isWhite(targetPiece) === game.isWhite(game.grid[selectedSquare.r][selectedSquare.c])) {
                    selectedSquare = { r, c };
                    renderBoard();
                } else {
                    alert(result.error || "Invalid move");
                    selectedSquare = null;
                    renderBoard();
                }
            }
        }
    }

    function updateInfo() {
        turnInfoEl.innerText = `Turn: ${game.turn === 'white' ? "White (白)" : "Black (黒)"}`;
    }
});

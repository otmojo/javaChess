let selectedSquare = null;

function handleSquareClick(row, col) {
    const square = document.querySelector(`.square[data-row="${row}"][data-col="${col}"]`);
    const pieceContent = square.querySelector('.piece-content').innerText.trim();
    
    // if chose a square
    if (selectedSquare) {
        // double click for cancelling
        if (selectedSquare.row === row && selectedSquare.col === col) {
            clearSelection();
            return;
        }
        
        

        movePiece(selectedSquare.row, selectedSquare.col, row, col);
        clearSelection();
    } else {
        // choosing the piece
        if (pieceContent !== "") {
            // checking for this round or not
            selectedSquare = { row: row, col: col };
            square.classList.add('selected');
        }
    }
}

function clearSelection() {
    selectedSquare = null;
    document.querySelectorAll('.square').forEach(el => el.classList.remove('selected'));
}

function movePiece(fRow, fCol, tRow, tCol) {
    const params = new URLSearchParams();
    params.append('fRow', fRow);
    params.append('fCol', fCol);
    params.append('tRow', tRow);
    params.append('tCol', tCol);
    // level default 0

    fetch('game', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === 'success') {
            updateBoard(data.board);
            updateTurn(data.turn);
            showMessage("");
        } else {
            showMessage(data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showMessage("通信エラーが発生しました");
    });
}

function updateBoard(grid) {
    for (let r = 0; r < 8; r++) {
        for (let c = 0; c < 8; c++) {
             const square = document.querySelector(`.square[data-row="${r}"][data-col="${c}"]`);
             const piece = grid[r][c];
             square.querySelector('.piece-content').innerText = piece;
        }
    }
}

function updateTurn(turn) {
    const turnIndicator = document.querySelector('.turn-indicator');
    if (turn === 'white') {
        turnIndicator.innerText = "手番: 白 (White)";
    } else {
        turnIndicator.innerText = "手番: 黒 (Black)";
    }
}

function showMessage(msg) {
    const msgArea = document.getElementById('message-area');
    if (msgArea) {
        msgArea.innerText = msg;
    } else {
        if (msg) alert(msg);
    }
}

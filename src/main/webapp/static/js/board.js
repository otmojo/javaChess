let selectedSquare = null;

function handleSquareClick(row, col) {
    const square = document.getElementById('sq-' + row + '-' + col);
    const pieceContent = square.innerText.trim();

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
    params.append('fR', fRow);
    params.append('fC', fCol);
    params.append('tR', tRow);
    params.append('tC', tCol);

    fetch('game', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params
    })
    .then(response => response.text())
    .then(status => {
        if (status.includes("GAMEOVER")) {
            const winner = status.split("_")[1] === "white" ? "白" : "黒";
            alert("勝負あり！" + winner + " WON！");
            location.reload();
        } else if (status === "OK") {
            location.reload();
        } else {
            showMessage(status);
            location.reload();
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
             const square = document.getElementById('sq-' + r + '-' + c);
             const piece = grid[r][c];
             square.innerText = piece;
        }
    }
}

function updateTurn(turn) {
    const turnIndicator = document.querySelector('.info');
    if (turnIndicator) {
        if (turn === 'white') {
            turnIndicator.innerText = "手番: 白 (White)";
        } else {
            turnIndicator.innerText = "手番: 黒 (Black)";
        }
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

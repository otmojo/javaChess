/**
 * API helper for the chess app
 */

/**
 * Pull all json of movement records （from movesJson action in servlet）
 * back to Promise -> Array of moves: {id, player, piece, from:[r,c], to:[r,c]}
 */
function fetchMoves() {
	var base = (typeof window !== 'undefined' && window.apiBase) ? window.apiBase : '';
	var url = base + '/game?action=movesJson';
	return fetch(url, {
		method: 'GET',
		headers: { 'Accept': 'application/json' }
	})
	.then(resp => {
		if (!resp.ok) throw new Error('Network response was not ok');
		return resp.json();
	});
}

// Export to the global for easy JSP inline script calls
window.api = {
	fetchMoves: fetchMoves
};
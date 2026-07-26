package lld.tictactoe;

/**
 * Outcome of a single move.
 *
 * {@code winner} is set only when {@code state == WON}; a draw and an ongoing game
 * are told apart by {@code state}, never by a null winner.
 */
public record MoveResult(GameState state, Player winner) {

    public boolean isGameOver() {
        return state.isOver();
    }
}

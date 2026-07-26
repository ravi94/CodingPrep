package lld.snakeLadder;

/**
 * Outcome of a single turn, enough for a caller to log or render it without
 * reaching into the game.
 *
 * @param from     the square the player started the turn on
 * @param to       the square they finished on, after any jump; equal to {@code from} when {@code overshot}
 * @param jump     the snake or ladder they landed on, or null
 * @param overshot true when the roll ran past the last square, so the turn was forfeit
 */
public record TurnResult(Player player, int roll, int from, int to, Jump jump, boolean overshot, GameState state) {

    public boolean isGameOver() {
        return state.isOver();
    }
}

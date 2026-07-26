package lld.tictactoe;

/**
 * Thrown when a move cannot be applied: off the board, onto an occupied cell,
 * without a player symbol, or after the game has already finished.
 *
 * Unchecked so the game API stays free of {@code throws} clauses.
 */
public class InvalidMoveException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidMoveException(String message) {
        super(message);
    }
}

package lld.tictactoe;

/**
 * Two-player turn loop over a {@link Board}.
 *
 * Reports outcomes through {@link MoveResult} rather than printing, so callers
 * decide how a win or a draw is presented.
 */
public class BoardGame {
    private final Board board;
    private final Player[] players; // 2 players only

    private int turn;
    private GameState state = GameState.IN_PROGRESS;
    private Player winner;

    public BoardGame(int size, Player player1, Player player2) {
        if (!player1.getSymbol().isPlayable() || !player2.getSymbol().isPlayable())
            throw new IllegalArgumentException("Both players need a playable symbol (X or O)");
        if (player1.getSymbol() == player2.getSymbol())
            throw new IllegalArgumentException("Players must use different symbols, both got " + player1.getSymbol());

        this.players = new Player[]{player1, player2};
        this.board = new Board(size);
    }

    /**
     * Applies the current player's move.
     *
     * @return the resulting state, carrying the winner when the state is WON
     * @throws InvalidMoveException if the game has finished or the cell is unusable
     */
    public MoveResult move(int r, int c) {
        if (state.isOver())
            throw new InvalidMoveException("Game is over (" + state + "), so no move allowed !");

        Player current = currentPlayer();
        board.move(r, c, current.getSymbol());
        turn++;

        if (board.isWon(r, c)) {
            state = GameState.WON;
            winner = current;
        } else if (board.isDraw()) {
            state = GameState.DRAW;
        }

        return new MoveResult(state, winner);
    }

    /** The player due to move. Only meaningful while the state is IN_PROGRESS. */
    public Player currentPlayer() {
        return players[turn % players.length];
    }

    public GameState getState() {
        return state;
    }

    /** The winner, or null when nobody has won. */
    public Player getWinner() {
        return winner;
    }

    public Board getBoard() {
        return board;
    }
}

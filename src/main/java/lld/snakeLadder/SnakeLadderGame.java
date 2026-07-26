package lld.snakeLadder;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Turn loop over a {@link Board}. Players take turns round robin, and a player
 * must land exactly on the last square to win: a roll that would run past it
 * forfeits the turn.
 *
 * Turns are reported through {@link TurnResult} rather than printed, so callers
 * decide how a game is rendered. {@link #play()} is just a loop over
 * {@link #playTurn()}.
 */
public class SnakeLadderGame {

    /**
     * Not every board can be won: if every square within a roll of the finish is
     * a snake's head, no sequence of rolls ever lands on it. Give up rather than
     * spin forever.
     */
    private static final int MAX_TURNS = 10_000;

    private final Dice dice;
    private final Board board;
    private final Deque<Player> playersQueue;

    private GameState state = GameState.IN_PROGRESS;
    private Player winner;

    public SnakeLadderGame(Board board, Dice dice, List<Player> players) {
        if (board == null)
            throw new IllegalArgumentException("A board is required");
        if (dice == null)
            throw new IllegalArgumentException("A dice is required");
        if (players == null || players.isEmpty())
            throw new IllegalArgumentException("No players to play !");
        // not contains(null), which throws on the immutable List.of collections
        for (Player player : players) {
            if (player == null)
                throw new IllegalArgumentException("A player cannot be null");
        }

        this.board = board;
        this.dice = dice;
        this.playersQueue = new ArrayDeque<>(players); // copied, so the caller's list stays untouched
    }

    /**
     * Rolls for the player whose turn it is and moves them.
     *
     * @throws IllegalStateException if the game has already been won
     */
    public TurnResult playTurn() {
        if (state.isOver())
            throw new IllegalStateException("Game is over (" + state + "), " + winner.getName() + " already won");

        Player current = playersQueue.poll();
        int from = current.getPosition();
        int roll = dice.roll();
        int target = from + roll;

        if (target > board.getSize()) { // has to be exact, so the turn is forfeit
            playersQueue.addLast(current);
            return new TurnResult(current, roll, from, from, null, true, state);
        }

        Jump jump = board.jumpAt(target);
        int to = board.landingPosition(target);
        current.moveTo(to);

        if (to == board.getSize()) {
            state = GameState.WON;
            winner = current;
        } else {
            playersQueue.addLast(current);
        }

        return new TurnResult(current, roll, from, to, jump, false, state);
    }

    /**
     * Plays turns until somebody wins.
     *
     * @throws IllegalStateException if no winner emerges within {@value #MAX_TURNS} turns
     */
    public Player play() {
        for (int turn = 0; turn < MAX_TURNS; turn++) {
            if (playTurn().isGameOver())
                return winner;
        }
        throw new IllegalStateException(
                "No winner after " + MAX_TURNS + " turns, the board is very likely unwinnable");
    }

    /** The player due to roll. Only meaningful while the state is IN_PROGRESS. */
    public Player currentPlayer() {
        return playersQueue.peek();
    }

    public GameState getState() {
        return state;
    }

    /** The winner, or null while nobody has won. */
    public Player getWinner() {
        return winner;
    }

    public Board getBoard() {
        return board;
    }
}

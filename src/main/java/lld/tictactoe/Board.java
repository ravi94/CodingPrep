package lld.tictactoe;

import java.util.Arrays;

/**
 * Square grid that tracks win progress in O(1) per move instead of rescanning
 * the board.
 *
 * Every cell adds +1 for X and -1 for O to its row counter, its column counter
 * and, when the cell sits on one, each diagonal counter. A line is complete when
 * its counter hits +size (all X) or -size (all O). This encoding only works for
 * exactly two symbols; supporting N players means per-symbol counts or an
 * O(size) scan of the four lines through the last move.
 */
public class Board {
    private static final int MIN_SIZE = 3;

    private final int size;
    private final Symbol[][] grid;
    private final int[] row;
    private final int[] column;
    private int moves;
    private int diag;
    private int antiDiag;

    public Board(int size) {
        if (size < MIN_SIZE)
            throw new IllegalArgumentException("Board size must be at least " + MIN_SIZE + ", got " + size);

        this.size = size;
        this.row = new int[size];
        this.column = new int[size];
        this.grid = new Symbol[size][size];

        for (Symbol[] cells : grid) {
            Arrays.fill(cells, Symbol.EMPTY);
        }
    }

    public void move(int r, int c, Symbol symbol) {
        // EMPTY would score as -1 and credit O with a cell nobody played.
        if (symbol == null || !symbol.isPlayable())
            throw new InvalidMoveException("Invalid move , a move needs a player symbol, got "
                    + (symbol == null ? "null" : symbol.name()) + " !");
        if (r >= size || c >= size || r < 0 || c < 0)
            throw new InvalidMoveException("Invalid move , out of bound from board size !");
        if (grid[r][c] != Symbol.EMPTY)
            throw new InvalidMoveException("Invalid move , Place already filled !");

        grid[r][c] = symbol;
        moves++;

        int v = symbol == Symbol.X ? 1 : -1; // assume adding 1 for x and adding o for -1
        row[r] += v; // for row check
        column[c] += v; // for col check

        if (r == c) diag += v; // for diag check
        if (r + c == size - 1) antiDiag += v;
    }

    /** True when the move just played at (r,c) completed one of its four lines. */
    public boolean isWon(int r, int c) {
        return Math.abs(row[r]) == size                                  // row check
                || Math.abs(column[c]) == size                           // column check
                || (r == c && Math.abs(diag) == size)                    // diag check
                || (r + c == size - 1 && Math.abs(antiDiag) == size);    // antiDiag check
    }

    /** True once every cell is filled. Only a draw if {@link #isWon} already came back false. */
    public boolean isDraw() {
        return moves == size * size;
    }

    public int getSize() {
        return size;
    }

    public Symbol symbolAt(int r, int c) {
        return grid[r][c];
    }

    /**
     * Renders the grid, e.g.
     * <pre>
     *  X | O | X
     * -----------
     *    | O |
     * -----------
     *  X |   |
     * </pre>
     */
    public String render() {
        String separator = "-".repeat(size * 4 - 1);
        StringBuilder sb = new StringBuilder();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                sb.append(' ').append(grid[r][c].getValue()).append(' ');
                if (c < size - 1) sb.append('|');
            }
            sb.append(System.lineSeparator());
            if (r < size - 1) sb.append(separator).append(System.lineSeparator());
        }
        return sb.toString();
    }
}

package lld.snakeLadder;

import lombok.Builder;
import lombok.Singular;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Squares 1..size, with the snakes and ladders that start on them.
 *
 * A jump is rejected if it starts on the last square: the game resolves the
 * jump before testing for a win, so such a square could never be held and the
 * board would be unwinnable.
 */
public class Board {
    private final int size;
    private final Map<Integer, Jump> jumps;

    @Builder
    Board(int size, @Singular List<Jump> jumps) {
        if (size < 2)
            throw new IllegalArgumentException("A board needs at least 2 squares, got " + size);
        this.size = size;
        this.jumps = new HashMap<>();
        jumps.forEach(this::addJump);
    }

    public void addJump(int start, int end) {
        addJump(new Jump(start, end));
    }

    /**
     * @throws IllegalArgumentException if either square is off the board, the
     *                                  jump starts on the last square, or that
     *                                  square already holds a jump
     */
    public void addJump(Jump jump) {
        requireSquare(jump.start(), "start");
        requireSquare(jump.end(), "end");
        if (jump.start() == size)
            throw new IllegalArgumentException(
                    "A jump cannot start on the last square (" + size + "), the game would be unwinnable");

        Jump existing = jumps.putIfAbsent(jump.start(), jump);
        if (existing != null)
            throw new IllegalArgumentException(
                    "Square " + jump.start() + " already jumps to " + existing.end());
    }

    /**
     * Where a player landing on {@code position} ends up.
     *
     * A jump is applied at most once, so a ladder that drops you onto a snake's
     * head leaves you there until your next turn. That is the standard rule.
     */
    public int landingPosition(int position) {
        Jump jump = jumps.get(position);
        return jump == null ? position : jump.end();
    }

    /** The snake or ladder starting on {@code position}, or null. */
    public Jump jumpAt(int position) {
        return jumps.get(position);
    }

    public int getSize() {
        return size;
    }

    private void requireSquare(int square, String label) {
        if (square < 1 || square > size)
            throw new IllegalArgumentException(
                    "Jump " + label + " must be within 1.." + size + ", got " + square);
    }
}

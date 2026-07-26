package lld.snakeLadder;

/**
 * A snake or a ladder: a player landing on {@code start} is moved to {@code end}.
 *
 * The direction alone tells the two apart, so there is no separate type. Bounds
 * are not checked here because a jump only makes sense against a {@link Board}
 * of a known size, which is where the squares are validated.
 */
public record Jump(int start, int end) {

    public Jump {
        if (start == end)
            throw new IllegalArgumentException("A jump must move the player, but start and end are both " + start);
    }

    public boolean isSnake() {
        return end < start;
    }

    public boolean isLadder() {
        return end > start;
    }
}

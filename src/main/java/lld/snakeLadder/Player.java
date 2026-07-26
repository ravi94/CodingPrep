package lld.snakeLadder;

import lombok.Getter;

/**
 * A token on the board. Identity is the object itself: two players sharing a
 * name or a square are still two players, so equality is deliberately left as
 * the default rather than derived from the mutable position.
 */
@Getter
public class Player {
    private final String name;
    private int position;

    public Player(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("A player needs a name");
        this.name = name;
        this.position = 0; // off the board until the first roll
    }

    /** Package private so that only the game can move a token. */
    void moveTo(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return name + " on " + position;
    }
}

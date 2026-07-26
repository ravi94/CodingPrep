package lld.snakeLadder;

import java.util.Random;

/**
 * A fair die. Seed the {@link Random} to make a game reproducible, or implement
 * {@link Dice} directly to script one outright.
 */
public class StandardDice implements Dice {
    private static final int STANDARD_FACES = 6;

    private final Random random;
    private final int faces;

    public StandardDice() {
        this(STANDARD_FACES, new Random());
    }

    public StandardDice(int faces) {
        this(faces, new Random());
    }

    public StandardDice(int faces, Random random) {
        if (faces < 1)
            throw new IllegalArgumentException("A die needs at least one face, got " + faces);
        if (random == null)
            throw new IllegalArgumentException("A source of randomness is required");
        this.faces = faces;
        this.random = random;
    }

    @Override
    public int roll() {
        return random.nextInt(faces) + 1;
    }
}

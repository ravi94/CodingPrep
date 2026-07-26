package lld.snakeLadder;

import java.util.List;

/**
 * Exercises the game end to end: a ladder shortcut, a snake bite, the
 * land-exactly-on-the-last-square rule, the guard rails around bad boards and
 * rosters, and a board nobody can win.
 *
 * Every scenario feeds {@link SnakeLadderGame} a fixed sequence of rolls through
 * {@link ScriptedDice}, so outcomes are deterministic and each one is checked
 * against the square the rules say the player should reach.
 */
public class SnakeLadderDemo {

    private static int checks;
    private static int failures;

    public static void main(String[] args) {
        ladderShortcut();
        snakeBite();
        mustLandExactly();
        guardRails();
        unwinnableBoard();

        System.out.printf("%d checks, %d failed%n", checks, failures);
        if (failures > 0) System.exit(1);
    }

    /** A 20 square board. Ladders climb from 4 and 8, snakes drop from 16 and 18. */
    private static Board standardBoard() {
        return Board.builder()
                .size(20)
                .jump(new Jump(4, 14))
                .jump(new Jump(8, 12))
                .jump(new Jump(16, 6))
                .jump(new Jump(18, 2))
                .build();
    }

    private static void ladderShortcut() {
        header("1. A ladder carries Alice from 4 to 14, then she finishes");
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");

        Player winner = play(List.of(alice, bob), standardBoard(), 4, 3, 6);

        note("Alice rolled 4 and 6, yet she covered 20 squares. The ladder at 4 covered the rest.");
        check("winner", winner.getName(), "Alice");
        check("Alice", alice.getPosition(), 20);
        check("Bob", bob.getPosition(), 3);
    }

    private static void snakeBite() {
        header("2. Alice lands on 16 and the snake drops her to 6, so Bob wins");
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");

        Player winner = play(List.of(alice, bob), standardBoard(), 5, 5, 6, 6, 5, 3, 6, 6);

        note("Alice rolled 22 in total and still ended on 12, having lost 10 squares to the snake at 16.");
        check("winner", winner.getName(), "Bob");
        check("Alice", alice.getPosition(), 12);
        check("Bob", bob.getPosition(), 20);
    }

    private static void mustLandExactly() {
        header("3. Overshooting the last square forfeits the turn");
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");

        Player winner = play(List.of(alice, bob), standardBoard(), 6, 1, 6, 1, 5, 1, 5, 1, 3);

        note("Alice's 5 from square 17 would have run past 20, so she stayed put and won later with an exact 3.");
        check("winner", winner.getName(), "Alice");
        check("Alice", alice.getPosition(), 20);
        check("Bob", bob.getPosition(), 14);
    }

    private static void guardRails() {
        header("4. Rejected boards and rosters");

        expectRejected("jump onto the last square", () -> board(new Jump(20, 2)));
        expectRejected("jump off the board", () -> board(new Jump(4, 21)));
        expectRejected("jump from square 0", () -> board(new Jump(0, 4)));
        expectRejected("jump that goes nowhere", () -> new Jump(4, 4));
        expectRejected("two jumps on one square", () -> board(new Jump(4, 14), new Jump(4, 2)));
        expectRejected("board smaller than 2 squares", () -> Board.builder().size(1).build());
        expectRejected("no players",
                () -> new SnakeLadderGame(standardBoard(), new StandardDice(), List.of()));
        expectRejected("a nameless player", () -> new Player(" "));

        SnakeLadderGame finished = new SnakeLadderGame(standardBoard(), new ScriptedDice(4, 3, 6),
                List.of(new Player("Alice"), new Player("Bob")));
        finished.play();
        expectRejected("a turn after the game is won", finished::playTurn);

        System.out.println();
    }

    /**
     * Squares 14 to 19 are the only ones a player can win from, since the last
     * square is 20 and the die stops at 6. Put a snake on every one of them and
     * the game can never end, which the turn cap catches.
     */
    private static void unwinnableBoard() {
        header("5. A board nobody can win is given up on rather than looping forever");
        Board board = Board.builder()
                .size(20)
                .jump(new Jump(14, 1)).jump(new Jump(15, 1)).jump(new Jump(16, 1))
                .jump(new Jump(17, 1)).jump(new Jump(18, 1)).jump(new Jump(19, 1))
                .build();

        expectRejected("snakes guarding every approach",
                () -> new SnakeLadderGame(board, new StandardDice(), List.of(new Player("Alice"))).play());

        System.out.println();
    }

    /** Runs one game to completion on the given script, narrating each turn. */
    private static Player play(List<Player> players, Board board, int... script) {
        ScriptedDice dice = new ScriptedDice(script);
        SnakeLadderGame game = new SnakeLadderGame(board, dice, players);

        TurnResult result;
        int turn = 0;
        do {
            result = game.playTurn();
            System.out.printf("  turn %-2d %s%n", ++turn, describe(result));
        } while (!result.isGameOver());

        if (!dice.isExhausted())
            report("script", dice.remaining() + " rolls left over, the scenario ended early");

        System.out.println();
        return game.getWinner();
    }

    private static String describe(TurnResult result) {
        String opening = "%-5s on %2d rolls %d".formatted(result.player().getName(), result.from(), result.roll());

        if (result.overshot())
            return opening + ", overshoots the finish and stays put";

        String move = opening + " to " + result.to();
        if (result.jump() != null)
            move = "%s to %d, %s to %d".formatted(opening, result.jump().start(),
                    result.jump().isSnake() ? "snake down" : "ladder up", result.to());

        return result.isGameOver() ? move + " and wins !" : move;
    }

    /** Replays a fixed sequence of rolls, so a scenario reads as its script. */
    private static final class ScriptedDice implements Dice {
        private final int[] script;
        private int next;

        ScriptedDice(int... script) {
            this.script = script;
        }

        @Override
        public int roll() {
            if (next == script.length)
                throw new IllegalStateException("Dice script exhausted after " + next + " rolls");
            return script[next++];
        }

        boolean isExhausted() {
            return next == script.length;
        }

        int remaining() {
            return script.length - next;
        }
    }

    private static Board board(Jump... jumps) {
        Board.BoardBuilder builder = Board.builder().size(20);
        for (Jump jump : jumps) builder.jump(jump);
        return builder.build();
    }

    private static void check(String label, Object actual, Object expected) {
        checks++;
        if (actual.equals(expected)) {
            report(label, String.valueOf(actual));
        } else {
            failures++;
            report(label, "expected " + expected + " but got " + actual + " (bug)");
        }
    }

    private static void expectRejected(String label, Runnable action) {
        checks++;
        try {
            action.run();
            failures++;
            report(label, "NOT REJECTED (bug)");
        } catch (RuntimeException e) {
            report(label, "rejected: " + e.getMessage());
        }
    }

    private static void report(String label, String outcome) {
        System.out.printf("  %-28s %s%n", label, outcome);
    }

    private static void note(String text) {
        System.out.println("  " + text);
    }

    private static void header(String title) {
        System.out.println("=== " + title + " ===");
    }
}

package lld.snakeLadder;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every test scripts the dice, so nothing here depends on chance.
 */
class SnakeLadderGameTest {

    /** 20 squares, ladders from 4 and 8, snakes from 16 and 18. */
    private static Board board() {
        return Board.builder()
                .size(20)
                .jump(new Jump(4, 14))
                .jump(new Jump(8, 12))
                .jump(new Jump(16, 6))
                .jump(new Jump(18, 2))
                .build();
    }

    private static Dice scripted(int... rolls) {
        int[] script = rolls.clone();
        return new Dice() {
            private int next;

            @Override
            public int roll() {
                if (next == script.length)
                    throw new IllegalStateException("dice script exhausted");
                return script[next++];
            }
        };
    }

    @Test
    void ladderCarriesThePlayerUp() {
        Player alice = new Player("Alice");
        SnakeLadderGame game = new SnakeLadderGame(board(), scripted(4), List.of(alice));

        TurnResult result = game.playTurn();

        assertEquals(14, alice.getPosition());
        assertEquals(4, result.jump().start());
        assertTrue(result.jump().isLadder());
        assertFalse(result.isGameOver());
    }

    @Test
    void snakeDragsThePlayerDown() {
        Player alice = new Player("Alice");
        SnakeLadderGame game = new SnakeLadderGame(board(), scripted(6, 6, 4), List.of(alice));

        game.playTurn(); // 0 -> 6
        game.playTurn(); // 6 -> 12
        TurnResult result = game.playTurn(); // 12 -> 16, snake down to 6

        assertEquals(6, alice.getPosition());
        assertTrue(result.jump().isSnake());
    }

    @Test
    void aJumpIsAppliedOnlyOnce() {
        // The ladder at 8 ends on 12, which is itself a snake's head. Standard
        // rules say the snake waits for the next turn, so 12 is where you stop.
        Board chained = Board.builder().size(20).jump(new Jump(8, 12)).jump(new Jump(12, 2)).build();
        Player alice = new Player("Alice");
        SnakeLadderGame game = new SnakeLadderGame(chained, scripted(6, 2), List.of(alice));

        game.playTurn(); // 0 -> 6
        assertEquals(12, game.playTurn().to(), "6 + 2 climbs the ladder and stops"); // 6 -> 8 -> 12
        assertEquals(12, alice.getPosition());
    }

    @Test
    void winningNeedsAnExactRoll() {
        Player alice = new Player("Alice");
        SnakeLadderGame game = new SnakeLadderGame(board(), scripted(6, 6, 5, 5, 3), List.of(alice));

        game.playTurn(); // 0 -> 6
        game.playTurn(); // 6 -> 12
        game.playTurn(); // 12 -> 17

        TurnResult overshot = game.playTurn(); // 17 + 5 would pass 20
        assertTrue(overshot.overshot());
        assertEquals(17, alice.getPosition(), "an overshooting player does not move");
        assertNull(overshot.jump());
        assertFalse(overshot.isGameOver());

        TurnResult winning = game.playTurn(); // 17 + 3 lands exactly
        assertTrue(winning.isGameOver());
        assertEquals(GameState.WON, game.getState());
        assertSame(alice, game.getWinner());
        assertEquals(20, alice.getPosition());
    }

    @Test
    void playersTakeTurnsRoundRobin() {
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");
        Player carol = new Player("Carol");
        SnakeLadderGame game = new SnakeLadderGame(board(), scripted(1, 1, 1, 2), List.of(alice, bob, carol));

        assertSame(alice, game.playTurn().player());
        assertSame(bob, game.playTurn().player());
        assertSame(carol, game.playTurn().player());
        assertSame(alice, game.playTurn().player(), "the queue wraps back round");
        assertEquals(3, alice.getPosition());
    }

    @Test
    void anOvershootingPlayerStillLosesTheTurn() {
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");
        SnakeLadderGame game = new SnakeLadderGame(board(), scripted(6, 1, 6, 1, 5, 1, 6, 1), List.of(alice, bob));

        game.playTurn(); // Alice 0 -> 6
        game.playTurn(); // Bob   0 -> 1
        game.playTurn(); // Alice 6 -> 12
        game.playTurn(); // Bob   1 -> 2
        game.playTurn(); // Alice 12 -> 17
        game.playTurn(); // Bob   2 -> 3
        assertTrue(game.playTurn().overshot()); // Alice 17 + 6 passes 20

        assertSame(bob, game.playTurn().player(), "play passes on even after an overshoot");
    }

    @Test
    void playRunsUntilSomebodyWins() {
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");
        SnakeLadderGame game = new SnakeLadderGame(board(), scripted(4, 3, 6), List.of(alice, bob));

        assertSame(alice, game.play());
        assertEquals(20, alice.getPosition());
        assertEquals(3, bob.getPosition());
    }

    @Test
    void playGivesUpOnABoardNobodyCanWin() {
        // 14..19 are the only squares a win can be launched from, so snakes on all
        // of them make 20 unreachable.
        Board.BoardBuilder builder = Board.builder().size(20);
        for (int square = 14; square <= 19; square++) builder.jump(new Jump(square, 1));
        SnakeLadderGame game = new SnakeLadderGame(builder.build(), new StandardDice(), List.of(new Player("Alice")));

        IllegalStateException thrown = assertThrows(IllegalStateException.class, game::play);
        assertTrue(thrown.getMessage().contains("unwinnable"), thrown.getMessage());
    }

    @Test
    void aTurnAfterTheGameIsWonIsRejected() {
        SnakeLadderGame game = new SnakeLadderGame(board(), scripted(4, 3, 6), List.of(new Player("Alice"), new Player("Bob")));
        game.play();

        assertThrows(IllegalStateException.class, game::playTurn);
    }

    @Test
    void theRosterIsValidatedUpFront() {
        Board board = board();
        Dice dice = new StandardDice();
        List<Player> players = List.of(new Player("Alice"));

        assertThrows(IllegalArgumentException.class, () -> new SnakeLadderGame(board, dice, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new SnakeLadderGame(board, dice, null));
        assertThrows(IllegalArgumentException.class, () -> new SnakeLadderGame(null, dice, players));
        assertThrows(IllegalArgumentException.class, () -> new SnakeLadderGame(board, null, players));
    }

    @Test
    void theCallersPlayerListIsNotHeldOnTo() {
        Player alice = new Player("Alice");
        List<Player> roster = new ArrayList<>(List.of(alice));
        SnakeLadderGame game = new SnakeLadderGame(board(), scripted(3), roster);

        roster.clear();

        assertSame(alice, game.playTurn().player());
    }
}

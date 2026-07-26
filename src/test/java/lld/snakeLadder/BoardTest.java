package lld.snakeLadder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    @Test
    void resolvesLandingSquares() {
        Board board = Board.builder().size(20).jump(new Jump(4, 14)).jump(new Jump(16, 6)).build();

        assertEquals(14, board.landingPosition(4));
        assertEquals(6, board.landingPosition(16));
        assertEquals(7, board.landingPosition(7), "a plain square is left alone");
        assertNull(board.jumpAt(7));
    }

    @Test
    void tellsSnakesAndLaddersApartByDirection() {
        assertTrue(new Jump(16, 6).isSnake());
        assertFalse(new Jump(16, 6).isLadder());
        assertTrue(new Jump(4, 14).isLadder());
        assertFalse(new Jump(4, 14).isSnake());
    }

    @Test
    void rejectsAJumpStartingOnTheLastSquare() {
        // Resolving it would move the winner off the finish, so nobody could ever win.
        Board.BoardBuilder builder = Board.builder().size(20).jump(new Jump(20, 2));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, builder::build);
        assertTrue(thrown.getMessage().contains("unwinnable"), thrown.getMessage());
    }

    @Test
    void allowsALadderEndingOnTheLastSquare() {
        Board board = Board.builder().size(20).jump(new Jump(15, 20)).build();

        assertEquals(20, board.landingPosition(15));
    }

    @Test
    void rejectsSquaresOffTheBoard() {
        assertThrows(IllegalArgumentException.class, () -> Board.builder().size(20).jump(new Jump(0, 4)).build());
        assertThrows(IllegalArgumentException.class, () -> Board.builder().size(20).jump(new Jump(4, 21)).build());
        assertThrows(IllegalArgumentException.class, () -> Board.builder().size(20).jump(new Jump(-1, 4)).build());
    }

    @Test
    void rejectsAJumpThatGoesNowhere() {
        assertThrows(IllegalArgumentException.class, () -> new Jump(4, 4));
    }

    @Test
    void rejectsTwoJumpsOnTheSameSquare() {
        Board board = Board.builder().size(20).jump(new Jump(4, 14)).build();

        IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> board.addJump(4, 2));
        assertEquals(14, board.landingPosition(4), "the original jump survives the rejection");
        assertTrue(thrown.getMessage().contains("already"), thrown.getMessage());
    }

    @Test
    void rejectsABoardWithNowhereToGo() {
        assertThrows(IllegalArgumentException.class, () -> Board.builder().size(1).build());
        assertThrows(IllegalArgumentException.class, () -> Board.builder().size(0).build());
    }
}

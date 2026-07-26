package lld.tictactoe;

/**
 * Exercises the game end to end: a row win, a diagonal win, a full-board draw,
 * a win landing on the very last cell, and the guard rails around bad moves.
 *
 * Turn taking is driven entirely by {@link BoardGame}, so a scenario is just the
 * sequence of cells played, alternating between the two players.
 */
public class TicTacToeDemo {

    public static void main(String[] args) {
        rowWin();
        diagonalWin();
        fullBoardDraw();
        winOnLastCell();
        guardRails();
    }

    private static void rowWin() {
        header("1. X completes the top row");
        play(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}, {0, 2}});
    }

    private static void diagonalWin() {
        header("2. X completes the main diagonal");
        play(new int[][]{{0, 0}, {0, 1}, {1, 1}, {0, 2}, {2, 2}});
    }

    private static void fullBoardDraw() {
        header("3. Every cell filled, nobody wins");
        play(new int[][]{{0, 0}, {0, 1}, {0, 2}, {1, 1}, {1, 0}, {1, 2}, {2, 1}, {2, 0}, {2, 2}});
    }

    private static void winOnLastCell() {
        header("4. The ninth move wins, it is not a draw");
        play(new int[][]{{0, 0}, {0, 1}, {0, 2}, {1, 0}, {1, 1}, {1, 2}, {2, 1}, {2, 0}, {2, 2}});
    }

    private static void guardRails() {
        header("5. Rejected moves");

        BoardGame game = newGame();
        game.move(1, 1);

        expectRejected("play an occupied cell", () -> game.move(1, 1));
        expectRejected("play off the board", () -> game.move(3, 0));
        expectRejected("board smaller than 3x3", () -> new Board(2));
        expectRejected("EMPTY as a player symbol", () -> new Board(3).move(0, 0, Symbol.EMPTY));
        expectRejected("both players on X",
                () -> new BoardGame(3, new Player("Alice", Symbol.X), new Player("Bob", Symbol.X)));

        BoardGame finished = newGame();
        for (int[] cell : new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}, {0, 2}}) {
            finished.move(cell[0], cell[1]);
        }
        expectRejected("move after the game is won", () -> finished.move(2, 2));

        System.out.println();
    }

    private static void play(int[][] cells) {
        BoardGame game = newGame();

        for (int[] cell : cells) {
            Player mover = game.currentPlayer();
            MoveResult result = game.move(cell[0], cell[1]);
            System.out.printf("%s plays (%d,%d) -> %s%n", mover.getName(), cell[0], cell[1], describe(result));
        }

        System.out.println();
        System.out.print(game.getBoard().render());
        System.out.println();
    }

    private static String describe(MoveResult result) {
        return switch (result.state()) {
            case IN_PROGRESS -> "game continues";
            case WON -> result.winner().getName() + " won the game !";
            case DRAW -> "game is a draw !";
        };
    }

    private static void expectRejected(String label, Runnable action) {
        try {
            action.run();
            System.out.printf("  %-32s NOT REJECTED (bug)%n", label);
        } catch (RuntimeException e) {
            System.out.printf("  %-32s rejected: %s%n", label, e.getMessage());
        }
    }

    private static BoardGame newGame() {
        return new BoardGame(3, new Player("Alice", Symbol.X), new Player("Bob", Symbol.O));
    }

    private static void header(String title) {
        System.out.println("=== " + title + " ===");
    }
}

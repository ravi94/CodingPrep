package lld.tictactoe;

public enum GameState {
    IN_PROGRESS,
    WON,
    DRAW;

    public boolean isOver() {
        return this != IN_PROGRESS;
    }
}

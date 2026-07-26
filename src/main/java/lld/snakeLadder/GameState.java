package lld.snakeLadder;

/**
 * There is no draw: play continues until somebody lands on the last square.
 */
public enum GameState {
    IN_PROGRESS,
    WON;

    public boolean isOver() {
        return this != IN_PROGRESS;
    }
}

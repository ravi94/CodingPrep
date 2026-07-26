package lld.tictactoe;

public enum Symbol {
    X('X'), O('O'), EMPTY(' ');

    private final char value;

    Symbol(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    /** EMPTY marks an unplayed cell, so it is never a symbol a player may play. */
    public boolean isPlayable() {
        return this != EMPTY;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

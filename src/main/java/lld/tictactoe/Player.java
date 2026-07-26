package lld.tictactoe;

import lombok.AllArgsConstructor;
import lombok.Value;

/** Immutable: a player's symbol must not change once a game is under way. */
@Value
@AllArgsConstructor
public class Player {
    String name;
    Symbol symbol;
}

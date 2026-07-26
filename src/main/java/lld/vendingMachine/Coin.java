package lld.vendingMachine;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum Coin {
    ONE(1),
    TWO(2),
    FIVE(5),
    TEN(10);

    int value;

    public int getValue() {
        return value;
    }
}

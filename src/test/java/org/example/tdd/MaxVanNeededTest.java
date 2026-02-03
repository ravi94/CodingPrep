package org.example.tdd;

import coding.retail.minVanNeeded.MinVanNeeded;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MaxVanNeededTest {
    MinVanNeeded maxVanNeeded = new MinVanNeeded();

    @Test
    public void maxVanNeededTest() {
        // Test implementation goes here
        int [][] windows = {
                {1, 4},
                {2, 5},
                {7, 9}
        };
        int actual = maxVanNeeded.calculateMinVans(windows);
        Assertions.assertEquals(2, actual);
    }
}

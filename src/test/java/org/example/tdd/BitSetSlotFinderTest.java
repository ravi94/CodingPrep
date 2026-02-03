package org.example.tdd;

import coding.retail.deliverySlotFinder.BitSetSlotFinder;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BitSetSlotFinderTest {

    @Test
    void testCapacityOfTwo() {
        // Store closes at minute 1000, Capacity = 2 vans
        BitSetSlotFinder finder = new BitSetSlotFinder(2, 1000);

        // Van 1 is busy 500-600
        finder.bookSlot(500, 600);

        // Even though Van 1 is busy, we should find a slot at 500
        // because Van 2 is free.
        Optional<Integer> slot1 = finder.findNextAvailable(500, 60);
        assertEquals(500, slot1.get(), "Should find slot because capacity is not reached");

        // Van 2 is now also busy 500-600
        finder.bookSlot(500, 600);

        // Now both vans are busy, so searching at 500 should return 600
        Optional<Integer> slot2 = finder.findNextAvailable(500, 60);
        assertEquals(600, slot2.get(), "Should skip to 600 as both vans are busy");
    }
}

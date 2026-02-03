package org.example.tdd;

import coding.retail.shiftMerge.ShiftMerger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class ShiftMergerTest {
    private final ShiftMerger merger = new ShiftMerger();

    @Test
    void testOverlapAndTouchingShifts() {
        List<ShiftMerger.Shift> shifts = Arrays.asList(
                new ShiftMerger.Shift("Cashier", 800, 1000),
                new ShiftMerger.Shift("Cashier", 1000, 1200), // Touching
                new ShiftMerger.Shift("Cashier", 1100, 1300)  // Overlapping
        );

        Map<String, List<int[]>> result = merger.mergeShifts(shifts);

        Assertions.assertEquals(1, result.get("Cashier").size());
        Assertions.assertArrayEquals(new int[]{800, 1300}, result.get("Cashier").get(0));
    }

    @Test
    void testDifferentRolesDoNotMerge() {
        List<ShiftMerger.Shift> shifts = Arrays.asList(
                new ShiftMerger.Shift("Manager", 800, 1200),
                new ShiftMerger.Shift("Security", 900, 1100)
        );

        Map<String, List<int[]>> result = merger.mergeShifts(shifts);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(800, result.get("Manager").get(0)[0]);
        Assertions.assertEquals(900, result.get("Security").get(0)[0]);
    }
}
package org.example.tdd;

import coding.retail.WarehouseNavigator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WarehouseNavigatorTest {
    @Test
    void testShortestPathAroundObstacle() {
        WarehouseNavigator nav = new WarehouseNavigator();
        int[][] warehouse = {
                {0, 0, 0},
                {0, 1, 0}, // Obstacle in the middle
                {0, 0, 0}
        };

        int result = nav.findShortestPath(warehouse, new int[]{0,0}, new int[]{2,2});

        // Path: (0,0) -> (0,1) -> (0,2) -> (1,2) -> (2,2) = 4 steps
        assertEquals(4, result);
    }
}

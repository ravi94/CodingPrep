package org.example.tdd;
import coding.retail.DeliveryOptimizer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class DeliveryOptimizerTest {
    @Test
    void testRouteDoesNotExceedCapacity() {
        DeliveryOptimizer optimizer = new DeliveryOptimizer();
        DeliveryOptimizer.Location depot = new DeliveryOptimizer.Location(0, 0);

        List<DeliveryOptimizer.Delivery> drops = List.of(
                new DeliveryOptimizer.Delivery("A", new DeliveryOptimizer.Location(1, 1), 50),
                new DeliveryOptimizer.Delivery("B", new DeliveryOptimizer.Location(2, 2), 60)
        );

        // Capacity is 100, so it shouldn't be able to take both A (50) and B (60)
        List<DeliveryOptimizer.Delivery> route = optimizer.optimizeRoute(depot, drops, 100);

        assertEquals(1, route.size(), "Should only pick one delivery due to capacity");
        assertEquals("A", route.get(0).id(), "Should pick the cheapest delivery");
    }

    @Test
    void testNearestNeighborLogic() {
        DeliveryOptimizer optimizer = new DeliveryOptimizer();
        DeliveryOptimizer.Location depot = new DeliveryOptimizer.Location(0, 0);

        List<DeliveryOptimizer.Delivery> drops = List.of(
                new DeliveryOptimizer.Delivery("Far", new DeliveryOptimizer.Location(10, 10), 10),
                new DeliveryOptimizer.Delivery("Near", new DeliveryOptimizer.Location(1, 1), 10)
        );

        List<DeliveryOptimizer.Delivery> route = optimizer.optimizeRoute(depot, drops, 100);

        assertEquals("Near", route.get(0).id(), "Should visit the closest drop first");
    }
}
package coding.retail;

import java.util.*;

public class DeliveryOptimizer {

    public record Location(int x, int y) {
        public int distanceTo(Location other) {
            return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
        }
    }

    public record Delivery(String id, Location location, int weight) {}

    public List<Delivery> optimizeRoute(Location depot, List<Delivery> drops, int maxCapacity) {
        List<Delivery> route = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Location currentLocation = depot;
        int currentWeight = 0;

        while (visited.size() < drops.size()) {
            Delivery closest = null;
            int minDistance = Integer.MAX_VALUE;

            for (Delivery drop : drops) {
                if (!visited.contains(drop.id())) {
                    // SDE 3 Check: Respect van capacity
                    if (currentWeight + drop.weight() <= maxCapacity) {
                        int dist = currentLocation.distanceTo(drop.location());
                        if (dist < minDistance) {
                            minDistance = dist;
                            closest = drop;
                        }
                    }
                }
            }

            if (closest == null) break; // Capacity reached or unreachable

            visited.add(closest.id());
            route.add(closest);
            currentWeight += closest.weight();
            currentLocation = closest.location();
        }

        return route;
    }
}
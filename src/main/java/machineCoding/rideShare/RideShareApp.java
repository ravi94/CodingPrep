package machineCoding.rideShare;

import machineCoding.rideShare.entities.Ride;
import machineCoding.rideShare.selectionStrategy.MostVacantStrategy;
import machineCoding.rideShare.service.RiderService;

import java.util.List;
import java.util.Optional;

public class RideShareApp {
    public static void main(String[] args) {
        RiderService service = new RiderService();

        service.offerRide("Rohan", "Swift", 2, "Bangalore", "Mysore");
        service.offerRide("Shashank", "Baleno", 3, "Bangalore", "Mysore");
        service.offerRide("Nandini", "Prime", 1, "Mysore", "Coorg");

        // 2. Select Ride (Direct)
        System.out.println("--- Searching Direct Bangalore to Mysore ---");
        Optional<Ride> result = service.selectRide("Bangalore", "Mysore", 1, new MostVacantStrategy());
        result.ifPresent(r -> System.out.println("Selected Driver: " + r.getDriverName()));

        // 3. Select Ride (Multi-hop)
        System.out.println("\n--- Searching Multi-hop Bangalore to Coorg ---");
        List<Ride> path = service.selectRideMulitHop("Bangalore", "Coorg", 1);
        if (path.isEmpty()) {
            System.out.println("No path found.");
        } else {
            path.forEach(r -> System.out.println("Leg: " + r.getOrigin() + " to " + r.getDestination() + " via " + r.getDriverName()));
        }
    }
}

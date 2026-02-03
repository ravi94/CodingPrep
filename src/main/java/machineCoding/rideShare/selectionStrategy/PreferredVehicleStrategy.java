package machineCoding.rideShare.selectionStrategy;

import machineCoding.rideShare.entities.Ride;

import java.util.List;
import java.util.Optional;

public class PreferredVehicleStrategy implements RideSelectionStrategy {
    private String preferredModel;

    public PreferredVehicleStrategy(String model){
        this.preferredModel= model;
    }

    @Override
    public Optional<Ride> selectRide(List<Ride> availableRides, String origin, String destination, int seats) {
        return availableRides.stream().filter(r-> r.canFullFill(origin, destination,seats))
                .filter(r-> r.getVehicleModel().equals(this.preferredModel)).findFirst();
    }
}

package machineCoding.rideShare.selectionStrategy;

import machineCoding.rideShare.entities.Ride;

import java.util.List;
import java.util.Optional;

public interface RideSelectionStrategy {
    Optional<Ride> selectRide(List<Ride> availableRides , String origin , String destination,int seats);
}

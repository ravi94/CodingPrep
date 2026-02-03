package machineCoding.rideShare.selectionStrategy;



import machineCoding.rideShare.entities.Ride;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MostVacantStrategy implements RideSelectionStrategy {

    @Override
    public Optional<Ride> selectRide(List<Ride> availableRides,String origin ,String destination,int seats) {
        return availableRides.stream().filter(r-> r.canFullFill(origin,destination,seats))
                .max(Comparator.comparingInt(r->r.getAvailableSeat().get()));
    }
}

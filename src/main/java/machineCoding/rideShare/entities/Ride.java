package machineCoding.rideShare.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
public class Ride {
    private String id;
    private String driverName;
    private String vehicleModel;
    private String origin;
    private String destination;
    private AtomicInteger availableSeat;
    private boolean isActive;

    public boolean canFullFill(String origin, String destination , int seat){
        return this.isActive && this.availableSeat.get() > seat
                && this.origin.equalsIgnoreCase(origin) && this.destination.equalsIgnoreCase(destination);
    }
}

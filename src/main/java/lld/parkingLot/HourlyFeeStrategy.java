package lld.parkingLot;

import java.util.HashMap;
import java.util.Map;

public class HourlyFeeStrategy implements FeeStrategy{
    private static final long MILLIS_PER_HOUR = 60L * 60 * 1000;

    static Map<VehicleType, Integer> vehicleFeeHourly = new HashMap<>();

    static{
        vehicleFeeHourly.put(VehicleType.MOTORCYLE, 1);
        vehicleFeeHourly.put(VehicleType.CAR, 2);
        vehicleFeeHourly.put(VehicleType.TRUCK, 2);
    }


    @Override
    public double calculateParkingFee(Ticket ticket, long outTime) {
        long durationMillis = Math.max(0, outTime - ticket.inTime);
        // any started hour is a billed hour, with a one hour minimum
        long numHours = Math.max(1, (durationMillis + MILLIS_PER_HOUR - 1) / MILLIS_PER_HOUR);
        return vehicleFeeHourly.get(ticket.getVehicle().type)*numHours;
    }
}

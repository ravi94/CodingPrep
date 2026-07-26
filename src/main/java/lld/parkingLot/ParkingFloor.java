package lld.parkingLot;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Getter
public class ParkingFloor {

    private final int floorNum;

    private final List<ParkingSpot> parkingSpots;

    /** Same spots as {@link #parkingSpots}, indexed by type so assignment only scans candidates. */
    private final Map<VehicleType, List<ParkingSpot>> spotsByType;

    /**
     * Built through {@link #builder()}. {@code spotCounts} generates spots with
     * derived ids; {@code customSpots} lets a caller hand in ready-made ones.
     */
    @Builder
    private ParkingFloor(int floorNum,
                         @Singular Map<VehicleType, Integer> spotCounts,
                         @Singular List<ParkingSpot> customSpots) {
        if (floorNum <= 0)
            throw new IllegalArgumentException("floorNum must be positive, was " + floorNum);
        if (spotCounts.isEmpty() && customSpots.isEmpty())
            throw new IllegalStateException("floor " + floorNum + " has no spots");

        this.floorNum = floorNum;
        this.parkingSpots = new ArrayList<>();
        this.spotsByType = new EnumMap<>(VehicleType.class);

        // Copy into an EnumMap so spot ids are generated in a stable, type-ordered way.
        Map<VehicleType, Integer> orderedCounts = new EnumMap<>(VehicleType.class);
        orderedCounts.putAll(spotCounts);

        orderedCounts.forEach((vehicleType, count) -> {
            if (count == null || count < 0)
                throw new IllegalArgumentException("spot count for " + vehicleType + " must be >= 0");
            for (int i = 0; i < count; i++) {
                register(new ParkingSpot(spotId(floorNum, vehicleType, i), vehicleType, null));
            }
        });
        customSpots.forEach(this::register);
    }

    private void register(ParkingSpot spot) {
        parkingSpots.add(spot);
        spotsByType.computeIfAbsent(spot.getVehicleType(), type -> new ArrayList<>()).add(spot);
    }

    /** e.g. F2-CAR-03 : floor 2, third car spot. */
    private static String spotId(int floorNum, VehicleType vehicleType, int index){
        return String.format("F%d-%s-%02d", floorNum, vehicleType, index + 1);
    }

    boolean addParkingSpot(ParkingSpot spot){
        register(spot);
        return true;
    }

    boolean removeParkingSpot(ParkingSpot spot){
        spotsByType.getOrDefault(spot.getVehicleType(), List.of()).remove(spot);
        return this.parkingSpots.remove(spot);
    }

    /**
     * Returns the spot the vehicle was parked in, or null when this floor has no
     * usable spot left. The caller is expected to move on to the next floor.
     */
    ParkingSpot tryAssign(Vehicle vehicle) {
        for(ParkingSpot spot : spotsFor(vehicle.type)){
            if(spot.tryAssign(vehicle)){
                return spot;
            }
        }
        return null;
    }

    boolean isFree(Vehicle vehicle){
        return spotsFor(vehicle.type).stream().anyMatch(ParkingSpot::isFree);
    }

    long countFree(VehicleType vehicleType){
        return spotsFor(vehicleType).stream().filter(ParkingSpot::isFree).count();
    }

    private List<ParkingSpot> spotsFor(VehicleType vehicleType){
        return spotsByType.getOrDefault(vehicleType, List.of());
    }
}

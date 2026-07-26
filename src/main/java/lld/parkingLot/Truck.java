package lld.parkingLot;

// getters, equals and toString come from Vehicle - a subclass @Data would
// generate an equals that ignores regNum, making all Cars equal to each other.
public class Truck extends Vehicle{
    public Truck(String regNum) {
        super(regNum, VehicleType.TRUCK);
    }
}

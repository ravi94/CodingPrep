package lld.parkingLot;

// getters, equals and toString come from Vehicle - a subclass @Data would
// generate an equals that ignores regNum, making all Cars equal to each other.
public class Bike extends Vehicle{
    public Bike(String regNum) {
        super(regNum, VehicleType.MOTORCYLE);
    }
}

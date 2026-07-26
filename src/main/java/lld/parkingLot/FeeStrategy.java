package lld.parkingLot;

public interface FeeStrategy {
    double calculateParkingFee(Ticket ticket, long outTime);
}

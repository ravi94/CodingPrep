package lld.parkingLot;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Ticket{
    UUID id;
    Vehicle vehicle;
    ParkingSpot spot;
    long inTime;
}

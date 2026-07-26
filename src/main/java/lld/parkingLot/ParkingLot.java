package lld.parkingLot;

import lombok.Builder;
import lombok.Singular;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ParkingLot {
    List<ParkingFloor> parkingFloorList;
    FeeStrategy feeStrategy;
    Map<UUID,Ticket> tickets ;

    ParkingLot(FeeStrategy feeStrategy){
        this(feeStrategy, 3, 10);
    }

    @Builder
    private ParkingLot(FeeStrategy feeStrategy,
                         @Singular List<ParkingFloor> parkingFloors){

        this.feeStrategy = feeStrategy;
        this.parkingFloorList = parkingFloors;
        this.tickets = new HashMap<>();
    }

    ParkingLot(FeeStrategy feeStrategy, int numFloors, int spotsPerTypePerFloor){
        this.feeStrategy = feeStrategy;
        parkingFloorList = new ArrayList<>();
        tickets = new ConcurrentHashMap<>();
        for(int floorNum = 1; floorNum <= numFloors; floorNum++){
            parkingFloorList.add(ParkingFloor.builder()
                    .floorNum(floorNum)
                    .spotCount(VehicleType.MOTORCYLE, 10)
                    .spotCount(VehicleType.CAR, 10)
                    .spotCount(VehicleType.TRUCK, 10)
                    .build());
        }
    }

    boolean addParkingFloor(ParkingFloor parkingFloor){
        return this.parkingFloorList.add(parkingFloor);
    }

    boolean removeParkingFloor(ParkingFloor parkingFloor){
        return this.parkingFloorList.remove(parkingFloor);
    }

    Ticket park(Vehicle vehicle) throws Exception {
        for(ParkingFloor parkingFloor : parkingFloorList){
            ParkingSpot spot = parkingFloor.tryAssign(vehicle);
            if(spot != null){
                Ticket ticket = new Ticket(UUID.randomUUID(),vehicle,spot,System.currentTimeMillis());
                tickets.put(ticket.getId(),ticket);
                return ticket;
            }
        }
        throw new Exception("Parking is full for vehicle type " + vehicle.type);
    }

    double unpark(UUID ticketId, long outTime) throws Exception {
        Ticket ticket = tickets.remove(ticketId);
        if(ticket == null)
            throw new Exception("Unknown or already used ticket: " + ticketId);

        ticket.getSpot().release(ticket.getVehicle());
        return feeStrategy.calculateParkingFee(ticket, outTime);
    }

    double unpark(UUID ticketId) throws Exception {
        return unpark(ticketId, System.currentTimeMillis());
    }

    long countFree(VehicleType vehicleType){
        return parkingFloorList.stream().mapToLong((floor) -> floor.countFree(vehicleType)).sum();
    }
}

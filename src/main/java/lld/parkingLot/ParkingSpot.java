package lld.parkingLot;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ParkingSpot {
    final String id;
    VehicleType vehicleType;
    Vehicle current;

    synchronized boolean tryAssign(Vehicle vehicle){
        if(current != null || !vehicleType.equals(vehicle.type))
            return false;
        else{
            current = vehicle;
            return true;
        }
    }

    synchronized boolean isFree(){
        return current== null;
    }

    synchronized boolean release(Vehicle vehicle) throws Exception {
        if (current != null && vehicle.regNum.equals(current.regNum)){
            current = null; return true;
        }else{
            throw new Exception("Vehicle trying to release is not same as vehicle parked");
        }

    }

}

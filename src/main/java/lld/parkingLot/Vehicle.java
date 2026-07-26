package lld.parkingLot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@AllArgsConstructor
@Data
public abstract class Vehicle {
    final String regNum;
    final VehicleType type;


}

package lld.parkingLot;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum VehicleType {
    MOTORCYLE(1) ,
    CAR(2),
    TRUCK(3);

    int size;

    VehicleType(int size){
        this.size = size;
    }
}


package machineCoding.hotelRoomBooking.entities;

import lombok.Data;
import machineCoding.hotelRoomBooking.entities.enums.RoomStatus;
import machineCoding.hotelRoomBooking.entities.enums.RoomType;

import java.util.UUID;

@Data
public class Room {
    String roomId;
    String roomNumber;
    RoomType roomType;
    RoomStatus status;

    public Room(String roomNumber,RoomType roomType){
        this.roomId = UUID.randomUUID().toString();
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.status = RoomStatus.AVAILABLE;

    }

    @Override
    public String toString() {
        return String.format("Room %s (%s) - %s", roomNumber, roomType, status);
    }
}

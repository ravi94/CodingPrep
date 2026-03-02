package machineCoding.roomMeetingBooking.service;

import machineCoding.roomMeetingBooking.entities.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BookingManager {
    private final Map<String, Building> buildings = new ConcurrentHashMap<>();

    public void addBuilding(String buildingName){
        buildings.putIfAbsent(buildingName,new Building());
    }

    public void addFloor(String buildingName, int floorNum ){
        buildings.putIfAbsent(buildingName,new Building())
                .getFloors().putIfAbsent(floorNum,new Floor());
    }

    public void addRoom(String buildingName, int floorNum , String roomId ){
        buildings.putIfAbsent(buildingName,new Building())
                .getFloors().putIfAbsent(floorNum,new Floor())
                .getRooms().putIfAbsent(roomId,new Room(roomId));
    }

    public String bookRoom(String userId , String buildingName, int floorNum, String roomId, int start , int end ){
        if (end - start > 12) return "FAILED: Max 12 hours allowed";
        try{
            Room room = buildings.get(buildingName).getFloors().get(floorNum).getRooms().get(roomId);
            if (room.addBooking(new Booking(userId, new Slot(start , end)))) {
                return "SUCCESS: Room " + roomId + " booked for " + userId;
            }else
                return "FAILED: Slot unavailable";
        }catch (NullPointerException np){
            return "FAILED: Invalid Building/Floor/Room";
        }
    }
}

package machineCoding.roomBooking.entities;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Floor {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
}
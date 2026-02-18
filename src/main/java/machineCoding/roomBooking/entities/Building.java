package machineCoding.roomBooking.entities;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Building {
    private final Map<Integer,Floor> floors = new ConcurrentHashMap<>();
}

package machineCoding.roomBooking.entities;

import lombok.Data;

import java.util.UUID;

@Data
public class Booking {
    private final String id;
    private final String userId;
    private final Slot slot;

    public Booking( String userId, Slot slot){
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.slot = slot;
    }
}

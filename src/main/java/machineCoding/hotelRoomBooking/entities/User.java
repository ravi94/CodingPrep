package machineCoding.hotelRoomBooking.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    String userId;
    String name;
    String email;
    String phone;
}

package machineCoding.hotelRoomBooking.entities;

import lombok.Data;
import machineCoding.hotelRoomBooking.entities.enums.BookingStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Data
public class Booking {
    String bookingId;
    String userId;
    Room room;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    LocalDate bookingDate;
    double totalAmount;
    BookingStatus status;

    public Booking(String userId, Room room, LocalDate checkInDate,
                   LocalDate checkOutDate, double totalAmount) {
        this.bookingId = "BKG" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.userId = userId;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalAmount = totalAmount;
        this.status = BookingStatus.CONFIRMED;
        this.bookingDate = LocalDate.now();
    }

    public long getNumberOfNights(){
        return ChronoUnit.DAYS.between(checkInDate,checkOutDate);
    }

    @Override
    public String toString() {
        return String.format("Booking %s: %s from %s to %s (%d nights) - ₹%.2f [%s]",
                bookingId, room.getRoomNumber(), checkInDate, checkOutDate,
                getNumberOfNights(), totalAmount, status);
    }


}

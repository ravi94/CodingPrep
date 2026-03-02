package machineCoding.hotelRoomBooking.services.pricingStartegies;

import machineCoding.hotelRoomBooking.entities.enums.RoomType;

import java.time.LocalDate;

public interface PricingStrategy {
    double calculatePrice(RoomType roomType, LocalDate date);
    String getDescription();
}

package machineCoding.hotelRoomBooking.services.pricingStartegies;

import lombok.AllArgsConstructor;
import machineCoding.hotelRoomBooking.entities.enums.RoomType;

import java.time.DayOfWeek;
import java.time.LocalDate;

@AllArgsConstructor
public class WeekdayWeekendPricing implements PricingStrategy{
    private double weekdayMultiplier;
    private double weekendMultiplier;

    @Override
    public double calculatePrice(RoomType roomType, LocalDate date) {
        double basePrice = roomType.getBasePrice();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return basePrice * weekendMultiplier;
        }
        return basePrice * weekdayMultiplier;
    }

    @Override
    public String getDescription() {
        return String.format("Weekday: %.0f%%, Weekend: %.0f%%",
                weekdayMultiplier * 100, weekendMultiplier * 100);
    }
}

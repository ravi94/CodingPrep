package machineCoding.hotelRoomBooking.services.pricingStartegies;

import machineCoding.hotelRoomBooking.entities.enums.RoomType;

import java.time.LocalDate;
import java.time.Month;

public class SeasonalPricing implements PricingStrategy{
    @Override
    public double calculatePrice(RoomType roomType, LocalDate date) {
        double basePrice = roomType.getBasePrice();
        Month month = date.getMonth();

        // Peak season: December, January (1.5x)
        if (month == Month.DECEMBER || month == Month.JANUARY) {
            return basePrice * 1.5;
        }
        // High season: March-May, October-November (1.2x)
        else if (month == Month.MARCH || month == Month.APRIL || month == Month.MAY ||
                month == Month.OCTOBER || month == Month.NOVEMBER) {
            return basePrice * 1.2;
        }
        // Low season: June-September (0.8x)
        else {
            return basePrice * 0.8;
        }
    }

    @Override
    public String getDescription() {
        return "Seasonal pricing: Peak 150%, High 120%, Low 80%";
    }
}

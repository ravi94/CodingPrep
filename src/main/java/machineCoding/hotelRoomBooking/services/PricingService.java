package machineCoding.hotelRoomBooking.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import machineCoding.hotelRoomBooking.entities.enums.RoomType;
import machineCoding.hotelRoomBooking.services.pricingStartegies.PricingStrategy;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class PricingService {
    private PricingStrategy pricingStrategy;

    /**
     * Calculate total price for a booking
     */
    public double calculateTotalPrice(RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        double total = 0;
        LocalDate currentDate = checkIn;

        while (currentDate.isBefore(checkOut)) {
            double dailyPrice = pricingStrategy.calculatePrice(roomType, currentDate);
            total += dailyPrice;
            currentDate = currentDate.plusDays(1);
        }
        return total;
    }

    /**
     * Get price breakdown by date
     */
    public Map<LocalDate, Double> getPriceBreakdown(RoomType roomType,
                                                    LocalDate checkIn,
                                                    LocalDate checkOut) {
        Map<LocalDate, Double> breakdown = new LinkedHashMap<>();
        LocalDate currentDate = checkIn;

        while (currentDate.isBefore(checkOut)) {
            double dailyPrice = pricingStrategy.calculatePrice(roomType, currentDate);
            breakdown.put(currentDate, dailyPrice);
            currentDate = currentDate.plusDays(1);
        }

        return breakdown;
    }


}

package machineCoding.hotelRoomBooking.entities;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DateRange {
    LocalDate startDate;
    LocalDate endDate;

    public DateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean overlaps(DateRange other){
        return this.startDate.isBefore(other.endDate) && this.endDate.isAfter(other.startDate);
    }

    public boolean contains(LocalDate date){
        return  date.isBefore(this.endDate) && date.isAfter(this.startDate);
    }
}

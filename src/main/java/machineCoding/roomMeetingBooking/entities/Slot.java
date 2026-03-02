package machineCoding.roomMeetingBooking.entities;

import lombok.Data;

@Data
public class Slot{
    private final int start , end;

    public Slot(int start , int end){
        if (start >= end) throw new IllegalArgumentException("Start must be before end");
        this.start = start;
        this.end = end;
    }

    public boolean overlaps(Slot other) {
        return this.start < other.end && other.start < this.end;
    }

    public int getDuration(){
        return this.end - this.start;
    }
}

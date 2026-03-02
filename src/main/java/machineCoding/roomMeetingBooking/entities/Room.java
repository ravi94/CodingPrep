package machineCoding.roomMeetingBooking.entities;

import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class Room {
    private String id;
    private final ConcurrentSkipListSet<Booking> bookings ;
    private final ReentrantLock lock = new ReentrantLock();

    public Room(String id ){
        this.id = id;
        this.bookings = new ConcurrentSkipListSet<>(Comparator.comparingInt(b -> b.getSlot().getStart()));
    }

    public  boolean isAvailable(Slot slot){
        // Standard overlap: (StartA < EndB) && (EndA > StartB)
        return bookings.stream().noneMatch(b -> b.getSlot().overlaps(slot));
    }

    public boolean addBooking(Booking booking){
        lock.lock();
        try {
            if (isAvailable(booking.getSlot())) {
                bookings.add(booking);
                return true;
            }
            return false;
        } finally { // try is their just to run finally
            lock.unlock();
        }
    }

    public List<Slot> suggestSlots(Slot requested, int count) {
        List<Slot> suggestions = new ArrayList<>();
        int duration = requested.getDuration();
        int searchStart = requested.getStart();

        // Algorithm: Check every possible window of 'duration' starting from 'searchStart'
        while (suggestions.size() < count && searchStart + duration <= 24) {
            Slot potential = new Slot(searchStart, searchStart + duration);

            // Check if this window overlaps with any existing booking
            Optional<Booking> conflict = bookings.stream()
                    .filter(b -> b.getSlot().overlaps(potential))
                    .findFirst();

            if (conflict.isEmpty()) {
                suggestions.add(potential);
                searchStart += duration; // Move past this found slot
            } else {
                // Efficiency: If there's a conflict, skip to the end of the conflicting booking
                searchStart = conflict.get().getSlot().getEnd();
            }
        }
        return suggestions;
    }
}



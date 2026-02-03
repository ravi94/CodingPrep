package coding.retail.deliverySlotFinder;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class DeliverySlotFinder {

    public record Slot(int start, int end) {}

    public Optional<Slot> findNextAvailableSlot(List<Slot> bookedSlots, int capacity, int searchStart, int storeClose) {
        // 1. Map time points to change in van count
        ConcurrentSkipListMap<Integer, Integer> timeline = new ConcurrentSkipListMap<>();
        for (Slot s : bookedSlots) {
            timeline.merge(s.start, 1, Integer::sum);
            timeline.merge(s.end, -1, Integer::sum);
        }

        int activeVans = 0;
        int lastTime = searchStart;

        // Iterate through the timeline
        for (Map.Entry<Integer, Integer> entry : timeline.entrySet()) {
            int currentTime = entry.getKey();

            // If we found a gap where activeVans < capacity for at least 60 mins
            if (activeVans < capacity && (currentTime - lastTime) >= 100) { // Using 100 as 1 hour in HHmm format
                return Optional.of(new Slot(lastTime, lastTime + 100));
            }

            activeVans += entry.getValue();
            lastTime = Math.max(lastTime, currentTime);

            if (lastTime >= storeClose) break;
        }

        // Check if there is space after the last booked slot before store closes
        if (activeVans < capacity && (storeClose - lastTime) >= 100) {
            return Optional.of(new Slot(lastTime, lastTime + 100));
        }

        return Optional.empty();
    }
}


/*
SDE 3 Deep-Dive: Performance & Scalability
1. Time Complexity: $O(N \log N)$ where $N$ is the number of booked slots (due to sorting in the TreeMap). The traversal is $O(N)$.
2. Memory Complexity: $O(N)$ to store the timeline.
3. Interview Pivot: "What if we have 1,000 stores and millions of bookings?"
    * Senior Answer: We shouldn't calculate this on the fly for every request. We should use a BitSet or a Segment Tree to represent the 24-hour day in 5-minute increments. This allows $O(1)$ or $O(\log N)$ checks for availability.
    * Concurrency: Use Optimistic Locking (versioning) in the database when a customer actually claims the slot to prevent overbooking.

*
*
*
* */
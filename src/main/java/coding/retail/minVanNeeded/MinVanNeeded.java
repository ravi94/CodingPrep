package coding.retail.minVanNeeded;

import java.util.concurrent.ConcurrentSkipListMap;

public class MinVanNeeded {

    public int calculateMinVans(int[][] windows) {
        if (windows == null || windows.length == 0) return 0;

        // Key: Time, Value: Change in van count (+1 for start, -1 for end)
        // TreeMap keeps the time points naturally sorted
        ConcurrentSkipListMap<Integer, Integer> timeline = new ConcurrentSkipListMap<>();
        // use ConcurrentSkipListMap for thread safety if needed

        for (int[] window : windows) {
            timeline.merge(window[0], 1, Integer::sum);
            timeline.merge(window[1], -1, Integer::sum);
        }

        int maxVans = 0;
        int currentVans = 0;

        // Iterate through the sorted time points
        for (int delta : timeline.values()) {
            currentVans += delta;
            maxVans = Math.max(maxVans, currentVans);
        }

        return maxVans;
    }
}


/*
SDE 3 Bonus: Thread Safety
If this FleetManager is a singleton service in a Spring Boot app, and multiple threads are calling calculateMinVans, the current local-variable approach is safe.
However, if you were maintaining a Global Timeline that updates as bookings come in, you would replace TreeMap with a ConcurrentSkipListMap. This provides the same sorted properties but is safe for concurrent access without needing a heavy synchronized block.

*/
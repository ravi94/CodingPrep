package coding.retail.deliverySlotFinder;


import java.util.*;

public class BitSetSlotFinder {

    private final int capacity;
    private final BitSet[] occupancyLayers;
    private final int storeClose;

    public BitSetSlotFinder(int capacity, int storeClose) {
        this.capacity = capacity;
        this.storeClose = storeClose;
        // Each layer represents a 'level' of occupancy
        this.occupancyLayers = new BitSet[capacity];
        for (int i = 0; i < capacity; i++) {
            occupancyLayers[i] = new BitSet(storeClose + 1);
        }
    }

    /**
     * Books a slot by "filling up" the layers.
     */
    public boolean bookSlot(int start, int end) {
        for (int i = 0; i < capacity; i++) {
            // Find the first layer that has a gap during this time
            // In a simplified model, we fill the lowest available layer
            if (!occupancyLayers[i].get(start)) {
                occupancyLayers[i].set(start, end);
                return true;
            }
        }
        return false;
    }

    /**
     * A slot is available if the 'top' layer (capacity-1) is empty.
     */
    public Optional<Integer> findNextAvailable(int searchStart, int duration) {
        BitSet fullCapacityMap = occupancyLayers[capacity - 1];
        int current = searchStart;

        while (current + duration <= storeClose) {
            int nextBusyMinute = fullCapacityMap.nextSetBit(current);

            // If no busy minute exists or it's after our required window
            if (nextBusyMinute == -1 || nextBusyMinute >= current + duration) {
                return Optional.of(current);
            }

            // Skip past the busy minute
            current = nextBusyMinute + 1;
        }
        return Optional.empty();
    }
}

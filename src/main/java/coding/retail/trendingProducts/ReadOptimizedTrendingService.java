package coding.retail.trendingProducts;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

public class ReadOptimizedTrendingService {

    // Helper class to store product state in the sorted set
    record ProductScore(String id, long count) implements Comparable<ProductScore> {
        @Override
        public int compareTo(ProductScore other) {
            // Sort by count descending, then by ID for tie-breaking
            int res = Long.compare(other.count, this.count);
            return res != 0 ? res : this.id.compareTo(other.id);
        }
    }

    private final ConcurrentHashMap<String, LongAdder> counts = new ConcurrentHashMap<>();
    private final ConcurrentSkipListSet<ProductScore> topSet = new ConcurrentSkipListSet<>();

    public void recordEvent(String productId) {
        LongAdder counter = counts.computeIfAbsent(productId, k -> new LongAdder());

        // 1. Get current count before incrementing
        long oldCount = counter.sum();
        counter.increment();
        long newCount = oldCount + 1;

        // 2. Update the sorted set (O(log N))
        // We must remove the old score and add the new one to trigger a re-sort
        topSet.remove(new ProductScore(productId, oldCount));
        topSet.add(new ProductScore(productId, newCount));
        if( topSet.size() > 5){
            topSet.pollLast();
        }
    }

    public List<String> getTopK(int k) {
        // Read is now incredibly fast: O(K)
        return topSet.stream()
                .limit(k)
                .map(ProductScore::id)
                .toList();
    }
}
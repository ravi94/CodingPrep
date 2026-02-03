package coding.retail.trendingProducts;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

public class TrendingProducts {
    // ConcurrentHashMap + LongAdder is the most performant way to count in Java
    private final ConcurrentHashMap<String, LongAdder> counts = new ConcurrentHashMap<>();
    private final int k;

    public TrendingProducts(int k) {
        this.k = k;
    }

    public void addEvent(String productId) {
        // computeIfAbsent is atomic
        counts.computeIfAbsent(productId, key -> new LongAdder()).increment();
    }

    public List<String> getTopK() {
        return counts.entrySet().stream()
                // Sort by count descending
                .sorted((a, b) -> Long.compare(b.getValue().sum(), a.getValue().sum()))
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    /**
     * Optimized O(N log K) retrieval
     */
    public List<String> getTopK_withPQ() {
        if (counts.isEmpty()) return Collections.emptyList();

        // Min-Heap: Smallest frequency stays at the top (root)
        // This allows us to remove the "weakest" trending item easily
        PriorityQueue<Map.Entry<String, LongAdder>> minHeap = new PriorityQueue<>(
                Comparator.comparingLong(entry -> entry.getValue().sum())
        );

        for (Map.Entry<String, LongAdder> entry : counts.entrySet()) {
            minHeap.offer(entry);

            // If we exceed K, remove the item with the lowest frequency
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        // Extract results and reverse to get descending order
        List<String> result = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            result.add(minHeap.poll().getKey());
        }
        Collections.reverse(result);
        return result;
    }
}
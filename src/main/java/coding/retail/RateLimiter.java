package coding.retail;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {

    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();
    private final int capacity;
    private final long refillRateNanos; // Time to generate 1 token

    public RateLimiter(int maxBurst, int tokensPerSecond) {
        this.capacity = maxBurst;
        this.refillRateNanos = 1_000_000_000L / tokensPerSecond;
    }

    public boolean allowRequest(String userId) {
        Bucket bucket = userBuckets.computeIfAbsent(userId, k -> new Bucket(capacity));
        return bucket.refillAndConsume();
    }

    private class Bucket {
        private long tokens;
        private long lastRefillTimestamp;

        public Bucket(long capacity) {
            this.tokens = capacity;
            this.lastRefillTimestamp = System.nanoTime();
        }

        // Synchronized to handle concurrent requests from the same user
        public synchronized boolean refillAndConsume() {
            long now = System.nanoTime();
            long nanosSinceLastRefill = now - lastRefillTimestamp;

            // Calculate how many tokens were generated in the elapsed time
            long tokensToAdd = nanosSinceLastRefill / refillRateNanos;

            if (tokensToAdd > 0) {
                tokens = Math.min(capacity, tokens + tokensToAdd);
                lastRefillTimestamp = now;
            }

            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }
    }
}
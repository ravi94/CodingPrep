package org.example.tdd;

import coding.retail.ConcurrentLRUCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class ConcurrentLRUCacheTest {
    @Test
    void testConcurrencyStability() throws InterruptedException {
        ConcurrentLRUCache<Integer, String> cache = new ConcurrentLRUCache<>(10);
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int val = i;
            executor.submit(() -> {
                try {
                    cache.put(val, "Value-" + val);
                    cache.get(val);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        // Cache should not exceed capacity and should not crash
        assertTrue(cache.getPendingCount() <= 10);
    }

    private ConcurrentLRUCache<Integer, String> cache;

    @BeforeEach
    void setUp() {
        // Initialize with a small capacity to easily test eviction
        cache = new ConcurrentLRUCache<>(3);
    }

    @Test
    void testBasicPutAndGet() {
        cache.put(1, "Milk");
        cache.put(2, "Bread");

        assertEquals("Milk", cache.get(1));
        assertEquals("Bread", cache.get(2));
        assertEquals(2, cache.getPendingCount());
    }

    @Test
    void testUpdateValue() {
        cache.put(1, "Milk");
        cache.put(1, "Skimmed Milk"); // Updating same key

        assertEquals("Skimmed Milk", cache.get(1));
        assertEquals(1, cache.getPendingCount());
    }

    @Test
    void testEvictionPolicy() {
        cache.put(1, "Milk");
        cache.put(2, "Bread");
        cache.put(3, "Eggs");

        // At this point: [Eggs, Bread, Milk]
        // Adding 4th item should evict the oldest (Milk)
        cache.put(4, "Cereal");

        assertNull(cache.get(1), "Milk should have been evicted");
        assertEquals("Cereal", cache.get(4));
        assertEquals(3, cache.getPendingCount());
    }

    @Test
    void testGetPromotesToMRU() {
        cache.put(1, "Milk");
        cache.put(2, "Bread");
        cache.put(3, "Eggs");

        // Accessing Milk (1) makes it the Most Recently Used
        cache.get(1);

        // Now adding 4 should evict Bread (2), NOT Milk (1)
        cache.put(4, "Cereal");

        assertNotNull(cache.get(1), "Milk should still be in cache because it was accessed");
        assertNull(cache.get(2), "Bread should have been evicted as the LRU item");
    }

    @Test
    void testGetNonExistentKey() {
        assertNull(cache.get(999));
    }
}

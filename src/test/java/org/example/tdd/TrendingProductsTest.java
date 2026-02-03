package org.example.tdd;


import coding.retail.trendingProducts.TrendingProducts;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class TrendingProductsTest {

    @Test
    void testBasicTopK() {
        TrendingProducts trending = new TrendingProducts(2);

        // Setup data
        trending.addEvent("Milk");
        trending.addEvent("Milk");
        trending.addEvent("Bread");
        trending.addEvent("Bread");
        trending.addEvent("Bread");
        trending.addEvent("Apple");

        List<String> topK = trending.getTopK();

        // Assertions
        assertEquals(2, topK.size());
        assertEquals("Bread", topK.get(0)); // Most frequent
        assertEquals("Milk", topK.get(1));  // Second most frequent
    }

    @Test
    void testConcurrencySafety() throws InterruptedException {
        int k = 3;
        int numberOfThreads = 10;
        int updatesPerThread = 1000;
        TrendingProducts trending = new TrendingProducts(k);

        // We use a Thread Pool to simulate 10 users adding items simultaneously
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.execute(() -> {
                for (int j = 0; j < updatesPerThread; j++) {
                    trending.addEvent("Milk"); // Total should be 10,000
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Verify that the counts are accurate despite concurrent updates
        // Since we only added Milk, it should be the top item
        List<String> topK = trending.getTopK();

        assertFalse(topK.isEmpty());
        assertEquals("Milk", topK.get(0));
    }

    @Test
    void testTopKWithManyProducts() {
        TrendingProducts trending = new TrendingProducts(3);

        // Simulating many products with different frequencies
        trending.addEvent("Apple");  // 1

        trending.addEvent("Banana"); // 2
        trending.addEvent("Banana");

        trending.addEvent("Cider");  // 3
        trending.addEvent("Cider");
        trending.addEvent("Cider");

        trending.addEvent("Donut");  // 4
        trending.addEvent("Donut");
        trending.addEvent("Donut");
        trending.addEvent("Donut");

        List<String> result = trending.getTopK_withPQ();

        // Should return Donut, Cider, Banana (Top 3)
        assertEquals(3, result.size());
        assertEquals("Donut", result.get(0));
        assertEquals("Cider", result.get(1));
        assertEquals("Banana", result.get(2));
        assertFalse(result.contains("Apple")); // Apple was filtered out
    }
}
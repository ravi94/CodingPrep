package org.example.tdd;

import coding.retail.IdempotencyManager;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class IdempotencyManagerTest {

    @Test
    void testIdempotentFlow() {
        IdempotencyManager<String> manager = new IdempotencyManager<>();
        String key = "unique-order-123";

        // 1. First attempt succeeds
        assertTrue(manager.startRequest(key));
        manager.completeRequest(key, "OrderSuccessObj");

        // 2. Second attempt with same key should fail to start
        assertFalse(manager.startRequest(key));

        // 3. Second attempt should retrieve the cached response
        assertEquals("OrderSuccessObj", manager.getRecord(key).response());
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        IdempotencyManager<String> manager = new IdempotencyManager<>();
        String key = "race-key";
        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                if (manager.startRequest(key)) {
                    successCount.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);

        assertEquals(1, successCount.get(), "Only one thread should have successfully started the request");
    }
}

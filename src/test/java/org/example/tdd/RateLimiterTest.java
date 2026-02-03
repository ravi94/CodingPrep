package org.example.tdd;

import coding.retail.RateLimiter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RateLimiterTest {

    @Test
    void testRateLimiting() throws InterruptedException {
        // 5 tokens max, refills at 10 per second
        RateLimiter limiter = new RateLimiter(5, 10);

        // Burst: first 5 should pass
        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.allowRequest("user-1"));
        }

        // 6th should fail immediately
        assertFalse(limiter.allowRequest("user-1"));

        // Wait 100ms (should generate 1 token)
        Thread.sleep(110);
        assertTrue(limiter.allowRequest("user-1"));
    }
}

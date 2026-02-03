package org.example.tdd;

import coding.retail.shiftMerge.ThreadSafeRealtimeShiftMerger;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThreadSafeRealtimeShiftMergerTest {

    @Test
    public void testMerger() throws InterruptedException {
        ThreadSafeRealtimeShiftMerger merger = new ThreadSafeRealtimeShiftMerger();
        int threadCount = 100;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int start = i * 10; // Creating many small overlapping shifts
            final int end = start + 15;
            service.submit(() -> {
                try {
                    merger.addShift("Bakery", start, end);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // Wait for all threads
        List<int[]> result = merger.getMergedShifts("Bakery");

        // Since they all overlap (start+10, end+15), they should eventually merge into 1
        assertEquals(1, result.size());
        assertEquals(0, result.get(0)[0]);

    }
}

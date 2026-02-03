package org.example.tdd;

import coding.retail.trendingProducts.ReadOptimizedTrendingService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReadOptimizedTrendingServiceTest {
    ReadOptimizedTrendingService service = new ReadOptimizedTrendingService();

    @Test
    void testRankingUpdatesCorrectly() {



        service.recordEvent("Bread"); // Count 1
        service.recordEvent("Milk");  // Count 1
        service.recordEvent("Milk");  // Count 2

        List<String> top = service.getTopK(1);
        assertEquals("Milk", top.get(0));

        service.recordEvent("Bread");
        service.recordEvent("Bread"); // Bread is now 3

        assertEquals("Bread", service.getTopK(1).get(0));
    }
}

package org.example.tdd;

import coding.retail.deliverySlotFinder.DeliverySlotFinder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeliverySlotFinderTest {
    @Test
    void testSlotAvailableWhenUnderCapacity() {
        DeliverySlotFinder finder = new DeliverySlotFinder();
        List<DeliverySlotFinder.Slot> booked = List.of(
                new DeliverySlotFinder.Slot(900, 1000)
        );

        // Store can handle 2 vans. 1 is booked. 9:00 should still be available.
        var result = finder.findNextAvailableSlot(booked, 2, 900, 1200);

        assertTrue(result.isPresent());
        assertEquals(900, result.get().start());
    }

    @Test
    void testSlotFullWhenAtCapacity() {
        DeliverySlotFinder finder = new DeliverySlotFinder();
        List<DeliverySlotFinder.Slot> booked = List.of(
                new DeliverySlotFinder.Slot(900, 1000),
                new DeliverySlotFinder.Slot(900, 1000)
        );

        // Capacity is 2, and 2 are booked. Must look after 10:00.
        var result = finder.findNextAvailableSlot(booked, 2, 900, 1200);

        assertTrue(result.isPresent());
        assertEquals(1000, result.get().start());
    }
}

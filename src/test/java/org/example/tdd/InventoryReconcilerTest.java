package org.example.tdd;

import coding.retail.inventoryDiff.InventoryReconciler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InventoryReconcilerTest {
    @Test
    void testReconciliationMismatches() {
        InventoryReconciler reconciler = new InventoryReconciler();

        List<InventoryReconciler.StockItem> system = List.of(
                new InventoryReconciler.StockItem("MILK-01", 10),
                new InventoryReconciler.StockItem("BREAD-02", 5)
        );

        List<InventoryReconciler.StockItem> physical = List.of(
                new InventoryReconciler.StockItem("MILK-01", 8), // Mismatch
                new InventoryReconciler.StockItem("EGG-03", 12)   // Extra
        );

        List<InventoryReconciler.Discrepancy> results = reconciler.reconcile(system, physical);

        // Should find: MILK Mismatch, BREAD Missing, EGG Extra
        assertEquals(3, results.size());
    }
}

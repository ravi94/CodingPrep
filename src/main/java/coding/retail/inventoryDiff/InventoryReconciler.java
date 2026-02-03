package coding.retail.inventoryDiff;

import java.util.*;
import java.util.stream.Collectors;

public class InventoryReconciler {

    public record StockItem(String sku, int quantity) {}

    public enum DiffType { MISSING_IN_PHYSICAL, EXTRA_IN_PHYSICAL, QUANTITY_MISMATCH }

    public record Discrepancy(String sku, DiffType type, int expected, int actual) {}

    public List<Discrepancy> reconcile(List<StockItem> systemStock, List<StockItem> physicalStock) {
        Map<String, Integer> physicalMap = physicalStock.stream()
                .collect(Collectors.toMap(StockItem::sku, StockItem::quantity));

        List<Discrepancy> discrepancies = new ArrayList<>();
        Set<String> processedSkus = new HashSet<>();

        // 1. Check System items against Physical
        for (StockItem systemItem : systemStock) {
            String sku = systemItem.sku();
            processedSkus.add(sku);

            if (!physicalMap.containsKey(sku)) {
                discrepancies.add(new Discrepancy(sku, DiffType.MISSING_IN_PHYSICAL, systemItem.quantity(), 0));
            } else {
                int physicalQty = physicalMap.get(sku);
                if (physicalQty != systemItem.quantity()) {
                    discrepancies.add(new Discrepancy(sku, DiffType.QUANTITY_MISMATCH, systemItem.quantity(), physicalQty));
                }
            }
        }

        // 2. Check for "Extra" items in Physical that weren't in System
        for (StockItem physicalItem : physicalStock) {
            if (!processedSkus.contains(physicalItem.sku())) {
                discrepancies.add(new Discrepancy(physicalItem.sku(), DiffType.EXTRA_IN_PHYSICAL, 0, physicalItem.quantity()));
            }
        }

        return discrepancies;
    }
}
package org.example.tdd;

import coding.retail.categoryHierarchy.MemoizedCategoryService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemoizedCategoryServiceTest {
    @Test
    void testCacheInvalidationOnAncestor() {
        MemoizedCategoryService service = new MemoizedCategoryService();
        service.addRelationship("Fresh", "Dairy");

        // First call: Populates cache for "Fresh"
        List<String> initial = service.getAllSubCategories("Fresh");
        assertEquals(1, initial.size()); // Just "Dairy"

        // Add a grandchild
        service.addRelationship("Dairy", "Milk");

        // Second call: Should NOT return stale data. Should return "Dairy" and "Milk"
        List<String> updated = service.getAllSubCategories("Fresh");
        assertEquals(2, updated.size());
        assertTrue(updated.contains("Milk"));
    }
}

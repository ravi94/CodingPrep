package org.example.tdd;

import coding.retail.categoryHierarchy.CategoryHierarchyService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CategoryHierarchyServiceTest {

    @Test
    void testRootFinding() {
        CategoryHierarchyService service = new CategoryHierarchyService();
        service.addRelationship("Fresh Food", "Dairy");
        service.addRelationship("Dairy", "Milk");
        service.addRelationship("Milk", "Whole Milk");

        assertEquals("Fresh Food", service.getRootCategory("Whole Milk"));
    }

    @Test
    void testCircularDependencyDetection() {
        CategoryHierarchyService service = new CategoryHierarchyService();
        service.addRelationship("A", "B");
        service.addRelationship("B", "A");

        assertThrows(IllegalStateException.class, () -> service.getRootCategory("A"));
    }
}

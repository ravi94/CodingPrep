package org.example.tdd;

import coding.retail.discounts.DiscountEngine;
import coding.retail.discounts.Product;
import coding.retail.discounts.promotions.BogoPromotion;
import coding.retail.discounts.promotions.CategoryDiscountPromotion;
import coding.retail.discounts.promotions.PromotionStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountEngineTest {
    @Test
    void testComplexBasketDiscounts() {
        Product milk = new Product("1", "Milk", new BigDecimal("1.20"), "Dairy");
        Product bread = new Product("2", "Bread", new BigDecimal("1.00"), "Bakery");

        // Setup Promotions
        List<PromotionStrategy> promos = List.of(
                new BogoPromotion("1"), // BOGO on Milk
                new CategoryDiscountPromotion("Bakery", 10.0) // 10% off Bakery
        );

        DiscountEngine engine = new DiscountEngine(promos);

        // Basket: 2 Milk (1 should be free), 1 Bread (10% off)
        List<Product> basket = List.of(milk, milk, bread);

        BigDecimal totalDiscount = engine.getTotalDiscount(basket);

        // Expected: 1.20 (Milk) + 0.10 (Bread) = 1.30
        assertEquals(new BigDecimal("1.300"), totalDiscount);
    }
}

package coding.retail.discounts.promotions;

import coding.retail.discounts.Product;

import java.math.BigDecimal;
import java.util.List;

// 1. Concrete Strategy: Buy 1 Get 1 Free (on specific product)
public class BogoPromotion implements PromotionStrategy {
    private final String productId;

    public BogoPromotion(String productId) { this.productId = productId; }

    @Override
    public BigDecimal calculateDiscount(List<Product> items) {
        long count = items.stream().filter(p -> p.id().equals(productId)).count();
        if (count < 2) return BigDecimal.ZERO;

        BigDecimal unitPrice = items.stream().filter(p -> p.id().equals(productId))
                .findFirst().get().price();

        // Every 2nd item is free
        long freeItems = count / 2;
        return unitPrice.multiply(BigDecimal.valueOf(freeItems));
    }
}
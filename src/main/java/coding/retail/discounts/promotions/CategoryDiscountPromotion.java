package coding.retail.discounts.promotions;

import coding.retail.discounts.Product;

import java.math.BigDecimal;
import java.util.List;

// 3. Concrete Strategy: 10% off specific Category
public class CategoryDiscountPromotion implements PromotionStrategy {
    private final String category;
    private final double percentage;

    public CategoryDiscountPromotion(String category, double percentage) {
        this.category = category;
        this.percentage = percentage;
    }

    @Override
    public BigDecimal calculateDiscount(List<Product> items) {
        return items.stream()
                .filter(p -> p.category().equals(category))
                .map(p -> p.price().multiply(BigDecimal.valueOf(percentage / 100.0)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
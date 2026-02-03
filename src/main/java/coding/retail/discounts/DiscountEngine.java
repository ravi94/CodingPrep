package coding.retail.discounts;

import coding.retail.discounts.promotions.PromotionStrategy;

import java.math.BigDecimal;
import java.util.List;

public class DiscountEngine {
    private final List<PromotionStrategy> activePromotions;

    public DiscountEngine(List<PromotionStrategy> promotions) {
        this.activePromotions = promotions;
    }

    public void addPromotions(PromotionStrategy promotion) {
        this.activePromotions.add(promotion);
    }

    public BigDecimal getTotalDiscount(List<Product> items) {
        return activePromotions.stream()
                .map(strategy -> strategy.calculateDiscount(items))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
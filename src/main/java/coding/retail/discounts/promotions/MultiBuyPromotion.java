package coding.retail.discounts.promotions;

import coding.retail.discounts.Product;

import java.math.BigDecimal;
import java.util.List;

// 2. Concrete Strategy: 3 for £10 (Multi-buy)
public class MultiBuyPromotion implements PromotionStrategy {
    private final String productId;
    private final int quantity;
    private final BigDecimal targetPrice;

    public MultiBuyPromotion(String productId, int quantity, BigDecimal targetPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.targetPrice = targetPrice;
    }

    @Override
    public BigDecimal calculateDiscount(List<Product> items) {
        List<Product> targetItems = items.stream().filter(p -> p.id().equals(productId)).toList();
        if (targetItems.size() < quantity) return BigDecimal.ZERO;

        BigDecimal unitPrice = targetItems.get(0).price();
        int bundles = targetItems.size() / quantity;

        BigDecimal normalPriceForBundle = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal discountPerBundle = normalPriceForBundle.subtract(targetPrice);

        return discountPerBundle.multiply(BigDecimal.valueOf(bundles));
    }
}
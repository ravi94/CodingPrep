package coding.retail.discounts.promotions;

import coding.retail.discounts.Product;

import java.math.BigDecimal;
import java.util.List;

public interface PromotionStrategy {
    BigDecimal calculateDiscount(List<Product> items);
}

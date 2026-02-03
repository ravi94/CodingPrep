package coding.retail.discounts;

import java.math.BigDecimal;

public record Product(String id, String name, BigDecimal price, String category) {}

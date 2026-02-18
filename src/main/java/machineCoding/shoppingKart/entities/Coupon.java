package machineCoding.shoppingKart.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import machineCoding.shoppingKart.services.discounts.DiscountStrategy;

@Data
@AllArgsConstructor
public class Coupon {
    private String code;
    private DiscountStrategy discountStrategy;
    private boolean isActive;
}

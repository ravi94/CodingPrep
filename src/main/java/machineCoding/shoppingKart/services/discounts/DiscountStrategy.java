package machineCoding.shoppingKart.services.discounts;

import machineCoding.shoppingKart.entities.CartItem;

import java.util.List;

public interface DiscountStrategy {
    double calculateDiscount(double amount, List<CartItem> items);
    String getDescription();
}

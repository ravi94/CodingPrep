package machineCoding.shoppingKart.services.discounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import machineCoding.shoppingKart.entities.CartItem;

import java.util.List;

@AllArgsConstructor
@Data
public class FlatDiscount implements DiscountStrategy{
    private double discountAmount;
    private double minimumPurchase;



    @Override
    public double calculateDiscount(double amount, List<CartItem> items) {
        if (amount >= minimumPurchase) {
            return discountAmount;
        }
        return 0;
    }

    @Override
    public String getDescription() {
        return String.format("₹%.2f off on orders above ₹%.2f",
                discountAmount, minimumPurchase);
    }

}

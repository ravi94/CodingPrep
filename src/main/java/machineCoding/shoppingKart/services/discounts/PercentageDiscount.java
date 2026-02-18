package machineCoding.shoppingKart.services.discounts;

import machineCoding.shoppingKart.entities.CartItem;

import java.util.List;

public class PercentageDiscount implements DiscountStrategy{
    private double percentage;
    private double maxDiscount;

    public PercentageDiscount(double percentage, double maxDiscount) {
        this.percentage = percentage;
        this.maxDiscount = maxDiscount;
    }


    @Override
    public double calculateDiscount(double amount, List<CartItem> items) {
        double discount = amount * (percentage / 100.0);
        return Math.min(discount, maxDiscount);
    }

    @Override
    public String getDescription() {
        return String.format("%.0f%% off (max ₹%.2f)", percentage, maxDiscount);
    }
}

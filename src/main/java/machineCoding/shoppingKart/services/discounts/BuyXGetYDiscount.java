package machineCoding.shoppingKart.services.discounts;

import machineCoding.shoppingKart.entities.CartItem;

import java.util.List;

public class BuyXGetYDiscount implements DiscountStrategy {
    private int buyQuantity;
    private int getQuantity;
    private String productCategory;

    public BuyXGetYDiscount(int buyQuantity, int getQuantity, String productCategory) {
        this.buyQuantity = buyQuantity;
        this.getQuantity = getQuantity;
        this.productCategory = productCategory;
    }

    @Override
    public double calculateDiscount(double amount, List<CartItem> items) {
        double discount = 0;

        for (CartItem item : items) {
            if (productCategory == null ||
                    item.getProduct().getCategory().equals(productCategory)) {

                int quantity = item.getQuantity();
                int sets = quantity / (buyQuantity + getQuantity);
                int freeItems = sets * getQuantity;

                discount += freeItems * item.getProduct().getPrice();
            }
        }

        return discount;
    }

    @Override
    public String getDescription() {
        String category = productCategory != null ? " on " + productCategory : "";
        return String.format("Buy %d Get %d Free%s", buyQuantity, getQuantity, category);
    }

}

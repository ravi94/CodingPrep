package machineCoding.shoppingKart.entities;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Order {
    private String orderId;
    private User user;
    private List<CartItem> items;
    private double totalAmount;
    private double discountAmount;
    private double taxAmount;
    private double finalAmount;
    private LocalDateTime orderDate;
    private OrderStatus status;


    public Order(String orderId, User user, List<CartItem> items,
                 double totalAmount, double discountAmount,
                 double taxAmount, double finalAmount) {
        this.orderId = orderId;
        this.user = user;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.finalAmount = finalAmount;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PLACED;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== ORDER DETAILS ==========\n");
        sb.append(String.format("Order ID: %s\n", orderId));
        sb.append(String.format("Customer: %s\n", user.getName()));
        sb.append(String.format("Date: %s\n", orderDate));
        sb.append(String.format("Status: %s\n", status));
        sb.append("\nItems:\n");
        for (CartItem item : items) {
            sb.append("  " + item.toString() + "\n");
        }
        sb.append(String.format("\nSubtotal: ₹%.2f\n", totalAmount));
        sb.append(String.format("Discount: -₹%.2f\n", discountAmount));
        sb.append(String.format("Tax (18%%): ₹%.2f\n", taxAmount));
        sb.append(String.format("TOTAL: ₹%.2f\n", finalAmount));
        sb.append("===================================\n");
        return sb.toString();
    }
}

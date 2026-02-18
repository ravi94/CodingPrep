package machineCoding.shoppingKart.services;

import machineCoding.shoppingKart.entities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OrderService {
    private Map<String, Order> orders;
    private final AtomicInteger orderCounter; // thread safe counter
    private static final double TAX_RATE = 0.18; // 18% GST
    private final Lock orderCreationLock;

    public OrderService() {
        this.orders = new ConcurrentHashMap<>();
        this.orderCounter = new AtomicInteger(1000);
        this.orderCreationLock = new ReentrantLock();
    }

    public Order createOrder(User user, List<CartItem> cartItems , Coupon appliedCoupon,InventoryService inventoryService){
        orderCreationLock.lock();
        try {
            // Step 1: Validate and reserve stock atomically

            List<String> reservedProducts = new ArrayList<>();
            for (CartItem item : cartItems) {
                boolean reserved = inventoryService.reserveStock(
                        item.getProduct().getId(),
                        item.getQuantity()
                );


                if (!reserved) {
                    // Rollback: Release all previously reserved stock
                    System.out.println(String.format("[%s] Order failed: Insufficient stock for %s. Rolling back...",
                            Thread.currentThread().getName(),
                            item.getProduct().getName()));

                    for (String productId : reservedProducts) {
                        CartItem rollbackItem = cartItems.stream()
                                .filter(i -> i.getProduct().getId().equals(productId))
                                .findFirst()
                                .orElse(null);
                        if (rollbackItem != null) {
                            inventoryService.releaseStock(productId, rollbackItem.getQuantity());
                        }
                    }
                    return null;
                }
                reservedProducts.add(item.getProduct().getId());
            }

            // Step 2: Calculate totals
            double subtotal = cartItems.stream()
                    .mapToDouble(CartItem::getSubtotal)
                    .sum();

            double discount = 0;
            if (appliedCoupon != null && appliedCoupon.isActive()) {
                discount = appliedCoupon.getDiscountStrategy()
                        .calculateDiscount(subtotal, cartItems);
            }

            double amountAfterDiscount = subtotal - discount;
            double tax = amountAfterDiscount * TAX_RATE;
            double finalAmount = amountAfterDiscount + tax;


            // Step 3: Create order with atomic counter
            String orderId = "ORD" + orderCounter.getAndIncrement();;
            Order order = new Order(orderId, user, cartItems, subtotal,
                    discount, tax, finalAmount);

            orders.put(orderId, order);
            System.out.println("Order created successfully: " + orderId);

            return order;
        } finally {
            orderCreationLock.unlock();
        }
    }

    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }

    public List<Order> getUserOrders(String userId) {
        List<Order> userOrders = new ArrayList<>();
        orders.values().stream()
                .filter(order -> order.getUser().getId().equals(userId))
                .forEach(userOrders::add);

        return userOrders;
    }

    public void updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orders.get(orderId);
        if (order != null) {
            synchronized (order) {
                order.setStatus(status);
                System.out.println("Order " + orderId + " status updated to: " + status);
            }
        }else{
            System.out.println("Order not found with id "+orderId);
        }
    }

    /**
     * Cancel order and restore inventory
     */
    public boolean cancelOrder(String orderId, InventoryService inventoryService) {
        Order order = orders.get(orderId);
        if (order == null) return false;

        synchronized (order) {
            if (order.getStatus() == OrderStatus.DELIVERED ||
                    order.getStatus() == OrderStatus.CANCELLED) {
                System.out.println("Cannot cancel order in current status: " + order.getStatus());
                return false;
            }

            // Release stock back to inventory
            for (CartItem item : order.getItems()) {
                inventoryService.releaseStock(
                        item.getProduct().getId(),
                        item.getQuantity()
                );
            }

            order.setStatus(OrderStatus.CANCELLED);
            System.out.println(String.format("[%s] Order %s cancelled and stock restored",
                    Thread.currentThread().getName(), orderId));
            return true;
        }
    }

}

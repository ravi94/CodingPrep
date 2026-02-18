package machineCoding.shoppingKart;

import machineCoding.shoppingKart.entities.*;
import machineCoding.shoppingKart.services.CartService;
import machineCoding.shoppingKart.services.CouponService;
import machineCoding.shoppingKart.services.InventoryService;
import machineCoding.shoppingKart.services.OrderService;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ShoppingCartApplication {


    public static void main(String[] args) throws InterruptedException {
        // Initialize services
        InventoryService inventoryService = new InventoryService();
        CartService cartService = new CartService();
        CouponService couponService = new CouponService();
        OrderService orderService = new OrderService();

        // Setup limited inventory
        System.out.println("========== SETTING UP LIMITED INVENTORY ==========\n");
        Product iPhone = new Product("iPhone 15", 80000, "Electronics", 5); // Only 5 units
        Product airpods = new Product("AirPods Pro", 25000, "Electronics", 10);

        inventoryService.addProduct(iPhone);
        inventoryService.addProduct(airpods);

        // Create multiple users
        User user1 = new User("U001", "Ravi", "ravi@example.com");
        User user2 = new User("U002", "Amit", "amit@example.com");
        User user3 = new User("U003", "Priya", "priya@example.com");
        User user4 = new User("U004", "Sneha", "sneha@example.com");

        System.out.println("\n========== SCENARIO: RACE CONDITION TEST ==========");
        System.out.println("5 users trying to buy iPhone (only 5 in stock)\n");

        // Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);

        List<Future<Order>> futures = new ArrayList<>();

        // Simulate 5 concurrent users trying to buy iPhone
        User[] users = {user1, user2, user3, user4, new User("U005", "Raj", "raj@example.com")};

        for (int i = 0; i < 5; i++) {
            final User user = users[i];
            final int orderQty = (i < 2) ? 2 : 1; // First 2 users want 2 units, others want 1

            Future<Order> future = executor.submit(() -> {
                try {
                    // Simulate network delay
                    Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));

                    // Add to cart
                    cartService.addToCart(user.getId(), iPhone, orderQty, inventoryService);

                    // Small delay before checkout
                    Thread.sleep(100);

                    // Checkout
                    List<CartItem> cart = cartService.getCart(user.getId());
                    Order order = orderService.createOrder(user, cart, null, inventoryService);

                    if (order != null) {
                        cartService.clearCart(user.getId());
                    }

                    latch.countDown();
                    return order;

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    latch.countDown();
                    return null;
                }
            });

            futures.add(future);
        }

        // Wait for all threads to complete
        latch.await();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Check results
        System.out.println("\n========== RESULTS ==========\n");
        int successfulOrders = 0;
        int failedOrders = 0;

        for (int i = 0; i < futures.size(); i++) {
            try {
                Order order = futures.get(i).get();
                if (order != null) {
                    successfulOrders++;
                    System.out.println(String.format("✓ User %s: Order %s successful (₹%.2f)",
                            users[i].getName(), order.getOrderId(), order.getFinalAmount()));
                } else {
                    failedOrders++;
                    System.out.println(String.format("✗ User %s: Order failed (insufficient stock)",
                            users[i].getName()));
                }
            } catch (Exception e) {
                failedOrders++;
                System.out.println(String.format("✗ User %s: Order failed with exception",
                        users[i].getName()));
            }
        }

        System.out.println(String.format("\nSuccessful Orders: %d", successfulOrders));
        System.out.println(String.format("Failed Orders: %d", failedOrders));

        System.out.println("\n========== FINAL INVENTORY ==========\n");
        System.out.println(iPhone);
        System.out.println(airpods);

        // Test order cancellation
        System.out.println("\n========== TESTING ORDER CANCELLATION ==========\n");

        List<Order> allOrders = new ArrayList<>();
        for (Future<Order> future : futures) {
            try {
                Order order = future.get();
                if (order != null) {
                    allOrders.add(order);
                }
            } catch (Exception e) {
                // Skip
            }
        }

        if (!allOrders.isEmpty()) {
            Order firstOrder = allOrders.get(0);
            System.out.println("Attempting to cancel order: " + firstOrder.getOrderId());
            boolean cancelled = orderService.cancelOrder(firstOrder.getOrderId(), inventoryService);

            System.out.println("\n========== INVENTORY AFTER CANCELLATION ==========\n");
            System.out.println(iPhone);
        }
    }

}

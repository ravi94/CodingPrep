package machineCoding.shoppingKart.entities;

import lombok.Data;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Product {
    private String id;
    private String name;
    private double price;
    private String category;
    private AtomicInteger version = new AtomicInteger(0);
    private AtomicInteger availableQuantity; // Use AtomicInteger directly

    public Product(String name, double price, String category, int availableQuantity) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.price = price;
        this.category = category;
        this.availableQuantity = new AtomicInteger(availableQuantity);
    }


//    public boolean reduceStock(int quantity) {
//        if (availableQuantity >= quantity) {
//            availableQuantity -= quantity;
//            return true;
//        }
//        return false;
//    }

    //optimistic reduce
    public boolean reduceStock(int quantity) {
        while (true) {
            int currentStock = availableQuantity.get();

            // Check if enough stock available
            if (currentStock < quantity) {
                return false; // Not enough stock, exit
            }

            // Try to atomically update the stock
            int newStock = currentStock - quantity;
            if (availableQuantity.compareAndSet(currentStock, newStock)) {
                return true;
            }

            // CAS failed - another thread modified it. Retry in next iteration
            System.out.println(String.format("[%s] CAS failed for %s, retrying...",
                    Thread.currentThread().getName(), name));
        }
    }

    public void addStock(int quantity) {
        availableQuantity.addAndGet(quantity);  // we can directly use thread safe add and get because we dont need to check anyting before adding
    }

    @Override
    public String toString() {
        return String.format("%s (₹%.2f) - Stock: %d", name, price, availableQuantity);
    }

}

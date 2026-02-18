package machineCoding.shoppingKart.services;

import machineCoding.shoppingKart.entities.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InventoryService {
    private Map<String, Product> products;
    private final Map<String, ReadWriteLock> productLocks;

    public InventoryService() {
        this.products = new ConcurrentHashMap<>();
        this.productLocks = new ConcurrentHashMap<>();
    }

    public void addProduct(Product product) {
        products.put(product.getId(), product);
        productLocks.put(product.getId(), new ReentrantReadWriteLock());
        System.out.println("Added product: " + product);
    }

    public Product getProduct(String productId) {
        return products.get(productId);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public boolean checkAvailability(String productId, int quantity) {
        Product product = products.get(productId);
        if (product == null) return false;
        ReadWriteLock lock = productLocks.get(productId);
        lock.readLock().lock();
        try {
            return product.getAvailableQuantity().get() >= quantity;
        } finally {
            lock.readLock().unlock(); // always do unlock in finally. so lock is never left locked in case of errors
        }
    }

//    public void updateStock(String productId, int quantity) {
//        Product product = products.get(productId);
//        if (product != null) {
//            product.addStock(quantity);
//            System.out.println("Updated stock for " + product.getName() +
//                    ": " + product.getAvailableQuantity());
//        }
//    }

    /**
     * Thread-safe stock reservation with write lock
     * Returns true if reservation successful
     */
    public boolean reserveStock(String productId, int quantity) {
        Product product = products.get(productId);
        if (product == null) return false;

        ReadWriteLock lock = productLocks.get(productId);
        lock.writeLock().lock();
        try {
            if (product.getAvailableQuantity().get() >= quantity) {
                product.reduceStock(quantity);
                System.out.println(String.format("[%s] Reserved %d units of %s. Remaining: %d",
                        Thread.currentThread().getName(),
                        quantity,
                        product.getName(),
                        product.getAvailableQuantity()));
                return true;
            }
            System.out.println(String.format("[%s] Failed to reserve %d units of %s. Available: %d",
                    Thread.currentThread().getName(),
                    quantity,
                    product.getName(),
                    product.getAvailableQuantity()));
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Thread-safe stock release (for order cancellation)
     */
    public void releaseStock(String productId, int quantity) {
        Product product = products.get(productId);
        if (product == null) return;

        ReadWriteLock lock = productLocks.get(productId);
        lock.writeLock().lock();
        try {
            product.addStock(quantity);
            System.out.println(String.format("[%s] Released %d units of %s. Available: %d",
                    Thread.currentThread().getName(),
                    quantity,
                    product.getName(),
                    product.getAvailableQuantity()));
        } finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * Atomic compare-and-swap operation for stock update
     */
    public boolean updateStockIfAvailable(String productId, int requiredQuantity, int newQuantity) {
        Product product = products.get(productId);
        if (product == null) return false;

        ReadWriteLock lock = productLocks.get(productId);
        lock.writeLock().lock();
        try {
            if (product.getAvailableQuantity().get() >= requiredQuantity) {
                product.setAvailableQuantity(new AtomicInteger(newQuantity));
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

}

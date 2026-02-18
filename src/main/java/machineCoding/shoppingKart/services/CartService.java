package machineCoding.shoppingKart.services;

import machineCoding.shoppingKart.entities.CartItem;
import machineCoding.shoppingKart.entities.Product;

import java.net.Inet4Address;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CartService {
    private Map<String, List<CartItem>> userCarts; // userId -> cart items
    private final Map<String, Lock> cartLocks; // Per-user locks

    public CartService(){
        this.userCarts = new ConcurrentHashMap<>();
        this.cartLocks = new ConcurrentHashMap<>();
    }

    private Lock getCartLock(String userId) {
        return cartLocks.computeIfAbsent(userId, k -> new ReentrantLock());
    }

    public void addToCart(String userId , Product product , int qty , InventoryService inventoryService){
        Lock lock = getCartLock(userId);
        lock.lock();
        try{
            if(!inventoryService.checkAvailability(product.getId(),qty)){
                System.out.println(String.format("[%s] Insufficient stock for %s",
                        Thread.currentThread().getName(), product.getName()));
                return;
            }

            userCarts.putIfAbsent(userId,new ArrayList<>());

            List<CartItem> cartItems = userCarts.get(userId);

            Optional<CartItem> existingItem =  cartItems.stream()
                    .filter(c->  c.getProduct().getId().equals(product.getId()) )
                    .findFirst();

            if(existingItem.isPresent()){
                CartItem item = existingItem.get();
                int newQty = item.getQuantity()+ qty;

                if (inventoryService.checkAvailability(product.getId(), newQty)) {
                    item.setQuantity(newQty);
                    System.out.println("Updated cart: " + product.getName() +
                            " quantity = " + newQty);
                } else {
                    System.out.println("Cannot add more. Insufficient stock.");
                }
            }else{
                cartItems.add(new CartItem(product, qty));
                System.out.println("Added to cart: " + product.getName() + " x " + qty);
            }
        }finally {
            lock.unlock();
        }

    }

    public void removeFromCart(String userId , String productId){
        Lock lock = getCartLock(userId);
        lock.lock();
        try {
            List<CartItem> cartItems = userCarts.get(userId);
            if(cartItems != null){
                cartItems.removeIf(cartItem -> cartItem.getProduct().getId().equals(productId));
                System.out.println("Removed the product");
            }else{
                System.out.println("User's cart is empty");
            }
        }finally {
            lock.unlock();
        }
    }

    public List<CartItem> getCart(String userId) {
        Lock lock = getCartLock(userId);
        lock.lock();
        try {
//        return userCarts.getOrDefault(userId, new ArrayList<>()); // wrong way since new Arraylist returned will not be tied
        return userCarts.computeIfAbsent(userId, k->new ArrayList<>());
        }finally {
            lock.unlock();
        }
    }

    //put lock in all places like above
    public void clearCart(String userId) {
        userCarts.remove(userId);
    }

    public void viewCart(String userId) {
        List<CartItem> cart = getCart(userId);
        if (cart.isEmpty()) {
            System.out.println("Cart is empty");
            return;
        }

        System.out.println("\n========== SHOPPING CART ==========");
        double total = 0;
        for (CartItem item : cart) {
            System.out.println(item);
            total += item.getSubtotal();
        }
        System.out.println(String.format("Subtotal: ₹%.2f", total));
        System.out.println("===================================\n");
    }
}

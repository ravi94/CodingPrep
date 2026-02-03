package machineCoding.foodkart.entities;


import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurant {
    public String name;
    public Map<String, Double> menu; // Item Name -> Price
    public double rating;
    public AtomicInteger currentCapacity;
    public int maxCapacity;
    public final Lock lock = new ReentrantLock(); // To handle thread-safe capacity/menu updates

    // Getters, Setters, Constructor...
    public boolean canFulfill(Map<String, Integer> orderItems) {
        return orderItems.keySet().stream().allMatch(menu::containsKey) &&
                currentCapacity.get() >= 1; // Simplification: 1 order = 1 capacity
    }

    public double getCost(Map<String,Integer> orderItems){
        return orderItems.entrySet().stream()
                .mapToDouble( entry-> menu.get(entry.getKey()) * entry.getValue())
                .sum();
    }

}

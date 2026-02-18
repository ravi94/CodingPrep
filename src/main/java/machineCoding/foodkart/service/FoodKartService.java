package machineCoding.foodkart.service;

import machineCoding.foodkart.entities.Restaurant;
import machineCoding.foodkart.service.restaurantSelectionStrategy.RestaurantSelectionStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class FoodKartService {
    private final List<Restaurant> restaurants = new CopyOnWriteArrayList<>();

    public String placeOrder(String user , Map<String, Integer> orderItems , RestaurantSelectionStrategy strategy){
        Optional<Restaurant> selectedRestaurant = strategy.selectRestaurant(restaurants, orderItems);

        if(selectedRestaurant.isPresent()){
            Restaurant restaurant = selectedRestaurant.get();
            if(restaurant.currentCapacity.getAndDecrement()>0){
                return "Order assigned to "+ restaurant.name;
            }else{
                restaurant.currentCapacity.incrementAndGet();
                return "Capacity full at last minute ! sorry !";
            }
        }
        return "Order can't be filled as no restaurant available.";
    }

    public void completeOrder(String restaurantName) {
        restaurants.stream().filter(r -> r.name.equals(restaurantName))
                .findFirst().ifPresent(r -> r.currentCapacity.incrementAndGet());
    }


    public boolean tryBook(AtomicInteger capacity) {
        while (true) {
            int current = capacity.get();
            if (current <= 0) return false; // No capacity
            // Only update if the value is still what we just read
            if (capacity.compareAndSet(current, current - 1)) {
                return true; // Success! No rollback needed.
            }
            // If compareAndSet fails, another thread beat us to it.
            // The loop will run again and re-read the new capacity.
        }
    }


}

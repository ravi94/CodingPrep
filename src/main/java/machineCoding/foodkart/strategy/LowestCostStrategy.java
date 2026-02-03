package machineCoding.foodkart.strategy;

import machineCoding.foodkart.entities.Restaurant;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LowestCostStrategy implements RestaurantSelectionStrategy {
    @Override
    public Optional<Restaurant> selectRestaurant(List<Restaurant> restaurants, Map<String, Integer> orderItems) {
        return  restaurants.stream()
                .filter(r -> r.canFulfill(orderItems))
                .min(Comparator.comparingDouble( r -> r.getCost(orderItems)));
    }
}

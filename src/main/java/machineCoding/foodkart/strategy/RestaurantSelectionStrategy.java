package machineCoding.foodkart.strategy;

import machineCoding.foodkart.entities.Restaurant;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RestaurantSelectionStrategy {
    Optional<Restaurant> selectRestaurant(List<Restaurant> restaurants, Map<String, Integer> orderItems);

}


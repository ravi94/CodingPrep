package lld.elevator.strategy;

import lld.elevator.Elevator;
import lld.elevator.model.ElevatorDirection;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Picks the cheapest elevator for a hall call, where cost is distance penalised
 * by how badly the elevator has to break its current run (see Elevator#costForFloorRequest).
 * Ties go to the elevator with the smaller queue.
 */
public class NearestElevatorSchedulingStrategy implements SchedulingStrategy{

    @Override
    public Optional<Elevator> selectElevator(List<Elevator> elevatorList, int floor, ElevatorDirection direction) {
        return elevatorList.stream()
                .filter(Elevator::isAvailable)
                .filter(elevator -> floor >= elevator.getMinFloor() && floor <= elevator.getMaxFloor())
                .min(Comparator.comparingInt((Elevator e) -> e.costForFloorRequest(floor, direction))
                        .thenComparingInt(Elevator::pendingStops));
    }
}

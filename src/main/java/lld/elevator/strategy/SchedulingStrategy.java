package lld.elevator.strategy;

import lld.elevator.Elevator;
import lld.elevator.model.ElevatorDirection;

import java.util.List;
import java.util.Optional;

public interface SchedulingStrategy {
    Optional<Elevator> selectElevator(List<Elevator> elevatorList, int floor , ElevatorDirection direction);
}

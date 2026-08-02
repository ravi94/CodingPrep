package lld.elevator;

import lld.elevator.model.ElevatorDirection;
import lld.elevator.model.ElevatorState;
import lombok.Data;

import java.util.NavigableSet;
import java.util.TreeSet;

@Data
public class Elevator {
    int id;
    int currentFloor;
    int maxFloor;
    int minFloor;
    ElevatorState state;
    ElevatorDirection direction;
    NavigableSet<Integer> upStops ;
    NavigableSet<Integer> downStops ;


    public Elevator(int id, int minFloor, int maxFloor){
        this.id = id;
        this.currentFloor = minFloor;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.state = ElevatorState.IDLE;
        this.direction = ElevatorDirection.IDLE;
        this.upStops = new TreeSet<>();
        this.downStops = new TreeSet<>();
    }

    public void addStop(int floor){
        if(floor < minFloor || floor > maxFloor)
            throw new RuntimeException("Wrong floor provided for adding a stop");

        // already standing there - just open the doors, nothing to schedule
        if(floor == currentFloor){
            openDoor();
            return;
        }

        if(floor > currentFloor)
            upStops.add(floor);
        else
            downStops.add(floor);

        if (direction == ElevatorDirection.IDLE)
            direction = floor>currentFloor ? ElevatorDirection.UP : ElevatorDirection.DOWN;

        if(state == ElevatorState.IDLE)
            state = ElevatorState.MOVING;
    }

    public void openDoor(){
        this.state = ElevatorState.DOOR_OPEN;
    }

    public void setMaintenance(){
        this.state = ElevatorState.MAINTENANCE;
    }

    public void endMaintenance(){
        if(state != ElevatorState.MAINTENANCE) return;
        this.state = pendingStops() == 0 ? ElevatorState.IDLE : ElevatorState.MOVING;
    }

    public boolean isAvailable(){
        return state != ElevatorState.MAINTENANCE;
    }

    //simulation of one tick
    public void tick(){
        if(state == ElevatorState.MAINTENANCE) return;

        // doors opened on the previous tick, close them before moving again
        if(state == ElevatorState.DOOR_OPEN)
            state = ElevatorState.DOOR_CLOSE;

        if(direction == ElevatorDirection.UP){
            if(upStops.remove(currentFloor)){   // serving a stop costs the whole tick
                openDoor();
                return;
            }
            if(!upStops.isEmpty()){
                currentFloor++;
                state = ElevatorState.MOVING;
                return;
            }
            direction = downStops.isEmpty() ? ElevatorDirection.IDLE : ElevatorDirection.DOWN;

        }else if(direction == ElevatorDirection.DOWN){
            if(downStops.remove(currentFloor)){
                openDoor();
                return;
            }
            if(!downStops.isEmpty()){
                currentFloor--;
                state = ElevatorState.MOVING;
                return;
            }
            direction = upStops.isEmpty() ? ElevatorDirection.IDLE : ElevatorDirection.UP;
        }

        if(upStops.isEmpty() && downStops.isEmpty()){
            direction = ElevatorDirection.IDLE;
            state = ElevatorState.IDLE;
        }
    }

    // direction is to prirotise elevstor in same direction
    public int costForFloorRequest(int floorRequested, ElevatorDirection directionRequested){
        int distance = Math.abs(floorRequested-currentFloor);

        if(direction == ElevatorDirection.IDLE) return distance;

        boolean movingToward = (direction == ElevatorDirection.UP && floorRequested>=  currentFloor) ||
                (direction == ElevatorDirection.DOWN && floorRequested <=  currentFloor);

        if(movingToward && direction.equals(directionRequested))  // same direction and same direction
            return distance;
        else if(movingToward) // same way , wrong direction req
            return distance + 2 * pendingStops();

        return  distance+ 4*pendingStops();
    }

    public int pendingStops(){
        return upStops.size()+downStops.size();
    }

}

package lld.elevator;

import lld.elevator.model.ElevatorDirection;
import lld.elevator.strategy.SchedulingStrategy;

import java.util.*;

public class ElevatorManagementSystem {
    List<Elevator> elevatorList;
    SchedulingStrategy schedulingStrategy;
    Deque<Map.Entry<Integer, ElevatorDirection>> unservedReq ;

    public ElevatorManagementSystem(List<Elevator> elevatorList, SchedulingStrategy schedulingStrategy){
        this.elevatorList = new ArrayList<>(elevatorList);
        this.schedulingStrategy = schedulingStrategy;
        this.unservedReq = new ArrayDeque<>();
    }

    public void addElevator(Elevator elevator){
        this.elevatorList.add(elevator);
    }

    public Optional<Elevator> requestElevator(int floor, ElevatorDirection direction){
        Optional<Elevator> elevator = this.schedulingStrategy.selectElevator(elevatorList,floor,direction);
        if(elevator.isPresent())
            elevator.get().addStop(floor);   // a picked elevator has to actually be sent there
        else
            unservedReq.addLast(Map.entry(floor,direction));

        return elevator;
    }

    public void requestFloorFromInside(Elevator elevator,int floor){
       elevator.addStop(floor);
    }

    public void tick(){
        elevatorList.forEach(Elevator::tick);
        retryUnserved();
    }

    // drain what is queued right now - anything still unservable is re-queued by requestElevator
    private void retryUnserved(){
        int queued = unservedReq.size();
        for(int i = 0; i < queued; i++){
            Map.Entry<Integer, ElevatorDirection> unserved = unservedReq.pollFirst();
            requestElevator(unserved.getKey(), unserved.getValue());
        }
    }

    public List<Elevator> getElevators(){
        return Collections.unmodifiableList(elevatorList);
    }

    public int unservedRequestCount(){
        return unservedReq.size();
    }
}

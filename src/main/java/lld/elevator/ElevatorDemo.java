package lld.elevator;

import lld.elevator.model.ElevatorDirection;
import lld.elevator.model.ElevatorState;
import lld.elevator.strategy.NearestElevatorSchedulingStrategy;
import lld.elevator.strategy.SchedulingStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Drives the elevator system tick by tick: a single car serving a hall call plus an
 * in-cabin request, scheduler cost comparison across a bank of cars, direction-aware
 * pickup, maintenance with request replay and rejection of out-of-range floors.
 */
public class ElevatorDemo {

    private static final int MAX_TICKS = 40;

    public static void main(String[] args) {
        singleElevatorRide();
        schedulerPicksCheapest();
        directionAwarePickup();
        maintenanceAndUnservedQueue();
        invalidFloors();
    }

    private static void singleElevatorRide() {
        header("1. One elevator: hall call at 5 (UP), then rider presses 9");

        Elevator elevator = new Elevator(1, 0, 10);
        ElevatorManagementSystem ems = system(elevator);

        System.out.println("Start -> " + describe(elevator));
        ems.requestElevator(5, ElevatorDirection.UP);
        System.out.println("Hall call from 5 accepted by elevator " + elevator.getId());

        runUntil(ems, "doors open at 5", e -> e.getCurrentFloor() == 5 && e.getState() == ElevatorState.DOOR_OPEN);

        ems.requestFloorFromInside(elevator, 9);
        System.out.println("Rider presses 9");
        runUntilIdle(ems);
    }

    private static void schedulerPicksCheapest() {
        header("2. Three elevators, one hall call at floor 6 (UP)");

        Elevator idleAtGround = new Elevator(1, 0, 10);   // idle, far away
        Elevator busyBelow = new Elevator(2, 0, 10);      // climbing towards the caller
        Elevator idleHigh = new Elevator(3, 0, 10);       // idle, parked close

        busyBelow.addStop(8);                             // already heading up past floor 6
        busyBelow.tick();
        idleHigh.addStop(7);                              // park it near the top
        for (int i = 0; i < 9; i++) idleHigh.tick();

        List<Elevator> bank = List.of(idleAtGround, busyBelow, idleHigh);
        bank.forEach(e -> System.out.println("  " + describe(e)
                + "  cost(6,UP) = " + e.costForFloorRequest(6, ElevatorDirection.UP)));

        ElevatorManagementSystem ems = system(idleAtGround, busyBelow, idleHigh);
        Optional<Elevator> picked = ems.requestElevator(6, ElevatorDirection.UP);
        System.out.println("Scheduler picked elevator " + picked.map(Elevator::getId).orElse(-1)
                + " (cheapest cost, ties broken by queue length)");

        runUntil(ems, "picked car opens at 6",
                e -> e.getId() == picked.get().getId()
                        && e.getCurrentFloor() == 6 && e.getState() == ElevatorState.DOOR_OPEN);
    }

    private static void directionAwarePickup() {
        header("3. Same distance, different direction of travel");

        Elevator goingUp = new Elevator(1, 0, 10);
        Elevator goingDown = new Elevator(2, 0, 10);

        // Both end up at floor 4, one travelling UP, the other travelling DOWN.
        goingUp.addStop(9);
        for (int i = 0; i < 4; i++) goingUp.tick();
        goingDown.addStop(8);
        for (int i = 0; i < 9; i++) goingDown.tick();
        goingDown.addStop(0);
        for (int i = 0; i < 5; i++) goingDown.tick();

        System.out.println("  " + describe(goingUp));
        System.out.println("  " + describe(goingDown));

        for (ElevatorDirection wanted : List.of(ElevatorDirection.UP, ElevatorDirection.DOWN)) {
            System.out.printf("  call at 7 wanting %-4s -> cost(up-car) = %d, cost(down-car) = %d%n",
                    wanted,
                    goingUp.costForFloorRequest(7, wanted),
                    goingDown.costForFloorRequest(7, wanted));
        }

        ElevatorManagementSystem ems = system(goingUp, goingDown);
        System.out.println("Scheduler picked elevator "
                + ems.requestElevator(7, ElevatorDirection.UP).map(Elevator::getId).orElse(-1)
                + " for the UP call - it is already heading that way");
    }

    private static void maintenanceAndUnservedQueue() {
        header("4. Whole bank in maintenance, request is replayed");

        Elevator first = new Elevator(1, 0, 10);
        Elevator second = new Elevator(2, 0, 10);
        ElevatorManagementSystem ems = system(first, second);

        first.setMaintenance();
        second.setMaintenance();

        Optional<Elevator> picked = ems.requestElevator(3, ElevatorDirection.DOWN);
        System.out.println("Request at 3 served? " + picked.isPresent()
                + ", queued requests = " + ems.unservedRequestCount());

        ems.tick();
        ems.tick();
        System.out.println("After 2 ticks with nothing available, queued requests = "
                + ems.unservedRequestCount() + " (replayed, not duplicated)");

        second.endMaintenance();
        System.out.println("Elevator 2 back in service");
        ems.tick();
        System.out.println("Queued requests = " + ems.unservedRequestCount()
                + ", elevator 2 -> " + describe(second));

        runUntil(ems, "elevator 2 opens at 3",
                e -> e.getId() == 2 && e.getCurrentFloor() == 3 && e.getState() == ElevatorState.DOOR_OPEN);
    }

    private static void invalidFloors() {
        header("5. Out of range floors");

        Elevator elevator = new Elevator(1, 0, 10);
        ElevatorManagementSystem ems = system(elevator);

        for (int floor : new int[]{11, -1}) {
            try {
                ems.requestFloorFromInside(elevator, floor);
                System.out.println("BUG: floor " + floor + " was accepted");
            } catch (RuntimeException e) {
                System.out.println("Floor " + floor + " rejected as expected: " + e.getMessage());
            }
        }

        elevator.addStop(elevator.getCurrentFloor());
        System.out.println("Pressing the floor it already sits on just opens the doors -> " + describe(elevator));
    }

    private static ElevatorManagementSystem system(Elevator... elevators) {
        SchedulingStrategy strategy = new NearestElevatorSchedulingStrategy();
        return new ElevatorManagementSystem(List.of(elevators), strategy);
    }

    /** Ticks the whole system until the predicate holds for some elevator, printing every tick. */
    private static void runUntil(ElevatorManagementSystem ems, String goal,
                                 java.util.function.Predicate<Elevator> reached) {
        for (int tick = 1; tick <= MAX_TICKS; tick++) {
            ems.tick();
            printTick(ems, tick);
            if (ems.getElevators().stream().anyMatch(reached)) {
                System.out.println("  reached: " + goal + " in " + tick + " ticks");
                return;
            }
        }
        System.out.println("  BUG: never reached '" + goal + "' within " + MAX_TICKS + " ticks");
    }

    private static void runUntilIdle(ElevatorManagementSystem ems) {
        for (int tick = 1; tick <= MAX_TICKS; tick++) {
            ems.tick();
            printTick(ems, tick);
            if (ems.getElevators().stream().allMatch(e -> e.getState() == ElevatorState.IDLE)) {
                System.out.println("  all elevators idle after " + tick + " ticks");
                return;
            }
        }
        System.out.println("  BUG: elevators never settled within " + MAX_TICKS + " ticks");
    }

    private static void printTick(ElevatorManagementSystem ems, int tick) {
        StringBuilder line = new StringBuilder(String.format("  t%-3d", tick));
        ems.getElevators().forEach(e -> line.append(describe(e)).append("   "));
        System.out.println(line.toString().stripTrailing());
    }

    private static String describe(Elevator e) {
        return String.format("E%d@%-2d %-9s %-4s up%s down%s",
                e.getId(), e.getCurrentFloor(), e.getState(), e.getDirection(),
                e.getUpStops(), e.getDownStops());
    }

    private static void header(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}

package machineCoding.rideShare.service;

import machineCoding.rideShare.entities.Ride;
import machineCoding.rideShare.selectionStrategy.RideSelectionStrategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RiderService {
    private List<Ride> allRides = new CopyOnWriteArrayList<>();

    private Set<String> activeDrivers = ConcurrentHashMap.newKeySet();

    // onbaord Ride
    public void offerRide(String driverName, String vehicleModel , int seats , String origin , String destination){
        //check if driver is busy or not
        if(activeDrivers.contains(driverName)) {
            System.out.println("Driver " + driverName + " already has an active Ride ! Please Wait !");
            return;
        }
        Ride ride = new Ride(UUID.randomUUID().toString(),driverName,vehicleModel,origin,destination,new AtomicInteger(seats),true);
        allRides.add(ride);
        activeDrivers.add(driverName);
    }

    //Driver Search with Strategy
    public Optional<Ride> selectRide(String origin , String destination, int seats, RideSelectionStrategy rideSelectionStrategy){
        return rideSelectionStrategy.selectRide(allRides,origin,destination,seats);
    }

    public boolean bookRide(Ride ride, int seatsNeeded) {
        while (true) {
            int currentSeats = ride.getAvailableSeat().get();
            if (currentSeats < seatsNeeded) {
                return false; // Not enough seats
            }

            // compareAndSet ensures the seats are ONLY deducted if
            // the value hasn't changed since we last checked it.
            if (ride.getAvailableSeat().compareAndSet(currentSeats, currentSeats - seatsNeeded)) {
                return true; // Booking confirmed atomically
            }
            // If it returns false, another thread updated the seats first.
            // The loop runs again to check the new 'truth'.
        }
    }

    public List<Ride> selectRideMulitHop(String origin , String destination, int seatsNeeded){
        Deque<LinkedList<Ride>> queue = new ArrayDeque<>();

        // Initialize queue with all rides starting from 'origin'
        for(Ride r : allRides){
            if(r.getOrigin().equalsIgnoreCase(origin) && r.getAvailableSeat().get()>= seatsNeeded){
                LinkedList<Ride> path = new LinkedList<>();
                path.add(r);
                queue.add(path);
            }
        }

        while(!queue.isEmpty()){
            LinkedList<Ride> currentPath = queue.poll();
            Ride lastRide = currentPath.getLast();

            if(lastRide.getDestination().equalsIgnoreCase(destination)) {
                return currentPath;
            }

            //find Next Hop for journey
            for(Ride nextRide : allRides){
                if(nextRide.getOrigin().equalsIgnoreCase(lastRide.getDestination()) &&
                        nextRide.getAvailableSeat().get() >= seatsNeeded
                ){
                    //cycle detection
                    boolean alreadyInPath = currentPath.stream()
                            .anyMatch(r-> r.getDriverName().equals(nextRide.getDriverName()));

                    if(!alreadyInPath){
                        //Note Cloning is needed due to one path branching out to many and if not clone we modify same one again.
                        LinkedList<Ride> newPath = new LinkedList<>(currentPath);
                        newPath.add(nextRide);
                        queue.add(newPath);
                    }
                }
            }
        }
        return Collections.emptyList();
    }
}

package lld.parkingLot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Exercises the parking lot end to end: happy path, capacity exhaustion,
 * fee calculation, double exit and a concurrent rush on the last few spots.
 */
public class ParkingLotDemo {

    private static final long ONE_HOUR = 60L * 60 * 1000;

    public static void main(String[] args) throws Exception {
        happyPath();
        capacityExhaustion();
        feeCalculation();
        invalidExits();
        concurrentRush();
    }

    private static void happyPath() throws Exception {
        header("1. Park and unpark a few vehicles");

        //ParkingLot lot = new ParkingLot(new HourlyFeeStrategy(), 2, 2);
        ParkingLot lot = ParkingLot.builder()
                .feeStrategy(new HourlyFeeStrategy())
                        .parkingFloor(ParkingFloor.builder()
                                .floorNum(1)
                                .spotCount(VehicleType.MOTORCYLE,10)
                                .spotCount(VehicleType.CAR,10)
                                .spotCount(VehicleType.TRUCK,10)
                                .build())
                        .parkingFloor(ParkingFloor.builder()
                                .floorNum(2)
                                .spotCount(VehicleType.MOTORCYLE,10)
                                .spotCount(VehicleType.CAR,10)
                                .spotCount(VehicleType.TRUCK,10)
                                .build())
                        .build();

        printAvailability(lot);

        Ticket carTicket = lot.park(new Car("KA-01-HH-1234"));
        Ticket bikeTicket = lot.park(new Bike("KA-05-JJ-4321"));
        Ticket truckTicket = lot.park(new Truck("KA-09-TT-9999"));

        System.out.println("Car   -> " + describe(carTicket));
        System.out.println("Bike  -> " + describe(bikeTicket));
        System.out.println("Truck -> " + describe(truckTicket));
        printAvailability(lot);

        long outTime = carTicket.getInTime() + (3 * ONE_HOUR);
        System.out.printf("Car exits after 3h, fee = %.2f%n", lot.unpark(carTicket.getId(), outTime));
        printAvailability(lot);
    }

    private static void capacityExhaustion() throws Exception {
        header("2. Fill every car spot, then one more");

        // 2 floors x 1 car spot per floor = 2 car spots in total.
        ParkingLot lot = new ParkingLot(new HourlyFeeStrategy(), 2, 1);

        Ticket first = lot.park(new Car("CAR-1"));
        Ticket second = lot.park(new Car("CAR-2"));
        System.out.println("CAR-1 -> " + describe(first) + "   (should be floor 1)");
        System.out.println("CAR-2 -> " + describe(second) + "   (should overflow to floor 2)");
        System.out.println("Free car spots now: " + lot.countFree(VehicleType.CAR));

        try {
            lot.park(new Car("CAR-3"));
            System.out.println("BUG: CAR-3 parked in a full lot");
        } catch (Exception e) {
            System.out.println("CAR-3 rejected as expected: " + e.getMessage());
        }

        // A bike still fits - car capacity is exhausted, bike capacity is not.
        System.out.println("Bike still parks fine -> " + describe(lot.park(new Bike("BIKE-1"))));

        // Free one car spot and confirm the next car gets it.
        lot.unpark(first.getId(), first.getInTime() + ONE_HOUR);
        System.out.println("CAR-3 retried after CAR-1 left -> " + describe(lot.park(new Car("CAR-3"))));
    }

    private static void feeCalculation() throws Exception {
        header("3. Fee calculation per vehicle type");

        ParkingLot lot = new ParkingLot(new HourlyFeeStrategy(), 1, 5);

        // Bike 1/hr, Car 2/hr, Truck 2/hr; any started hour is billed in full.
        chargeFor(lot, new Bike("BIKE-A"), 30 * 60 * 1000L, "30 min  (billed 1h)");
        chargeFor(lot, new Car("CAR-A"), 2 * ONE_HOUR, "2h exact");
        chargeFor(lot, new Car("CAR-B"), 2 * ONE_HOUR + 1, "2h + 1ms (billed 3h)");
        chargeFor(lot, new Truck("TRUCK-A"), 5 * ONE_HOUR, "5h exact");
    }

    private static void invalidExits() throws Exception {
        header("4. Invalid exits");

        ParkingLot lot = new ParkingLot(new HourlyFeeStrategy(), 1, 1);
        Ticket ticket = lot.park(new Car("CAR-X"));
        System.out.printf("First exit fee = %.2f%n", lot.unpark(ticket.getId(), ticket.getInTime() + ONE_HOUR));

        try {
            lot.unpark(ticket.getId(), ticket.getInTime() + (2 * ONE_HOUR));
            System.out.println("BUG: the same ticket was accepted twice");
        } catch (Exception e) {
            System.out.println("Reused ticket rejected as expected: " + e.getMessage());
        }

        try {
            lot.unpark(UUID.randomUUID());
            System.out.println("BUG: an unknown ticket was accepted");
        } catch (Exception e) {
            System.out.println("Unknown ticket rejected as expected: " + e.getMessage());
        }
    }

    private static void concurrentRush() throws Exception {
        header("5. 50 cars racing for 10 spots");

        int spots = 10;
        int drivers = 50;
        ParkingLot lot = new ParkingLot(new HourlyFeeStrategy(), 1, spots);

        ExecutorService pool = Executors.newFixedThreadPool(16);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger parked = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();
        List<ParkingSpot> assignedSpots = java.util.Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < drivers; i++) {
            String regNum = "RUSH-" + i;
            pool.submit(() -> {
                try {
                    start.await();
                    Ticket ticket = lot.park(new Car(regNum));
                    assignedSpots.add(ticket.getSpot());
                    parked.incrementAndGet();
                } catch (Exception e) {
                    rejected.incrementAndGet();
                }
            });
        }

        start.countDown();
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        long distinctSpots = assignedSpots.stream().distinct().count();
        System.out.println("Parked:   " + parked.get() + " (expected " + spots + ")");
        System.out.println("Rejected: " + rejected.get() + " (expected " + (drivers - spots) + ")");
        System.out.println("Distinct spots handed out: " + distinctSpots
                + (distinctSpots == parked.get() ? " - no double allocation" : " - BUG: a spot was shared"));
        System.out.println("Free car spots left: " + lot.countFree(VehicleType.CAR));
    }

    private static void chargeFor(ParkingLot lot, Vehicle vehicle, long stayMillis, String label) throws Exception {
        Ticket ticket = lot.park(vehicle);
        double fee = lot.unpark(ticket.getId(), ticket.getInTime() + stayMillis);
        System.out.printf("%-8s %-22s fee = %.2f%n", vehicle.type, label, fee);
    }

    private static void printAvailability(ParkingLot lot) {
        System.out.printf("Available -> bikes: %d, cars: %d, trucks: %d%n",
                lot.countFree(VehicleType.MOTORCYLE),
                lot.countFree(VehicleType.CAR),
                lot.countFree(VehicleType.TRUCK));
    }

    private static String describe(Ticket ticket) {
        return "ticket " + ticket.getId().toString().substring(0, 8)
                + " for " + ticket.getVehicle().regNum
                + " at spot " + ticket.getSpot().getId();
    }

    private static void header(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}

package machineCoding.hotelRoomBooking;

import lombok.SneakyThrows;
import machineCoding.hotelRoomBooking.entities.Booking;
import machineCoding.hotelRoomBooking.entities.Room;
import machineCoding.hotelRoomBooking.entities.enums.RoomType;
import machineCoding.hotelRoomBooking.services.BookingService;
import machineCoding.hotelRoomBooking.services.PricingService;
import machineCoding.hotelRoomBooking.services.RoomInventoryService;
import machineCoding.hotelRoomBooking.services.pricingStartegies.WeekdayWeekendPricing;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class HotelBookingApplication {
    public static void main(String[] args) throws InterruptedException  {
        // Initialize services
        RoomInventoryService inventoryService = new RoomInventoryService();
        PricingService pricingService = new PricingService(
                new WeekdayWeekendPricing(1.0, 1.5)
        );

        BookingService bookingService = new BookingService(inventoryService, pricingService);

        // Setup rooms
        System.out.println("========== SETTING UP HOTEL INVENTORY ==========\n");
        for (int i = 1; i <= 5; i++) {
            inventoryService.addRoom(new Room("101" + i, RoomType.SINGLE));
        }
        for (int i = 1; i <= 3; i++) {
            inventoryService.addRoom(new Room("201" + i, RoomType.DOUBLE));
        }
        for (int i = 1; i <= 2; i++) {
            inventoryService.addRoom(new Room("301" + i, RoomType.SUITE));
        }



        System.out.println("\n========== SCENARIO 1: Normal Booking Flow ==========\n");

        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = LocalDate.now().plusDays(13);

        // Search for rooms
        List<Room> availableRooms = bookingService.searchRooms(
                RoomType.DOUBLE, checkIn, checkOut
        );


        System.out.println("Available DOUBLE rooms from " + checkIn + " to " + checkOut + ":");
        for (Room room : availableRooms) {
            System.out.println("  " + room);
        }

        // Book a room
        if (!availableRooms.isEmpty()) {
            Room selectedRoom = availableRooms.get(0);
            Booking booking = bookingService.createBooking(
                    "ravi",
                    selectedRoom.getRoomId(),
                    checkIn,
                    checkOut
            );

            if (booking != null) {
                System.out.println("\n" + booking);

                // Show price breakdown
                Map<LocalDate, Double> breakdown = pricingService.getPriceBreakdown(
                        selectedRoom.getRoomType(), checkIn, checkOut
                );
                System.out.println("\nPrice Breakdown:");
                breakdown.forEach((date, price) ->
                        System.out.println("  " + date + ": ₹" + price)
                );
            }
        }



        System.out.println("\n========== SCENARIO 2: Concurrent Booking (Race Condition Test) ==========\n");
        Room popularRoom = availableRooms.get(1);
        LocalDate raceCheckIn = LocalDate.now().plusDays(20);
        LocalDate raceCheckOut = LocalDate.now().plusDays(22);

        System.out.println("3 users trying to book the same room for same dates...\n");

        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(3);

        String[] userIds = {"ravi", "amit", "priya"};
        List<Future<Booking>> futures = new ArrayList<>();

        for (String userId : userIds) {
            Future<Booking> future = executor.submit(() -> {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(50, 150));

                    Booking booking = bookingService.createBooking(
                            userId,
                            popularRoom.getRoomId(),
                            raceCheckIn,
                            raceCheckOut
                    );

                    latch.countDown();
                    return booking;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    latch.countDown();
                    return null;
                }
            });
            futures.add(future);
        }
        latch.await();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);


        // Check results
        int successCount = 0;
        for (int i = 0; i < futures.size(); i++) {
            try {
                Booking booking = futures.get(i).get();
                if (booking != null) {
                    successCount++;
                    System.out.println(String.format("✓ %s: Booking successful (%s)",
                            userIds[i], booking.getBookingId()));
                } else {
                    System.out.println(String.format("✗ %s: Booking failed",
                            userIds[i]));
                }
            } catch (Exception e) {
                System.out.println(String.format("✗ %s: Booking error",
                        userIds[i]));
            }
        }

        System.out.println(String.format("\nResult: %d successful, %d failed",
                successCount, 3 - successCount));

        if (successCount == 1) {
            System.out.println("✓ No double-booking!");
        } else {
            System.out.println("✗ Double-booking occurred!");
        }


        System.out.println("\n========== SCENARIO 3: Cancellation & Refund ==========\n");

        List<Booking> raviBookings = bookingService.getUserBookings("ravi");
        if (!raviBookings.isEmpty()) {
            Booking bookingToCancel = raviBookings.get(0);
            System.out.println("Original booking: " + bookingToCancel);
            System.out.println("\nCancelling booking...");
            bookingService.cancelBooking(bookingToCancel.getBookingId());
            System.out.println("Status after cancellation: " + bookingToCancel.getStatus());
        }

    }
}

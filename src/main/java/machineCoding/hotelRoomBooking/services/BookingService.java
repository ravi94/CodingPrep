package machineCoding.hotelRoomBooking.services;

import machineCoding.hotelRoomBooking.entities.Booking;
import machineCoding.hotelRoomBooking.entities.DateRange;
import machineCoding.hotelRoomBooking.entities.Room;
import machineCoding.hotelRoomBooking.entities.enums.BookingStatus;
import machineCoding.hotelRoomBooking.entities.enums.RoomType;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BookingService {
    private Map<String, Booking> bookings; // bookingId -> Booking
    private Map<String, List<Booking>> userBookings; // userId -> List<Booking>
    private Lock bookingLock;

    private RoomInventoryService inventoryService;
    private PricingService pricingService;

    public BookingService(RoomInventoryService inventoryService,
                          PricingService pricingService) {
        this.bookings = new ConcurrentHashMap<>();
        this.userBookings = new ConcurrentHashMap<>();
        this.bookingLock = new ReentrantLock();
        this.inventoryService = inventoryService;
        this.pricingService = pricingService;
    }

    /**
     * Search for available rooms
     */
    public List<Room> searchRooms(RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        DateRange dateRange = new DateRange(checkIn, checkOut);
        return inventoryService.findAvailableRooms(roomType, dateRange);
    }

    /**
     * Thread-safe booking creation
     */
    public Booking createBooking(String userId, String roomId,
                                 LocalDate checkIn, LocalDate checkOut) {
        bookingLock.lock();
        try{
            DateRange dateRange = new DateRange(checkIn, checkOut);

            // Check availability
            if (!inventoryService.isRoomAvailable(roomId, dateRange)) {
                System.out.println(String.format("[%s] Room not available for requested dates",
                        Thread.currentThread().getName()));
                return null;
            }

            Room room = inventoryService.getRoom(roomId);
            if (room == null) {
                System.out.println("Room not found");
                return null;
            }

            // Calculate price
            double totalAmount = pricingService.calculateTotalPrice(
                    room.getRoomType(), checkIn, checkOut
            );

            // Create booking
            Booking booking = new Booking(userId, room, checkIn, checkOut, totalAmount);

            // Reserve room
            if (!inventoryService.reserveRoom(roomId, booking)) {
                System.out.println("Failed to reserve room");
                return null;
            }

            // Store booking
            bookings.put(booking.getBookingId(), booking);
            userBookings.computeIfAbsent(userId, k -> new ArrayList<>()).add(booking);

            System.out.println(String.format("[%s] Booking created: %s",
                    Thread.currentThread().getName(), booking.getBookingId()));

            return booking;
        }finally {
            bookingLock.unlock();
        }
    }

    /**
     * Cancel booking with refund calculation
     */
    public boolean cancelBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);

        if (booking == null) {
            System.out.println("Booking not found");
            return false;
        }

        synchronized (booking) {
            if (booking.getStatus() == BookingStatus.CANCELLED) {
                System.out.println("Booking already cancelled");
                return false;
            }

            if (booking.getStatus() == BookingStatus.CHECKED_OUT) {
                System.out.println("Cannot cancel completed booking");
                return false;
            }

            // Calculate refund based on cancellation policy
            double refund = calculateRefund(booking);

            booking.setStatus(BookingStatus.CANCELLED);

            System.out.println(String.format("Booking %s cancelled. Refund: ₹%.2f",
                    bookingId, refund));

            return true;

        }
    }

    /**
     * Cancellation policy:
     * - 7+ days before: 100% refund
     * - 3-6 days before: 50% refund
     * - Less than 3 days: No refund
     */
    private double calculateRefund(Booking booking) {
        LocalDate today = LocalDate.now();
        LocalDate checkIn = booking.getCheckInDate();
        long daysUntilCheckIn = ChronoUnit.DAYS.between(today, checkIn);

        if (daysUntilCheckIn >= 7) {
            return booking.getTotalAmount();
        } else if (daysUntilCheckIn >= 3) {
            return booking.getTotalAmount() * 0.5;
        } else {
            return 0;
        }
    }


    public Booking getBooking(String bookingId) {
        return bookings.get(bookingId);
    }

    public List<Booking> getUserBookings(String userId) {
        return userBookings.getOrDefault(userId, new ArrayList<>());
    }

    public void checkIn(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking != null) {
            synchronized (booking) {
                booking.setStatus(BookingStatus.CHECKED_IN);
                System.out.println("Checked in: " + bookingId);
            }
        }
    }

    public void checkOut(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking != null) {
            synchronized (booking) {
                booking.setStatus(BookingStatus.CHECKED_OUT);
                System.out.println("Checked out: " + bookingId);
            }
        }
    }
}

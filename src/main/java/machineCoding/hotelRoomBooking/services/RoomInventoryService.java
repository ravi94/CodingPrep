package machineCoding.hotelRoomBooking.services;

import machineCoding.hotelRoomBooking.entities.Booking;
import machineCoding.hotelRoomBooking.entities.DateRange;
import machineCoding.hotelRoomBooking.entities.Room;
import machineCoding.hotelRoomBooking.entities.enums.BookingStatus;
import machineCoding.hotelRoomBooking.entities.enums.RoomType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class RoomInventoryService {
    private Map<String, Room> rooms; // roomId -> Room
    private Map<String, List<Booking>> roomBookings; // roomId -> List<Booking>
    private Map<String, ReadWriteLock> roomLocks; // roomId -> Lock

    public RoomInventoryService() {
        this.rooms = new ConcurrentHashMap<>();
        this.roomBookings = new ConcurrentHashMap<>();
        this.roomLocks = new ConcurrentHashMap<>();
    }

    public void addRoom(Room room){
        rooms.put(room.getRoomId(), room);
        roomBookings.put(room.getRoomId(),new ArrayList<>());
        roomLocks.put(room.getRoomId(),new ReentrantReadWriteLock());
        System.out.println("Added room : "+ room);
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }
    public List<Room> getRoomsByType(RoomType roomType) {
        return rooms.values().stream().filter(room -> room.getRoomType().equals(roomType)).collect(Collectors.toList());
    }

    /**
     * Thread-safe: Check if room is available for given date range
     */
    public boolean isRoomAvailable(String roomId, DateRange dateRange) {
        ReadWriteLock lock = roomLocks.get(roomId);
        if(lock == null) return false;

        lock.readLock().lock();
        try{
            List<Booking> bookings = roomBookings.get(roomId);
            if (bookings == null) return true;

            for(Booking booking : bookings){
                if(!BookingStatus.CANCELLED.equals(booking.getStatus())){
                    DateRange bookingRange = new DateRange(booking.getCheckInDate(),booking.getCheckOutDate());

                    if(dateRange.overlaps(bookingRange))
                        return false;
                }
            }
            return true;

        }finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Thread-safe: Reserve room (add booking)
     */
    public boolean reserveRoom(String roomId, Booking booking) {
        ReadWriteLock lock = roomLocks.get(roomId);
        if (lock == null) return false;

        lock.writeLock().lock();
        try{
            DateRange newRange = new DateRange(
                    booking.getCheckInDate(),
                    booking.getCheckOutDate()
            );

            // Double-check availability
            if (!isRoomAvailableInternal(roomId, newRange)) {
                return false;
            }
            List<Booking> bookings = roomBookings.get(roomId);
            bookings.add(booking);

            System.out.println(String.format("[%s] Reserved %s for %s to %s",
                    Thread.currentThread().getName(),
                    rooms.get(roomId).getRoomNumber(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate()));

            return true;

        }finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Internal method - must be called with lock held
     */
    private boolean isRoomAvailableInternal(String roomId, DateRange dateRange) {
        List<Booking> bookings = roomBookings.get(roomId);
        if (bookings == null) return true;

        for (Booking booking : bookings) {
            if (booking.getStatus() != BookingStatus.CANCELLED) {
                DateRange bookingRange = new DateRange(
                        booking.getCheckInDate(),
                        booking.getCheckOutDate()
                );

                if (dateRange.overlaps(bookingRange)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Find available rooms by type and date range
     */

    public List<Room> findAvailableRooms(RoomType roomType, DateRange dateRange) {
        List<Room> availableRooms = new ArrayList<>();
        List<Room> roomsOfType = getRoomsByType(roomType);

        for(Room room : roomsOfType){
            if(isRoomAvailable(room.getRoomId(),dateRange)){
                availableRooms.add(room);
            }
        }

        return availableRooms;
    }

}

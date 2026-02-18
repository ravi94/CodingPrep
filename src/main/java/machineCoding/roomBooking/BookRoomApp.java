package machineCoding.roomBooking;

import machineCoding.roomBooking.service.BookingManager;

public class BookRoomApp {
    public static void main(String[] args) {
        BookingManager manager = new BookingManager();

        // Setup Inventory
        manager.addBuilding("Alpha");
        manager.addFloor("Alpha", 1);
        manager.addRoom("Alpha", 1, "C1");

        // Test Bookings
        System.out.println(manager.bookRoom("User1", "Alpha", 1, "C1", 10, 12)); // Success
        System.out.println(manager.bookRoom("User2", "Alpha", 1, "C1", 11, 13)); // Fails (Overlap)
        System.out.println(manager.bookRoom("User3", "Alpha", 1, "C1", 14, 16));
    }
}

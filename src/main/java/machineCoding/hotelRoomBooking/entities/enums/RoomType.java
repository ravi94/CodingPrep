package machineCoding.hotelRoomBooking.entities.enums;

public enum RoomType {
    SINGLE(1000),
    DOUBLE(2000),
    SUITE(5000);

    private final double basePrice;

    RoomType(double basePrice){
        this.basePrice = basePrice;
    }

    public double getBasePrice(){
        return this.basePrice;
    }


}

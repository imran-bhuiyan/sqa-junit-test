import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingTest {

    LocalDateTime s1 = LocalDateTime.of(2025, 10, 8, 10, 0);
    LocalDateTime e1 = LocalDateTime.of(2025, 10, 8, 12, 0);
    VehicleType type= VehicleType.CAR;
    Vehicle v= new Vehicle(1,type,200.0);
    ParkingSlot pt =new ParkingSlot("1",ParkingSlotType.COMPACT);
    Booking book =new Booking(1,v,pt,s1,e1,200.0);

    @Test
    void testBookIdTest(){
        assertEquals(1,book.getBookingId(),"book id not match");
    }
    @Test
    void getVehicleTest(){
        assertNotNull(book.getVehicle(),"vehicle null");
        assertEquals(v,book.getVehicle(),"vehicle not match");
    }
    @Test
    void getParkingSlotsTest(){
        assertNotNull(book.getParkingSlot(),"parking slot null");
        assertEquals(pt, book.getParkingSlot(),"parking slot not match");
    }

    @Test
    void  getStartTimeTest(){
        assertEquals(s1,book.getStartTime(),"start time not match");
    }

    @Test
    void  getEndTime(){
        assertEquals(e1,book.getEndTime(),"end time not match");
    }

    @Test
    void  getAmountTest(){
        assertEquals(200.0,book.getAmount(),"amount not match");
    }
    @Test
    void  getBookingStatusTest(){
        assertEquals(BookingStatus.ACTIVE,book.getBookingStatus(),"booking status not match");
        System.out.println(book);
    }

    @Test
    void  completeBookingTest(){
        Booking book2 = new Booking(2, v, pt, s1, e1, 300.0);
        book2.completeBooking();
      assertEquals(BookingStatus.COMPLETED,book2.getBookingStatus(),"booking status not match");
    }

    @Test
    void  cancelBookingTest(){
        Booking book3 = new Booking(3, v, pt, s1, e1, 400.0);
        book3.cancelBooking();
        assertEquals(BookingStatus.CANCELLED,book3.getBookingStatus(),"booking status not match");
    }

    @Test
    void mixtesting(){
        Booking book3 = new Booking(3, v, pt, s1, e1, 400.0);
        book3.cancelBooking();
        book3.completeBooking();
        assertNotEquals(BookingStatus.CANCELLED,book3.getBookingStatus(),"booking status not match");
    }


    @Test
    void toStringTesting() {
        String expected = "Booking{" +
                "bookingId=" + book.getBookingId() +
                ", vehicle=" + v + "\n" +
                ", parkingSlot=" + pt +
                ", startTime=" + s1 +
                ", endTime=" + e1 +
                ", amount=" +book.getAmount() +
                ", bookingStatus=" + book.getBookingStatus() +
                "}";
        assertNotNull(book,"book cant be null");
        assertEquals(expected, book.toString(), "object output doesnot match");
    }

    @Test
    void nullTestingOfParkingSlotObject() {
        assertThrows(Exception.class, () -> {
           Booking b= new Booking(5, v, null, s1, e1, 100.0);});
    }

    @Test
    void nullVehicleObjectTest() {
        assertThrows(Exception.class, () -> {
            new Booking(4, null, pt, s1, e1, 200.0);
        });
    }
    @Test
    void negativeAmountTest() {
        assertThrows(Exception.class, () -> {new Booking(5, v, pt, s1, e1, -20.0);});
    }
    @Test
    void TimingTest() {
        LocalDateTime start = LocalDateTime.of(2025, 10, 8, 12, 0);
        LocalDateTime end = LocalDateTime.of(2025, 10, 8, 10, 0);
        Booking b = new Booking(11, v, pt, start, end, 200.0);
        assertTrue(b.getStartTime().isAfter(b.getEndTime()));
    }

    @Test
    void TimingTest2() {
        LocalDateTime start = LocalDateTime.of(2025,10,8,10,0);
        LocalDateTime end = LocalDateTime.of(2025,10,8,10,0);
        Booking b = new Booking(12, v, pt, start, end, 100);
        assertEquals(start, b.getStartTime());
        assertEquals(end, b.getEndTime());

    }

    @Test
    void nullTimingTest() {
        LocalDateTime start = null;
        LocalDateTime end = null;
        Booking b = new Booking(11, v, pt, start, end, 200.0);
        assertThrows(Exception.class, () -> {new Booking(4, v, pt, s1, e1, 200.0);});

    }

    @Test
    void zeroAmountTesting() {

        assertThrows(Exception.class, () -> {new Booking(4, v, pt, s1, e1, 0.0);});

    }



}

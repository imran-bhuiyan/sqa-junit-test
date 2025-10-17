import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

public class ParkingSystemTest {

    ParkingSystem system = ParkingSystem.getInstance();

    VehicleType vehicleType = VehicleType.CAR;
    ParkingSlotType slotType = ParkingSlotType.REGULAR;

    Wallet vehicleWallet = new Wallet(1000.0);
    Vehicle vehicle = new Vehicle(1, vehicleType, vehicleWallet);

    Wallet slotWallet = new Wallet(0.0);
    ParkingSlot slot = new ParkingSlot("S1", slotType);

    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = startTime.plusHours(2);


    @Test
    public void singletonInstanceTest() {
        ParkingSystem s1 = ParkingSystem.getInstance();
        ParkingSystem s2 = ParkingSystem.getInstance();
        assertSame(s1, s2, "ParkingSystem instance should be singleton");
    }

    @Test
    public void addVehicleAndParkingSlotTest() {
        system.addVehicle(vehicle);
        system.addParkingSlot(slot);

        assertTrue(system.getVehicles().contains(vehicle), "Vehicle should be added");
        assertTrue(system.getParkingSlots().contains(slot), "Parking slot should be added");
    }

    @Test
    public void getAvailableParkingSlotsTest() {
        system.addParkingSlot(slot);
        List<ParkingSlot> available = system.getAvailableParkingSlots(vehicle, startTime, endTime);
        assertNotNull(available, "Available slots should not be null");
        assertTrue(available.size() >= 0, "Available slots list should be valid");
    }

    @Test
    public void successfulBookingTest() {
        system.addVehicle(vehicle);
        system.addParkingSlot(slot);

        double initialVehicleBalance = vehicle.getWallet().getBalance();
        double initialSystemBalance = system.getBalance();

        Booking booking = system.book(vehicle, slot, startTime, endTime);
        assertNotNull(booking, "Booking should be created");

        double expectedHours = java.time.Duration.between(startTime, endTime).toHours();
        double expectedAmount = expectedHours * system.getPARKING_RATE_PER_HOUR();

        assertEquals(initialVehicleBalance - expectedAmount, vehicle.getWallet().getBalance(), 0.01, "Vehicle balance should be reduced");
        assertEquals(initialSystemBalance + expectedAmount, system.getBalance(), 0.01, "System balance should increase");
    }

    @Test
    public void invalidBookingTimeTest() {
        assertThrows(IllegalBookingTimeException.class, () -> {
            system.book(vehicle, slot, endTime, startTime);
        });
    }

    @Test
    public void completeBookingTest() {
        Booking booking = system.book(vehicle, slot, startTime, endTime);
        double systemBalanceBefore = system.getBalance();
        double slotBalanceBefore = slot.getWallet().getBalance();

        system.completeBooking(booking);

        assertEquals(slotBalanceBefore + (booking.getAmount() * 0.8), slot.getWallet().getBalance(), 0.01, "Slot wallet should get 80%");
        assertEquals(systemBalanceBefore - (booking.getAmount() * 0.8), system.getBalance(), 0.01, "System balance reduced after transfer");
    }

    @Test
    public void cancelBookingTest() {
        Booking booking = system.book(vehicle, slot, startTime, endTime);
        double vehicleBalanceBefore = vehicle.getWallet().getBalance();
        double systemBalanceBefore = system.getBalance();

        system.cancelBooking(booking);

        assertEquals(vehicleBalanceBefore + (booking.getAmount() * 0.9), vehicle.getWallet().getBalance(), 0.01, "Refund 90% to vehicle wallet");
        assertEquals(systemBalanceBefore - (booking.getAmount() * 0.9), system.getBalance(), 0.01, "System balance reduced after refund");
    }

    @Test
    public void illegalBookingArgumentTest() {
        ParkingSlot incompatibleSlot = new ParkingSlot("S2", ParkingSlotType.COMPACT);
        assertThrows(IllegalArgumentException.class, () -> {
            system.book(vehicle, incompatibleSlot, startTime, endTime);
        });
    }

    @Test
    public void rateAndMultiplierTest() {
        double rate = system.getPARKING_RATE_PER_HOUR();
        assertEquals(10.0, rate, "Default parking rate should be 10.0");
    }
}

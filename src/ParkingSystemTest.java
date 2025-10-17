import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingSystemTest {

    private ParkingSystem system;
    private Vehicle vehicleCar;
    private Vehicle vehicleMotorcycle;
    private Vehicle vehicleMicrocar;
    private Vehicle vehicleBus;
    private ParkingSlot slotCompact;
    private ParkingSlot slotRegular;
    private ParkingSlot slotLarge;

    // This setup method is essential. It resets the singleton before each test
    // to ensure they are independent and don't interfere with each other.
    @BeforeEach
    void setUp() throws Exception {
        // This reflection code resets the private static instance.
        // It's a standard technique for testing singletons, as found on Stack Overflow (thread 8918318).
        Field instanceField = ParkingSystem.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        // Now, getInstance() will create a fresh, clean object for this test.
        system = ParkingSystem.getInstance();

        // Setup common vehicles and slots for tests
        vehicleCar = new Vehicle(11, VehicleType.CAR, 500.0);
        vehicleMotorcycle = new Vehicle(12, VehicleType.MOTORCYCLE, 300.0);
        vehicleMicrocar = new Vehicle(13, VehicleType.MICROCAR, 250.0);
        vehicleBus = new Vehicle(14, VehicleType.BUS, 1000.0);

        slotCompact = new ParkingSlot("C1", ParkingSlotType.COMPACT);
        slotRegular = new ParkingSlot("R1", ParkingSlotType.REGULAR);
        slotLarge = new ParkingSlot("L1", ParkingSlotType.LARGE);

        system.addVehicle(vehicleCar);
        system.addVehicle(vehicleMotorcycle);
        system.addVehicle(vehicleMicrocar);
        system.addVehicle(vehicleBus);

        system.addParkingSlot(slotCompact);
        system.addParkingSlot(slotRegular);
        system.addParkingSlot(slotLarge);
    }

    @Test
    @DisplayName("Test singleton pattern ensures one instance")
    void testGetInstanceSingleton() {
        ParkingSystem instance1 = ParkingSystem.getInstance();
        assertSame(system, instance1, "getInstance() should return the same instance created in setUp");
    }

    // --- Tests for Slot Availability and Compatibility ---

    @Test
    @DisplayName("Should find correct available slots for a CAR")
    void testGetAvailableParkingSlots_ForCar() {
        List<ParkingSlot> slots = system.getAvailableParkingSlots(vehicleCar, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        assertTrue(slots.contains(slotRegular), "CAR should be compatible with REGULAR slot");
        assertTrue(slots.contains(slotLarge), "CAR should be compatible with LARGE slot");
        assertFalse(slots.contains(slotCompact), "CAR should NOT be compatible with COMPACT slot");
    }

    @Test
    @DisplayName("Booked slots should not appear in available list")
    void testGetAvailableSlots_ExcludesBookedSlots() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);

        system.book(vehicleCar, slotRegular, start, end); // Book the slot

        List<ParkingSlot> available = system.getAvailableParkingSlots(vehicleCar, start, end);

        assertFalse(available.contains(slotRegular), "Booked slot should not be in the available list");
    }

    // --- Tests for the Booking Process ---

    @Test
    @DisplayName("A successful booking should become ACTIVE and transfer funds")
    void testBook_Successful() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        double initialCarBalance = vehicleCar.getWallet().getBalance();

        Booking booking = system.book(vehicleCar, slotRegular, start, end);

        assertNotNull(booking);
        assertEquals(BookingStatus.ACTIVE, booking.getBookingStatus());
        assertEquals(1, system.getBookings().size());

        double expectedPrice = 20.0; // 2h * 10.0 rate
        assertEquals(initialCarBalance - expectedPrice, vehicleCar.getWallet().getBalance());
    }

    @Test
    @DisplayName("Booking should fail if vehicle has insufficient funds")
    void testBook_FailsWithInsufficientFunds() {
        Vehicle poorVehicle = new Vehicle(201, VehicleType.CAR, 10.0); // Price will be 20.0
        assertThrows(InsufficientFundsException.class, () -> {
            system.book(poorVehicle, slotRegular, LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        });
    }

    @Test
    @DisplayName("Booking should fail if a slot is already booked for an overlapping time")
    void testBook_FailsForOverlappingTime() {
        system.book(vehicleCar, slotRegular, LocalDateTime.now(), LocalDateTime.now().plusHours(2));

        // Attempt to book an overlapping time on the same slot
        assertThrows(IllegalArgumentException.class, () -> {
            system.book(vehicleMotorcycle, slotRegular, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3));
        });
    }

    // --- Tests for Pricing Logic ---

    @Test
    @DisplayName("Should correctly calculate price for Motorcycle in Compact slot")
    void testPricing_MotorcycleInCompact() {
        Booking booking = system.book(vehicleMotorcycle, slotCompact, LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        // 2h * 10.0 (base) * 0.5 (vehicle) * 0.8 (slot) = 8.0
        assertEquals(8.0, booking.getAmount());
    }

    @Test
    @DisplayName("Should truncate fractional hours for pricing")
    void testPricing_FractionalHours() {
        // Booking for 1 hour and 30 minutes should be charged for 1 hour
        Booking booking = system.book(vehicleCar, slotRegular, LocalDateTime.now(), LocalDateTime.now().plusMinutes(90));
        assertEquals(10.0, booking.getAmount()); // 1h * 10.0 rate
    }

    @Test
    @DisplayName("[FAIL] Defect found: Microcar is charged the incorrect default rate")
    void testPricing_DefectForMicrocar() {
        // Analysis: The pricing logic is missing a case for MICROCAR, so it defaults to the CAR rate (1.0).
        Booking booking = system.book(vehicleMicrocar, slotCompact, LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        double actualPrice = booking.getAmount();

        // The actual price is 2h * 10.0 * 1.0 (default rate) * 0.8 = 16.0
        double priceUsingCarRate = 16.0;

        // This test fails by confirming the price is wrong. It should ideally be a lower rate.
        assertEquals(priceUsingCarRate, actualPrice, "Defect: Microcar is incorrectly charged the same rate as a Car.");
    }

    // --- Tests for Post-Booking Actions ---

    @Test
    @DisplayName("Completing a booking should pay 80% to the slot owner")
    void testCompleteBooking_Successful() {
        Booking booking = system.book(vehicleCar, slotRegular, LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        double initialSlotBalance = slotRegular.getWallet().getBalance();

        system.completeBooking(booking);

        assertEquals(BookingStatus.COMPLETED, booking.getBookingStatus());
        assertEquals(initialSlotBalance + 16.0, slotRegular.getWallet().getBalance(), "Slot should receive 80% of the 20.0 fee");
    }

    @Test
    @DisplayName("Cancelling a booking should refund 90% to the vehicle")
    void testCancelBooking_Successful() {
        Booking booking = system.book(vehicleCar, slotRegular, LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        double initialVehicleBalance = vehicleCar.getWallet().getBalance();

        system.cancelBooking(booking);

        assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus());
        assertEquals(initialVehicleBalance + 18.0, vehicleCar.getWallet().getBalance(), "Vehicle should be refunded 90% of the 20.0 fee");
    }

    @Test
    @DisplayName("[FAIL] Defect found: System allows completing an already CANCELLED booking")
    void testCompleteBooking_DefectWhenAlreadyCancelled() {
        Booking booking = system.book(vehicleCar, slotRegular, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        system.cancelBooking(booking);

        double slotBalanceAfterCancel = slotRegular.getWallet().getBalance();

        // This action should be prevented, but the defect allows it
        system.completeBooking(booking);

        // This assertion will fail, because the defective code transfers money to the slot.
        assertEquals(slotBalanceAfterCancel, slotRegular.getWallet().getBalance(), "Slot balance should not change for a cancelled booking.");
    }

    @Test
    @DisplayName("[FAIL] Defect found: System allows cancelling an already COMPLETED booking")
    void testCancelBooking_DefectWhenAlreadyCompleted() {
        Booking booking = system.book(vehicleCar, slotRegular, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        system.completeBooking(booking);

        double vehicleBalanceAfterComplete = vehicleCar.getWallet().getBalance();

        // This action should be prevented, but the defect allows it
        system.cancelBooking(booking);

        // This assertion will fail, because the defective code refunds the vehicle.
        assertEquals(vehicleBalanceAfterComplete, vehicleCar.getWallet().getBalance(), "Vehicle should not receive a refund for a completed booking.");
    }
}
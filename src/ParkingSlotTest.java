import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


public class ParkingSlotTest {

    ParkingSlotType parType= ParkingSlotType.COMPACT;

    ParkingSlot par = new ParkingSlot("1",parType);


    @Test
    public void slotIDTest() {

        String slotID = par.getSlotId();
        assertEquals(slotID, par.getSlotId());

    }

    @Test
    public void slotTypeTest() {

       assertSame(parType, par.getSlotType());
    }
    @Test
    public void isActiveTest() {

        assertTrue(par.isActive());
    }

    @Test
    public void isactiveTest() {

        assertTrue(par.isActive());
    }

    @Test
    public void getWalletTest() {

      assertNotNull(par.getWallet());
    }
    @Test
    public void getBookingsTest() {

        assertNotNull(par.getBookings());

    }

    @Test
    public void isCompatible_IFisActiveTest() {

        VehicleType vType=VehicleType.MOTORCYCLE;
        LocalDateTime start = LocalDateTime.of(2025, 10, 8, 18, 0);
        LocalDateTime end = LocalDateTime.of(2025, 10, 8, 18, 0);

        boolean a= par.isActive();
        System.out.println(a);
        assertTrue(par.isCompatible(vType,start,end));

    }

    @Test
    public void deactivateTest() {

        par.deactivate();
        assertFalse(par.isActive());

        VehicleType vType=VehicleType.MOTORCYCLE;
        LocalDateTime start = LocalDateTime.of(2025, 10, 8, 18, 0);
        LocalDateTime end = LocalDateTime.of(2025, 10, 8, 20, 0);

        assertFalse(par.isCompatible(vType,start,end));


    }

    @Test
    public void vehicleCompatibilityTypeTest() {


        LocalDateTime s1 = LocalDateTime.of(2025, 10, 8, 10, 0);
        LocalDateTime e1 = LocalDateTime.of(2025, 10, 8, 12, 0);

        //Testing for car
        //car mainly uses Regular and large

        //testing using Compact which is unvalid

        ParkingSlotType parType= ParkingSlotType.COMPACT;

        ParkingSlot par1 = new ParkingSlot("1",parType);
        VehicleType type = VehicleType.CAR;

        assertFalse(par1.isCompatible(type, e1, s1));

        //Car, real Compatibility Test
        parType= ParkingSlotType.LARGE;
        ParkingSlot par2 = new ParkingSlot("1",parType);

        assertTrue(par2.isCompatible(type, e1, s1),"Only Large and regular valid for Car type");



    }

    @Test
    public void isCompatibleTesting_foroverlap_forNotOverlap() {

        VehicleType type=VehicleType.MOTORCYCLE;

        //creating booking 1
        Vehicle v1=new Vehicle(1,VehicleType.CAR,new Wallet());
        LocalDateTime s1 = LocalDateTime.of(2025, 10, 8, 10, 0);
        LocalDateTime e1 = LocalDateTime.of(2025, 10, 8, 12, 0);
//        ParkingSlot slot1 = new ParkingSlot("1",ParkingSlotType.COMPACT);

        Booking b1=new Booking(1,v1,par,s1,e1,200);

        //creating booking 2

        Vehicle v2=new Vehicle(2,VehicleType.MICROCAR,new Wallet());
        LocalDateTime s2 = LocalDateTime.of(2025, 10, 8, 13, 0);
        LocalDateTime e2 = LocalDateTime.of(2025, 10, 8, 15, 0);

        Booking b2=new Booking(2,v2,par,s2,e2 ,200);

        //creating booking 3

        Vehicle v3=new Vehicle(3,VehicleType.BICYCLE,new Wallet());
        LocalDateTime s3 = LocalDateTime.of(2025, 10, 8, 16, 0);
        LocalDateTime e3 = LocalDateTime.of(2025, 10, 8, 18, 0);

        Booking b3=new Booking(3,v3,par,s3,e3 ,200);

        par.getBookings().add(b1);
        par.getBookings().add(b2);
        par.getBookings().add(b3);

        //Now testing isCompatible method
        VehicleType vType=VehicleType.MOTORCYCLE;
        LocalDateTime start = LocalDateTime.of(2025, 10, 8, 19, 0);
        LocalDateTime end = LocalDateTime.of(2025, 10, 8, 18, 0);



      //for not time overlapping problem
        System.out.println( par.isActive());
        assertTrue(par.isCompatible(vType,start,end),"Time overlapping problem");

//        par.isCompatible(vType,start,end);


        //for time overlapping testing

        VehicleType vType1=VehicleType.MOTORCYCLE;
        LocalDateTime start1 = LocalDateTime.of(2025, 10, 8, 15, 0);
        LocalDateTime end1 = LocalDateTime.of(2025, 10, 8, 18, 0);
        assertFalse(par.isCompatible(vType1,start1,end1),"No time overlapping problem");




    }



}

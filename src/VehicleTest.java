
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VehicleTest {


    VehicleType vehicleType=VehicleType.CAR;

    Wallet w=new Wallet(20.0);

    Vehicle v=new Vehicle(1,vehicleType,w);


    @Test

    public void getVehicleIdTest() {

        assertEquals(1,v.getVehicleId(),"vehicle id is not correct");

    }

    @Test

    public void getVehicleTypeTest() {
//        vehicleType=VehicleType.TRUCK;

        assertEquals(vehicleType,v.getVehicleType(),"vehicle type is not correct");

    }
    @Test
    public void getWalletTest() {
//        Wallet w1=new Wallet();
        assertEquals(w,v.getWallet(),"wallet is not correct");


    }


    @Test
    public void vehicleWalletBalanceTest() {
        Vehicle v1=new Vehicle(1,vehicleType,200);

        assertEquals(200,v1.getWallet().getBalance(),"vehicle id is not correct");


    }

    @Test
    public void tosrtingTest() {


           String ObjectOutput = "Vehicle{vehicleId=1, vehicleType=CAR, walletBalance=20.0}";
            assertNotNull(v,"this object should not be null");
            assertEquals(ObjectOutput,v.toString(), " object output is not correct");
        }
    @Test
    public void VehicleNullWalletWithToStringTEst() {
        Vehicle v2 = new Vehicle(2, vehicleType, null);
        assertNull(v2.getWallet());
        v2.toString();
    }
    @Test
    public void nullVehicleTypeWithTosrtingTest() {
        Vehicle v3 = new Vehicle(3, null, w);
        assertNull(v3.getVehicleType());
        v3.toString();
    }

    @Test
    public void NegativeBalanceTest() {
        assertThrows(Exception.class,()->new Vehicle(4, vehicleType, -20));
    }

    @Test
    public void nullVehicleTypeTest() {
        Vehicle v= new Vehicle(3, null, w);
        System.out.println(v.getVehicleType());

        assertThrows(Exception.class,()->new Vehicle(3, null, w));

    }

    @Test
    public void NullVehicleWalletTest() {
        Vehicle v= new Vehicle(3, vehicleType, null);
        System.out.println(v.getWallet());

        assertThrows(Exception.class,()->new Vehicle(2, vehicleType, null));
    }

    @Test
    public void NullVehicleWalletGetterTest() {
        Vehicle v= new Vehicle(3, vehicleType, null);

        assertNull(v.getWallet());

        assertThrows(Exception.class,()->v.getWallet());

    }
    @Test
    public void nullVehicleTypeGetterTest() {
        Vehicle v= new Vehicle(3, null, w);

        assertNull(v.getVehicleType());

        assertThrows(Exception.class,()->v.getVehicleType());

    }





}





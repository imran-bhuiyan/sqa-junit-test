import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WalletTest {

   @Test
    public void initial_wallet_balance_check() {
       Wallet w=new Wallet();
       System.out.println(w.getBalance());

       assertEquals(0.0,w.getBalance(),"Intial balance should be zero");
   }

   @Test
    public void adding_balance_then_check() {

       Wallet w1=new Wallet(12.00);
       assertEquals(12.00,w1.getBalance(),"adding balance should be equal to 12");

   }

   @Test
    public void addFundTest() {
       Wallet w1=new Wallet(12);
       w1.addFunds(12.40);
       assertEquals(24.40,w1.getBalance(),"adding funds should be equal to 24");

   }
    @Test
    public void deductFundsTest() {
        Wallet w1=new Wallet(12);
        w1.addFunds(20.45);
        w1.deductFunds(12.35);
        assertEquals(20.1,w1.getBalance(),"Deducting funds should be equal to 20");

    }

    // withdraw more than available
    @Test
    public void deductFundsExceptionTest() {
        Wallet w1=new Wallet(12);
        w1.addFunds(20);
        InsufficientFundsException ex= assertThrows(
                InsufficientFundsException.class,
                () -> w1.deductFunds(200.45));

        assertEquals("Insufficient funds in wallet.", ex.getMessage());

    }
    @Test
    public void addFundsInvalidExceptionTest() {
        Wallet w1=new Wallet(12.00);

    //for negative balance testing
        InvalidAmountException ex= assertThrows(
                InvalidAmountException.class,
                () -> w1.addFunds(-2.45));

        assertEquals("Invalid amount. Amount must be positive.", ex.getMessage());

        //for zero balance testing
        InvalidAmountException ex2= assertThrows(
                InvalidAmountException.class,
                () -> w1.addFunds(0));

        assertEquals("Invalid amount. Amount must be positive.", ex2.getMessage());



    }
//deducting fund with negative amount
    @Test
    public void deductFundsNegativeAmountTest() {
        Wallet w1 = new Wallet(50);

        InvalidAmountException ex = assertThrows(
                InvalidAmountException.class,
                () -> w1.deductFunds(-25));

        assertEquals("Invalid amount. Amount must be positive.", ex.getMessage());

    }

    @Test
    public void deductZeroAmountTest() {
        Wallet w1 = new Wallet(50);

        InvalidAmountException ex = assertThrows(
                InvalidAmountException.class,
                () -> w1.deductFunds(0));

        assertEquals("Invalid amount. Amount must be positive.", ex.getMessage());

    }


    @Test
    public void TranferFundsTest1() {

        Wallet toWallet=new Wallet();
        Wallet w1=new Wallet(12);


        //with negative amount
        InvalidAmountException ex1= assertThrows(
                InvalidAmountException.class,
                () -> w1.transferFunds(toWallet,-2));
        assertEquals("Invalid amount. Amount must be positive.", ex1.getMessage());

        ///with 0 amount
        InvalidAmountException ex2= assertThrows(
                InvalidAmountException.class,
                () -> w1.transferFunds(toWallet,0));
        assertEquals("Invalid amount. Amount must be positive.", ex2.getMessage());




//


    }

//here testing if the balance is insufficient to transfer from sender
    @Test
    public void TranferFundsTest2() {

        Wallet w1=new Wallet(12);
        Wallet toWallet=new Wallet();
        InsufficientFundsException In = assertThrows(

                InsufficientFundsException.class,
                () -> w1.transferFunds(toWallet,20));

        assertEquals("Insufficient funds in wallet.", In.getMessage());


    }


    @Test
    public void TranferFundsTest3() {
        Wallet w1=new Wallet(12);
        Wallet toWallet=new Wallet();


        // here im checking whether the tranfer balance
        double a=w1.getBalance();
        double b=8;
        w1.transferFunds(toWallet,b);
        double c=a-b;

//        InvalidAmountException ex= assertThrows(
//                InvalidAmountException.class,
//                () -> w1.transferFunds(toWallet,-2)
//
//        );
//
//        InsufficientFundsException In = assertThrows(
//
//                InsufficientFundsException.class,
//                () -> w1.transferFunds(toWallet,20)
//
//        );

        assertAll("TranferFundsTest Method Testing",
//                () -> assertEquals("Invalid amount. Amount must be positive.", ex.getMessage()) ,
//                () -> assertEquals("Insufficient funds in wallet.",In.getMessage()),
                () -> assertEquals(c,w1.getBalance()),
                () -> assertEquals(b,toWallet.getBalance()));


    }



    @Test
    public void negativeBalaceInitializationTest () {
        assertThrows(InvalidAmountException.class,()-> new Wallet(-2)
        );


    }

    @Test
    public void nullobjectTest() {

        Wallet w1=new Wallet(12);
        Wallet toWallet=new Wallet();
        toWallet=null;

        w1.transferFunds(toWallet,2);

    }



    @Test
    public void floatingPointTesting(){
        Wallet w = new Wallet();

        w.addFunds(0.4);
        w.addFunds(0.55);

        assertEquals(0.95, w.getBalance(), "Floating-point Problem " );
    }








}

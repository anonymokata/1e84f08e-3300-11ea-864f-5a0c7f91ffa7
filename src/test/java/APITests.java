import Controller.Checkout;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Field;

import static junit.framework.TestCase.assertEquals;

@RunWith(JUnit4.class)
public class APITests  {


    Checkout checkout = Checkout.getInstance();
    @Before
    public void resetSingletons() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException  {
        Field checkoutInstance = Checkout.class.getDeclaredField("obj");
        checkoutInstance.setAccessible(true);
        checkoutInstance.set(null, null);
    }



    @Test
    public void validateUnitTotals() {

        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createInventoryItem("Chicken Soup", "Unit", 1.00);
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Chicken Soup");
        checkout.scanAnItemAtCheckout("Chicken Soup");

        assertEquals(3.50, checkout.getTotal());
    }
}

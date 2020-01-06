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

    @Test
    public void validateUnitTotalsWithMarkdownDiscount() {
        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createInventoryItem("Chicken Soup", "Unit", 1.00);
        checkout.createDiscount("Tomato Soup", "Tomato Soup Markdown", 1.00, 0, "Markdown", "Currency");
        checkout.createDiscount("Chicken Soup", "Chicken Soup Markdown", .50, 0, "Markdown", "Currency");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Chicken Soup");
        checkout.scanAnItemAtCheckout("Chicken Soup");
        checkout.scanAnItemAtCheckout("Chicken Soup");

        assertEquals(2.5, checkout.getTotal());
    }

    @Test
    public void validateUnitTotalsWithMarkdownDiscountIncludeLimit() {
        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createDiscount("Tomato Soup", "Tomato Soup Markdown", .50, 0, "Markdown", "Currency", 1);
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");

        assertEquals(2.5, checkout.getTotal());
    }

    @Test
    public void validateWeightTotalsWithMarkdownDiscount() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", .50, 0, "Markdown", "Currency");

        checkout.scanAnItemAtCheckout("Ground Beef", 2);

        assertEquals(3.00, checkout.getTotal());
    }

    @Test
    public void validateWeightTotalsWithMarkdownDiscountIncludeLimit() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", .50, 0, "Markdown", "Currency", 1);

        checkout.scanAnItemAtCheckout("Ground Beef", 2);

        assertEquals(1.50, checkout.getTotal());
    }
}

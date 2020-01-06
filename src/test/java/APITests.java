import Controller.Checkout;
import Services.InventoryService;
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
        Field inventoryInstance = InventoryService.class.getDeclaredField("obj");
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
    public void validateUnitTotalsWithMarkdownDiscountCurrency() {
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
    public void validateUnitTotalsWithMarkdownDiscountIncludeLimitCurrency() {
        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createDiscount("Tomato Soup", "Tomato Soup Markdown", .50, 0, "Markdown", "Currency", 1);
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");

        assertEquals(2.5, checkout.getTotal());
    }

    @Test
    public void validateWeightTotalsWithMarkdownDiscountCurrency() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", .50, 0, "Markdown", "Currency");

        checkout.scanAnItemAtCheckout("Ground Beef", 2);

        assertEquals(3.00, checkout.getTotal());
    }

    @Test
    public void validateWeightTotalsWithMarkdownDiscountIncludeLimitCurrency() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", .50, 0, "Markdown", "Currency", 1);

        checkout.scanAnItemAtCheckout("Ground Beef", 2);
        checkout.scanAnItemAtCheckout("Ground Beef", 2);

        assertEquals(7.00, checkout.getTotal());
    }

    @Test
    public void validateUnitTotalsWithBxgyDiscountCurrency() {
        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createDiscount("Tomato Soup", "Tomato Soup Bxgy", 1.50, 1, "BXGY", "Currency");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");

        assertEquals(3.00, checkout.getTotal());
    }

    @Test
    public void validateUnitTotalsWithBxgyDiscountAndLimitCurrency() {
        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createDiscount("Tomato Soup", "Tomato Soup Bxgy", 1.50, 1, "BXGY", "Currency", 1);
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");

        assertEquals(4.50, checkout.getTotal());
    }

    @Test
    public void validateWeightTotalWithBxgyDiscountCurrency() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 2, 1, "BXGY", "Currency", 1);

        checkout.scanAnItemAtCheckout("Ground Beef", 2);
        assertEquals(2.00, checkout.getTotal());
    }

    @Test
    public void validateWeightTotalWithBxgyDiscountAndLimitCurrency() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 2, 1, "BXGY", "Currency", 1);

        checkout.scanAnItemAtCheckout("Ground Beef", 2);
        checkout.scanAnItemAtCheckout("Ground Beef", 2);
        assertEquals(6.00, checkout.getTotal());
    }
}

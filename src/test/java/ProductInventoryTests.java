import Controller.Checkout;
import Entities.Product;
import Services.CheckoutService;
import Services.DiscountService;
import Services.InventoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotSame;

@RunWith(JUnit4.class)
public class ProductInventoryTests {

    InventoryService inventoryService;
    Checkout checkout;
    CheckoutService checkoutService;

    @Before
    public void setUp() {
        checkout = new Checkout();
        checkoutService = new CheckoutService(checkout);
        inventoryService = new InventoryService(checkoutService);
    }

    @Test
    public void validateThatAUnitBasedProductCanBeCreated() {
        Product product = new Product("Tomato Soup", Product.PricingMethod.Unit, 99);
        assertEquals(product, inventoryService.createInventoryProduct("Tomato Soup", "Unit", 99));
    }

    @Test
    public void validateThatAWeightBasedProductCanBeCreate() {
        Product product = new Product("Carved Turkey", Product.PricingMethod.Weighted, 99, 1200);
        assertEquals(product, inventoryService.createInventoryProduct("Carved Turkey", "Weighted", 99, 1200));
    }

    @Test
    public void validateThatAnInventoryProductCanBeRemoved() {
        inventoryService.createInventoryProduct("Carved Turkey", "Weighted", 99, 1200);
        assertEquals("Soup removed.", inventoryService.removeInventoryProduct("Soup"));
    }
}

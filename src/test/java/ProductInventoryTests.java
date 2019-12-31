import Entities.Product;
import Services.InventoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotSame;

@RunWith(JUnit4.class)
public class ProductInventoryTests {

    InventoryService inventoryService;

    @Before
    public void setUp() {
        inventoryService = InventoryService.getInstance();
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

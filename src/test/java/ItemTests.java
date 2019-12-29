import Entities.Product;
import Services.ItemService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotSame;

@RunWith(JUnit4.class)
public class ItemTests {

    ItemService itemService;

    @Before
    public void setUp() {
        itemService = new ItemService();
    }

    @Test
    public void validateThatAUnitBasedProductCanBeCreated() {
        Product product = new Product("Tomato Soup", Product.PricingMethod.Unit, 99);
        assertEquals(product, itemService.createInventoryProduct("Tomato Soup", "Unit", 99));
    }

    @Test
    public void validateThatAWeightBasedProductCanBeCreate() {
        Product product = new Product("Carved Turkey", Product.PricingMethod.Weighted, 99, 1200);
        assertEquals(product, itemService.createInventoryProduct("Carved Turkey", "Weighted", 99, 1200));
    }

    @Test
    public void validateThatAnInventoryProductCanBeRemoved() {
        itemService.createInventoryProduct("Carved Turkey", "Weighted", 99, 1200);
        assertEquals("Soup removed.", itemService.removeInventoryProduct("Soup"));
    }
}

import Entities.Discount;
import Services.DiscountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.assertEquals;

@RunWith(JUnit4.class)
public class DiscountTests {

    DiscountService discountService;

    @Before
    public void setUp() {
        discountService = new DiscountService();
    }

    @Test
    public void validateThatADiscountHasBeenCreated() {
        Discount discount = new Discount();
        assertEquals(discount, discountService.createDiscount("Soup"));
    }
}

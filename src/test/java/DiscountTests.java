import Entities.Discount;
import Services.DiscountService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;

import static junit.framework.TestCase.assertEquals;

@RunWith(JUnit4.class)
public class DiscountTests {

    @Autowired
    DiscountService discountService;

//    @Before
//    public void setUp() {
//        discountService = DiscountService.getInstance();
//    }
//
//    @Before
//    public void resetSingletons() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException  {
//        Field discountInstance = DiscountService.class.getDeclaredField("obj");
//        discountInstance.setAccessible(true);
//        discountInstance.set(null, null);
//    }




    @Test
    public void validateThatADiscountHasBeenCreated() {
        Discount discount = new Discount("Soup","Markdown For Soup", 99, 2, Discount.DiscountType.Markdown, Discount.ValueType.Currency);
        assertEquals(discount, discountService.createDiscount("Soup", "Markdown For Soup", 99, 2, "Markdown", "Currency"));
    }



    @Test
    public void validateThatADiscountCanBeRemoved() {
        discountService.createDiscount("Soup", "Markdown For Soup", 99, 2, "Markdown", "Currency");
        assertEquals("Successfully removed Markdown For Soup from the discount list.", discountService.removeDiscount("Soup", "Markdown For Soup"));

    }

}

import Controller.Checkout;
import Entities.Discount;
import Services.CheckoutService;
import Services.DiscountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static junit.framework.TestCase.assertEquals;

@RunWith(JUnit4.class)
public class DiscountTests {
    Checkout checkout;
    DiscountService discountService;
    CheckoutService checkoutService;

    @Before
    public void setUp() {
        checkout = new Checkout();
        checkoutService = new CheckoutService(checkout);
        discountService = new DiscountService(checkoutService);
    }
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

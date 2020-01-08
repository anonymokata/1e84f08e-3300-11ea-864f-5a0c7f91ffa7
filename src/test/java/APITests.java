import Controller.Checkout;
import Entities.Discount;
import Services.CheckoutService;
import Services.DiscountService;
import Services.InventoryService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


import javax.persistence.criteria.CriteriaBuilder;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static junit.framework.TestCase.assertEquals;

@RunWith(JUnit4.class)
public class APITests  {



//    CheckoutService checkoutService = CheckoutService.getInstance();
//    InventoryService inventoryService = InventoryService.getInstance();
//    DiscountService discountService = DiscountService.getInstance();

//    @Before
//    public void resetSingletons() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException  {
//        checkout = mock(Checkout.class);
//        setMock(checkout);
//    }
//
    @Before
    public void resetSingleton() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field checkoutInstance = Checkout.class.getDeclaredField("obj");
        Field inventoryServiceInstance = InventoryService.class.getDeclaredField("obj");
        Field checkoutServiceInstance = CheckoutService.class.getDeclaredField("obj");
        Field discountServiceInstance = DiscountService.class.getDeclaredField("obj");
        checkoutInstance.setAccessible(true);
        checkoutInstance.set(null, null);
        inventoryServiceInstance.setAccessible(true);
        inventoryServiceInstance.set(null, null);
        checkoutServiceInstance.setAccessible(true);
        checkoutServiceInstance.set(null, null);
        discountServiceInstance.setAccessible(true);
        discountServiceInstance.set(null, null);
    }

    @After
    public void resetManagerSingleton() throws Exception {
        Field checkoutInstance = Checkout.class.getDeclaredField("obj");
        Field inventoryServiceInstance = InventoryService.class.getDeclaredField("obj");
        Field checkoutServiceInstance = CheckoutService.class.getDeclaredField("obj");
        Field discountServiceInstance = DiscountService.class.getDeclaredField("obj");
        checkoutInstance.setAccessible(true);
        inventoryServiceInstance.setAccessible(true);
        checkoutServiceInstance.setAccessible(true);
        discountServiceInstance.setAccessible(true);

        Constructor checkoutConstructor = Checkout.class.getDeclaredConstructor();
        checkoutConstructor.setAccessible(true);
        checkoutInstance.set(null, checkoutConstructor.newInstance());

        Constructor checkoutServiceConstructor = CheckoutService.class.getDeclaredConstructor();
        checkoutServiceConstructor.setAccessible(true);
        checkoutServiceInstance.set(null, checkoutServiceConstructor.newInstance());

        Constructor inventoryServiceConstructor = InventoryService.class.getDeclaredConstructor();
        inventoryServiceConstructor.setAccessible(true);
        inventoryServiceInstance.set(null, inventoryServiceConstructor.newInstance());

        Constructor discountServiceConstructor = DiscountService.class.getDeclaredConstructor();
        discountServiceConstructor.setAccessible(true);
        discountServiceInstance.set(null, discountServiceConstructor.newInstance());
    }
//
//    private void setMock(Checkout mock) {
//        Field instance;
//        try {
//            instance = Checkout.class.getDeclaredField("obj");
//            instance.setAccessible(true);
//            instance.set(instance, mock);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Test
    public void validateUnitTotals() {
        Checkout checkout = Checkout.getInstance();
        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createInventoryItem("Chicken Soup", "Unit", 1.00);
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Chicken Soup");
        checkout.scanAnItemAtCheckout("Chicken Soup");

        assertEquals(3.50, checkout.getTotal());
    }

    @Test
    public void validateUnitTotalsWithMarkdownDiscountCurrency() {
        Checkout checkoutMarkdownCurrency = Checkout.getInstance();
        checkoutMarkdownCurrency.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkoutMarkdownCurrency.createInventoryItem("Chicken Soup", "Unit", 1.00);
        checkoutMarkdownCurrency.createDiscount("Tomato Soup", "Tomato Soup Markdown", 1.00, 0, "Markdown", "Currency");
        checkoutMarkdownCurrency.createDiscount("Chicken Soup", "Chicken Soup Markdown", .50, 0, "Markdown", "Currency");
        checkoutMarkdownCurrency.scanAnItemAtCheckout("Tomato Soup");
        checkoutMarkdownCurrency.scanAnItemAtCheckout("Tomato Soup");
        checkoutMarkdownCurrency.scanAnItemAtCheckout("Chicken Soup");
        checkoutMarkdownCurrency.scanAnItemAtCheckout("Chicken Soup");
        checkoutMarkdownCurrency.scanAnItemAtCheckout("Chicken Soup");

        assertEquals(2.5, checkoutMarkdownCurrency.getTotal());
    }


    @Test
    public void validateUnitTotalsWithMarkdownDiscountIncludeLimitCurrency() {
        Checkout checkoutMarkdownLimitCurrency = Checkout.getInstance();
        checkoutMarkdownLimitCurrency.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkoutMarkdownLimitCurrency.createDiscount("Tomato Soup", "Tomato Soup Markdown", .50, 0, "Markdown", "Currency", 1);
        checkoutMarkdownLimitCurrency.scanAnItemAtCheckout("Tomato Soup");
        checkoutMarkdownLimitCurrency.scanAnItemAtCheckout("Tomato Soup");

        assertEquals(2.5, checkoutMarkdownLimitCurrency.getTotal());
    }

    @Test
    public void validateUnitTotalsWithMarkdownDiscountPercentage() {
        Checkout checkoutMarkdownPercentage = Checkout.getInstance();
        checkoutMarkdownPercentage.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkoutMarkdownPercentage.createDiscount("Tomato Soup", "Tomato Soup Markdown", 50, 0, "Markdown", "Percentage");

        checkoutMarkdownPercentage.scanAnItemAtCheckout("Tomato Soup");
        checkoutMarkdownPercentage.scanAnItemAtCheckout("Tomato Soup");

        assertEquals(1.50, checkoutMarkdownPercentage.getTotal());
    }

    @Test
    public void validateUnitTotalsWithMarkdownDiscountPercentageWithLimit() {
        Checkout checkoutMarkdownPercentageLimit = Checkout.getInstance();
        checkoutMarkdownPercentageLimit.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkoutMarkdownPercentageLimit.createDiscount("Tomato Soup", "Tomato Soup Markdown", 50, 0, "Markdown", "Percentage", 1);

        checkoutMarkdownPercentageLimit.scanAnItemAtCheckout("Tomato Soup");
        checkoutMarkdownPercentageLimit.scanAnItemAtCheckout("Tomato Soup");

        assertEquals(2.25, checkoutMarkdownPercentageLimit.getTotal());
    }


//    @Test
//    public void validateWeightTotalsWithMarkdownDiscountCurrency() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
//        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", .50, 0, "Markdown", "Currency");
//
//        checkout.scanAnItemAtCheckout("Ground Beef", 2);
//
//        assertEquals(3.00, checkout.getTotal());
//    }
//
//    @Test
//    public void validateWeightTotalsWithMarkdownDiscountIncludeLimitCurrency() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
//        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", .50, 0, "Markdown", "Currency", 1);
//
//        checkout.scanAnItemAtCheckout("Ground Beef", 2);
//        checkout.scanAnItemAtCheckout("Ground Beef", 2);
//
//        assertEquals(7.00, checkout.getTotal());
//    }
//
//    @Test
//    public void validateWeightTotalsWithMarkdownDiscountPercentage() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Ground Beef", "Weighted", 2.00);
//        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 25, 0, "Markdown", "Percentage");
//
//        checkout.scanAnItemAtCheckout("Ground Beef", 2);
//
//        assertEquals(3.00, checkout.getTotal());
//    }
//
//    @Test
//    public void validateWeightTotalsWithMarkdownDiscountIncludeLimitPercentage() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Ground Beef", "Weighted", 2.00);
//        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 25, 0, "Markdown", "Percentage", 1);
//
//        checkout.scanAnItemAtCheckout("Ground Beef", 2);
//        checkout.scanAnItemAtCheckout("Ground Beef", 2);
//
//        assertEquals(7.00, checkout.getTotal());
//    }
//
//    @Test
//    public void validateUnitTotalsWithBxgyDiscountCurrency() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
//        checkout.createDiscount("Tomato Soup", "Tomato Soup Bxgy", 1.50, 1, "BXGY", "Currency");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//
//        assertEquals(3.00, checkout.getTotal());
//    }
//
//    @Test
//    public void validateUnitTotalsWithBxgyDiscountAndLimitCurrency() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
//        checkout.createDiscount("Tomato Soup", "Tomato Soup Bxgy", 1.50, 1, "BXGY", "Currency", 1);
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//
//        assertEquals(4.50, checkout.getTotal());
//    }
//
//    @Test
//    public void validateUnitTotalsWithBxgyDiscountPercentage() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
//        checkout.createDiscount("Tomato Soup", "Tomato Soup Bxgy", 100, 1, "BXGY", "Percentage");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//
//        assertEquals(1.50, checkout.getTotal());
//    }
//
//    @Test
//    public void validateUnitTotalsWithBxgyDiscountAndLimitPercentage() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
//        checkout.createDiscount("Tomato Soup", "Tomato Soup Bxgy", 100, 1, "BXGY", "Percentage", 1);
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//
//        assertEquals(4.50, checkout.getTotal());
//    }
//
//    @Test
//    public void validateWeightTotalWithBxgyDiscountCurrency() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
//        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 100, 1, "BXGY", "Currency");
//
//        checkout.scanAnItemAtCheckout("Ground Beef", 2);
//        assertEquals(2.00, checkout.getTotal());
//    }
//
//    @Test
//    public void validateWeightTotalWithBxgyDiscountAndLimitCurrency() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
//        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 2, 1, "BXGY", "Currency", 1);
//
//        checkout.scanAnItemAtCheckout("Ground Beef", 2);
//        checkout.scanAnItemAtCheckout("Ground Beef", 2);
//        assertEquals(6.00, checkout.getTotal());
//    }
//
//    @Test
//    public void validateWeightTotalWithBxgyDiscountPercentage() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
//        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 50, 1, "BXGY", "Percentage");
//
//        checkout.scanAnItemAtCheckout("Ground Beef", 6);
//        assertEquals(9.00, checkout.getTotal());
//    }
//
//    @Test
//    public void validateWeightTotalWithBxgyDiscountAndLimitPercentage() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
//        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 50, 1, "BXGY", "Percentage", 1);
//
//        checkout.scanAnItemAtCheckout("Ground Beef", 4);
//        assertEquals(6.00, checkout.getTotal());
//    }
//
//    @Test
//    public void validateUnitTotalWithXforYDiscountCurrency() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
//        checkout.createDiscount("Tomato Soup", "Tomato Soup BXFY", 5, 4, "XForY", "Currency");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//
//        assertEquals(5.00, checkout.getTotal());
//    }
//
//    @Test
//    public void validateUnitTotalWithXforYDiscountAndLimitCurrency() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
//        checkout.createDiscount("Tomato Soup", "Tomato Soup BXFY", 5, 4, "XForY", "Currency", 1);
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//
//
//        assertEquals(11.00, checkout.getTotal());
//    }
//
//    @Test
//    public void validateUnitTotalWithXforYDiscountPercentage() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
//        checkout.createDiscount("Tomato Soup", "Tomato Soup BXFY", 50, 2, "XForY", "Percentage");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//
//
//        assertEquals(1.50, checkout.getTotal());
//    }
//
//    @Test
//    public void validateUnitTotalWithXforYDiscountAndLimitPercentage() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
//        checkout.createDiscount("Tomato Soup", "Tomato Soup BXFY", 50, 2, "XForY", "Percentage", 1);
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//        checkout.scanAnItemAtCheckout("Tomato Soup");
//
//
//        assertEquals(4.50, checkout.getTotal());
//    }
//
//
//    @Test
//    public void validateWeightTotalWithXforYDiscountCurrency() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
//        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 3.00, 2, "XForY", "Currency");
//
//        checkout.scanAnItemAtCheckout("Ground Beef", 2.00);
//        checkout.scanAnItemAtCheckout("Ground Beef", 2.25);
//
//        assertEquals(6.50, checkout.getTotal());
//    }
//
//    @Test
//    public void validateWeightTotalWithXforYDiscountAndLimitCurrency() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
//        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 3.00, 2, "XForY", "Currency", 2);
//
//        checkout.scanAnItemAtCheckout("Ground Beef", 2.00);
//        checkout.scanAnItemAtCheckout("Ground Beef", 2.20);
//
//        assertEquals(6.40, checkout.getTotal());
//    }
//
//    @Test
//    public void validateWeightTotalWithXforYDiscountPercentage() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
//        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 50, 2, "XForY", "Percentage");
//
//        checkout.scanAnItemAtCheckout("Ground Beef", 2.00);
//
//
//        assertEquals(2.00, checkout.getTotal());
//    }
//
//    @Test
//    public void validateWeightTotalWithXforYDiscountAndLimitPercentage() {
//        Checkout checkout = Checkout.getInstance();
//        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
//        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 50, 2, "XForY", "Percentage", 1);
//
//        checkout.scanAnItemAtCheckout("Ground Beef", 4.00);
//
//        assertEquals(6.00, checkout.getTotal());
//    }
}

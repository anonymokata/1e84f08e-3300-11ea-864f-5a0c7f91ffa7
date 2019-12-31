import Entities.Discount;
import Entities.Product;
import Services.CheckoutService;
import Services.DiscountService;
import Services.InventoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(JUnit4.class)
public class CartProductTests {

    InventoryService inventoryService;
    CheckoutService checkoutService;
    DiscountService discountService;

    @Before
    public void setUp() {
        inventoryService = new InventoryService();
        checkoutService = new CheckoutService();
        discountService = new DiscountService();
    }
    @Test
    public void validateThatProductsBeingScannedAreAddedToCheckoutCart() {
        HashMap<String, List<Product>> cartAtCheckout = new HashMap<String, List<Product>>();

        Product productA = new Product("Tomato Soup", Product.PricingMethod.Unit, 99);
        Product productB = new Product("Tomato Soup", Product.PricingMethod.Unit, 99);
        Product productC = new Product("Chicken Soup", Product.PricingMethod.Unit, 99);
        Product productD = new Product("Chicken Soup", Product.PricingMethod.Unit, 99);

        List<Product> listA = new ArrayList<Product>();
        List<Product> listB = new ArrayList<Product>();
        listA.add(productA);
        listA.add(productB);

        listB.add(productC);
        listB.add(productD);

        cartAtCheckout.put(productA.getProductId(), listA);
        cartAtCheckout.put(productC.getProductId(), listB);

        Product tomatoSoup = inventoryService.createInventoryProduct("Tomato Soup", "Unit", 99);
        Product chickenSoup = inventoryService.createInventoryProduct("Chicken Soup", "Unit", 99);
        checkoutService.scanItem(tomatoSoup);
        checkoutService.scanItem(tomatoSoup);
        checkoutService.scanItem(chickenSoup);

        assertEquals(cartAtCheckout, checkoutService.scanItem(chickenSoup));
    }

    @Test
    public void validateCorrectTotalProducedFromCartWithoutDiscount() {
        Product groundBeef = new Product("Ground Beef", Product.PricingMethod.Weighted, 100);
        Product chickenSoup = new Product("Chicken Soup", Product.PricingMethod.Unit, 100);
        Product potatoSoup = new Product("Potato Soup", Product.PricingMethod.Unit, 100);

        checkoutService.scanItem(groundBeef, 1.0);
        checkoutService.scanItem(chickenSoup);
        checkoutService.scanItem(potatoSoup);

        assertEquals(300, checkoutService.calculateTotal());

    }

    @Test
    public void validateItemRemovalFromCartAndCalculateTotal() {
        Product groundBeef = new Product("Ground Beef", Product.PricingMethod.Weighted, 100);
        Product chickenSoup = new Product("Chicken Soup", Product.PricingMethod.Unit, 100);
        Product potatoSoup = new Product("Potato Soup", Product.PricingMethod.Unit, 100);

        checkoutService.scanItem(groundBeef, 1.0);
        checkoutService.scanItem(chickenSoup);
        checkoutService.scanItem(potatoSoup);

        assertEquals(200, checkoutService.deleteItemFromCart("Ground Beef", 1));

    }

    @Test
    public void validateThatDiscountsAreAppliedAtCheckout() {
        Product groundBeef = new Product("Ground Beef", Product.PricingMethod.Weighted, 100);
        Product chickenSoup = new Product("Chicken Soup", Product.PricingMethod.Unit, 100);
        Product potatoSoup = new Product("Potato Soup", Product.PricingMethod.Unit, 100);
        Product tomatoSoup = new Product("Tomato Soup", Product.PricingMethod.Unit, 100);

        Discount discount = discountService.createDiscount("Tomato Soup", "markdownTomatoSoup", 50, 0, "BXGY", "Percentage");
        Discount discount2 = discountService.createDiscount("Chicken Soup", "markdownChickenSoup", 20, 0, "Markdown", "Percentage");
        Discount discount3 = discountService.createDiscount("Ground Beef", "MForNGroundBeef", 100, 200, "XForY", "Percentage");
        HashMap<String, List<Discount>> allDiscounts = discountService.returnAllDiscounts();

        checkoutService.scanItem(groundBeef, 6.0);
        checkoutService.scanItem(chickenSoup);
        checkoutService.scanItem(tomatoSoup);
        checkoutService.scanItem(potatoSoup);


        assertEquals(630, checkoutService.calculateTotal());
    }
}

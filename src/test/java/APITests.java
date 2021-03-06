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

    Checkout checkout;

    @Before
    public void setUp() {
        checkout = new Checkout();
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
    public void validateUnitTotalsWithMarkdownDiscountPercentage() {
        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createDiscount("Tomato Soup", "Tomato Soup Markdown", 50, 0, "Markdown", "Percentage");

        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");

        assertEquals(1.50, checkout.getTotal());
    }

    @Test
    public void validateUnitTotalsWithMarkdownDiscountPercentageWithLimit() {
        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createDiscount("Tomato Soup", "Tomato Soup Markdown", 50, 0, "Markdown", "Percentage", 1);

        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");

        assertEquals(2.25, checkout.getTotal());
    }


    @Test
    public void validateWeightTotalsWithMarkdownDiscountCurrency() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", .50, 0, "Markdown", "Currency");

        checkout.scanAnItemAtCheckout("Ground Beef", 2);

        assertEquals(3.50, checkout.getTotal());
    }

    @Test
    public void validateWeightTotalsWithMarkdownDiscountIncludeLimitCurrency() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", .50, 0, "Markdown", "Currency", 1);

        checkout.scanAnItemAtCheckout("Ground Beef", 2);
        checkout.scanAnItemAtCheckout("Ground Beef", 2);

        assertEquals(7.50, checkout.getTotal());
    }

    @Test
    public void validateWeightTotalsWithMarkdownDiscountPercentage() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2.00);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 25, 0, "Markdown", "Percentage");

        checkout.scanAnItemAtCheckout("Ground Beef", 2);

        assertEquals(3.50, checkout.getTotal());
    }

    @Test
    public void validateWeightTotalsWithMarkdownDiscountIncludeLimitPercentage() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2.00);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 25, 0, "Markdown", "Percentage", 1);

        checkout.scanAnItemAtCheckout("Ground Beef", 2);
        checkout.scanAnItemAtCheckout("Ground Beef", 2);

        assertEquals(7.50, checkout.getTotal());
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
    public void validateUnitTotalsWithBxgyDiscountPercentage() {
        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createDiscount("Tomato Soup", "Tomato Soup Bxgy", 100, 1, "BXGY", "Percentage");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");

        assertEquals(1.50, checkout.getTotal());
    }

    @Test
    public void validateUnitTotalsWithBxgyDiscountAndLimitPercentage() {
        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createDiscount("Tomato Soup", "Tomato Soup Bxgy", 100, 1, "BXGY", "Percentage", 1);
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");

        assertEquals(4.50, checkout.getTotal());
    }

    @Test
    public void validateWeightTotalWithBxgyDiscountCurrency() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 100, 1, "BXGY", "Currency");

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

    @Test
    public void validateWeightTotalWithBxgyDiscountPercentage() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 50, 1, "BXGY", "Percentage");

        checkout.scanAnItemAtCheckout("Ground Beef", 6);
        assertEquals(9.00, checkout.getTotal());
    }

    @Test
    public void validateWeightTotalWithBxgyDiscountAndLimitPercentage() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 50, 1, "BXGY", "Percentage", 1);

        checkout.scanAnItemAtCheckout("Ground Beef", 4);
        assertEquals(6.00, checkout.getTotal());
    }

    @Test
    public void validateUnitTotalWithXforYDiscountCurrency() {
        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createDiscount("Tomato Soup", "Tomato Soup BXFY", 5, 4, "XForY", "Currency");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");

        assertEquals(5.00, checkout.getTotal());
    }

    @Test
    public void validateUnitTotalWithXforYDiscountAndLimitCurrency() {
        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createDiscount("Tomato Soup", "Tomato Soup BXFY", 5, 4, "XForY", "Currency", 1);
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");


        assertEquals(11.00, checkout.getTotal());
    }

    @Test
    public void validateUnitTotalWithXforYDiscountPercentage() {
        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createDiscount("Tomato Soup", "Tomato Soup BXFY", 50, 2, "XForY", "Percentage");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");


        assertEquals(1.50, checkout.getTotal());
    }

    @Test
    public void validateUnitTotalWithXforYDiscountAndLimitPercentage() {
        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createDiscount("Tomato Soup", "Tomato Soup BXFY", 50, 2, "XForY", "Percentage", 1);
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Tomato Soup");


        assertEquals(4.50, checkout.getTotal());
    }


    @Test
    public void validateWeightTotalWithXforYDiscountCurrency() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 3.00, 2, "XForY", "Currency");

        checkout.scanAnItemAtCheckout("Ground Beef", 2.00);
        checkout.scanAnItemAtCheckout("Ground Beef", 2.25);

        assertEquals(6.50, checkout.getTotal());
    }

    @Test
    public void validateWeightTotalWithXforYDiscountAndLimitCurrency() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 3.00, 2, "XForY", "Currency", 2);

        checkout.scanAnItemAtCheckout("Ground Beef", 2.00);
        checkout.scanAnItemAtCheckout("Ground Beef", 2.20);

        assertEquals(6.40, checkout.getTotal());
    }

    @Test
    public void validateWeightTotalWithXforYDiscountPercentage() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 50, 2, "XForY", "Percentage");

        checkout.scanAnItemAtCheckout("Ground Beef", 2.00);


        assertEquals(2.00, checkout.getTotal());
    }

    @Test
    public void validateWeightTotalWithXforYDiscountAndLimitPercentage() {
        checkout.createInventoryItem("Ground Beef", "Weighted", 2);
        checkout.createDiscount("Ground Beef", "Ground Beef Markdown", 50, 2, "XForY", "Percentage", 1);

        checkout.scanAnItemAtCheckout("Ground Beef", 4.00);

        assertEquals(6.00, checkout.getTotal());
    }
}

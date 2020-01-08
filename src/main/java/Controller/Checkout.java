package Controller;

import Entities.Discount;
import Entities.Product;
import Services.CheckoutService;
import Services.DiscountService;
import Services.InventoryService;

import java.util.HashMap;
import java.util.List;

public class Checkout {

        /*
        Methods for API:
         - Create an item for the store inventory. Return value should be void or a success response string for item creation.
         - Remove an item from the store inventory. Return value should be void or a success response string for item removal.
         - Create Discounts. Return value should be void or a success response string for Discount creation.
         - Scan Item at Checkout. Return value will be a list of product ids alone with the total for the cart.
         - Delete Item at Checkout. Return value should be void or a success response string for item in the cart removal.
     */


    CheckoutService checkoutService = new CheckoutService(this);

    public HashMap<String, Product> productInventory = new HashMap<String, Product>();
    public HashMap<String, List<Discount>> allActiveDiscounts = new HashMap<String, List<Discount>>();

    InventoryService inventoryService = new InventoryService(checkoutService);
    DiscountService discountService = new DiscountService(checkoutService);

    //Used to create per unit items (Soup can / Cereal /Etc)
    public void createInventoryItem(String productId, String pricingMethod, double costOfProductPerPricingMethod) {
        int costTranslated = (int) (costOfProductPerPricingMethod * 100);
        inventoryService.createInventoryProduct(productId, pricingMethod, costTranslated);
    }

    //Used to create weighted items (Ground Beef / Turkey)
    public void createInventoryItem(String productId, String productPricingMethod, int costOfProductPerPricingMethod, double productWeightIfWeighted) {
        int costTranslated = (int) (costOfProductPerPricingMethod * 100);
        inventoryService.createInventoryProduct(productId, productPricingMethod, costOfProductPerPricingMethod, costTranslated);
    }

    //Used to create discounts without any item limits
    public void createDiscount(String productId, String uniqueDiscountId, double discountValue, int quantityTriggerForDiscount, String typeOfDiscount, String discountDeductionValueType) {
        int valueTranslated = (int) (discountValue * 100);
        discountService.createDiscount(productId, uniqueDiscountId, valueTranslated, quantityTriggerForDiscount, typeOfDiscount, discountDeductionValueType);
    }

    //Used to create discounts with item limits
    public void createDiscount(String productId, String uniqueDiscountId, double discountValue, int quantityTriggerForDiscount, String typeOfDiscount, String discountDeductionValueType, int limitForDiscount) {
        int valueTranslated = (int) (discountValue * 100);
        discountService.createDiscount(productId, uniqueDiscountId, valueTranslated, quantityTriggerForDiscount, typeOfDiscount, discountDeductionValueType, limitForDiscount);
    }

    //Used to scan a Per Unit Item
    public double scanAnItemAtCheckout(String productId) {
        checkoutService.scanItem( checkoutService.setProduct(productId));
        double total =  checkoutService.calculateCurrentTotal();
        return (total / 100);
    }

    //Used to scan a Per Weight Item
    public double scanAnItemAtCheckout(String productId, double itemWeight) {
        checkoutService.scanItem(checkoutService.setProduct(productId), itemWeight);
        double total = checkoutService.calculateCurrentTotal();
        return (total / 100);
    }

    //Used to delete quantities of items
    public void deleteAnItemAtCheckout(String productId, int itemQuantityToBeRemoved) {
        checkoutService.deleteItemFromCart(productId, itemQuantityToBeRemoved);
    }

    //Used to calculate final total. scan item will calculate total without discounts applied, as discounts are applied after final item is scanned.
    public double getTotal() {
       return ((double) checkoutService.calculateCurrentTotalWithDiscounts() / 100);
    }
}

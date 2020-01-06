package Controller;

import Services.CheckoutService;
import Services.DiscountService;
import Services.InventoryService;

public class Checkout {

        /*
        Methods for API:
         - Create an item for the store inventory. Return value should be void or a success response string for item creation.
         - Remove an item from the store inventory. Return value should be void or a success response string for item removal.
         - Create Discounts. Return value should be void or a success response string for Discount creation.
         - Scan Item at Checkout. Return value will be a list of product ids alone with the total for the cart.
         - Delete Item at Checkout. Return value should be void or a success response string for item in the cart removal.
     */

    static InventoryService inventoryService = InventoryService.getInstance();
    static CheckoutService checkoutService = CheckoutService.getInstance();
    static DiscountService discountService = DiscountService.getInstance();

    //Make this class a singleton so that the services aren't instantied at every call.

    public void createInventoryItem(String productId, String pricingMethod, double costOfProductPerPricingMethod) {
        int costTranslated = (int) (costOfProductPerPricingMethod * 100);
        inventoryService.createInventoryProduct(productId, pricingMethod, costTranslated);
    }

    public void createInventoryItem(String productId, String productPricingMethod, int costOfProductPerPricingMethod, double productWeightIfWeighted) {
        int costTranslated = (int) (costOfProductPerPricingMethod * 100);
        inventoryService.createInventoryProduct(productId, productPricingMethod, costOfProductPerPricingMethod, costTranslated);
    }

    public void createDiscount(String productId, String uniqueDiscountId, double discountValue, int quantityTriggerForDiscount, String typeOfDiscount, String discountDeductionValueType) {
        int valueTranslated = (int) (discountValue * 100);
        discountService.createDiscount(productId, uniqueDiscountId, valueTranslated, quantityTriggerForDiscount, typeOfDiscount, discountDeductionValueType);
    }

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

    public void deleteAnItemAtCheckout(String productId, int itemQuantityToBeRemoved) {
        checkoutService.deleteItemFromCart(productId, itemQuantityToBeRemoved);
    }

    public double getTotal() {
       return ((double) checkoutService.calculateCurrentTotalWithDiscounts() / 100);
    }

    //Singleton implementation
    /*************************************************************************************/

    private static Checkout obj;

    private Checkout() {}

    public static synchronized Checkout getInstance() {
        if (obj == null)
            obj = new Checkout();
        return obj;
    }

    /*************************************************************************************/
}

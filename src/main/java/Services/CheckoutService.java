package Services;

import Entities.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static Services.DiscountService.allActiveDiscounts;

public class CheckoutService {
    HashMap<String, List<Product>> checkoutCart = new HashMap<String, List<Product>>();
    DiscountService discountService = new DiscountService();
    InventoryService inventoryService = new InventoryService();
    private int currentTotal;

    public HashMap<String, List<Product>> scanItem(Product scannedProduct) {

        //When an item is scanned, the new item is then added to the cart at
        // the key corresponding to the product id.
        if (checkoutCart.containsKey(scannedProduct.getProductId())) {
            checkoutCart.get(scannedProduct.getProductId()).add(scannedProduct);
        } else {
            List<Product> tempList = new ArrayList<Product>();
            tempList.add(scannedProduct);
            checkoutCart.put(scannedProduct.getProductId(), tempList);
        }

        return checkoutCart;
    }

    public Product setProduct(String productId) {
        return inventoryService.getProductFromInventory(productId);
    }

    //Method overloading to allow a weight parameter to be inputted for weighted items.
    public HashMap<String, List<Product>> scanItem(Product product, double weight) {
       int integerWeight = (int) (weight * 100);
       product.setProductWeightIfWeighted((integerWeight));
       return scanItem(product);
    }

    public int calculateTotal() {

       int costBeforeApplieDiscounts = 0;

       //For every list of products in the cart.
       for (String productId : checkoutCart.keySet()) {
           int costAfterAppliedDiscounts = 0;

           //While working with one product, calculate the cost based on whether that product is
           // priced per unit or per weight.
           /*
               If the product is weight based, then the cost will be the product cost multiplied by the weight of that current product.
            */
           for (int counter = 0; counter < checkoutCart.get(productId).size(); counter++) {
               if (checkoutCart.get(productId).get(counter).getProductPricingMethod() == Product.PricingMethod.Unit) {
                   costBeforeApplieDiscounts += checkoutCart.get(productId).get(counter).getProductCostPerPricingMethod();
               } else {
                   int costOfWeightedProduct = 0;
                   costOfWeightedProduct = checkoutCart.get(productId).get(counter).getProductCostPerPricingMethod() *
                           checkoutCart.get(productId).get(counter).getProductWeightIfWeighted();
                   costBeforeApplieDiscounts += (costOfWeightedProduct / 100);
               }
           }

           //Check for any discounts.
           if (allActiveDiscounts.size() > 0) {
               costAfterAppliedDiscounts = discountService.applyDiscountsToCostOfProducts(checkoutCart.get(productId).get(0), checkoutCart.get(productId).size());
           }

           // If the cost after a discount has been applied is less than the cost before discount, then the cost per item will be with discounts applied. Else, it will be the original price.
           int costPerItem = 0;
           if (costAfterAppliedDiscounts == 0) {
               costPerItem = checkoutCart.get(productId).get(0).getProductCostPerPricingMethod();
           } else {
                costPerItem = costAfterAppliedDiscounts < costBeforeApplieDiscounts ? costAfterAppliedDiscounts : checkoutCart.get(productId).get(0).getProductCostPerPricingMethod();
           }
            currentTotal = currentTotal + (costPerItem);
       }

       return currentTotal;
    }


    // After an item has been deleted, recheck the total and return the value.
    public int deleteItemFromCart(String productId, int quantity) {

       for (int counter = 0; counter < quantity; counter++) {
           if (checkoutCart.containsKey(productId)) {
               if (checkoutCart.size() == 1) {
                   checkoutCart = null;
               } else {
                   //Statically setting index for removal becuase the index will always be zero.
                   if (checkoutCart.get(productId).size() == 1) {
                       checkoutCart.remove(productId);
                   } else {
                       checkoutCart.get(productId).remove(0);
                   }
               }
           }
       }
       return calculateTotal();
    }

}

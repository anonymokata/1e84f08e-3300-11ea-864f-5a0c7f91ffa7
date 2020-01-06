package Services;

import Entities.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckoutService {
    public HashMap<String, List<Product>> checkoutCart = new HashMap<String, List<Product>>();
    DiscountService discountService = DiscountService.getInstance();
    InventoryService inventoryService = InventoryService.getInstance();
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

    public int calculateCurrentTotal() {
        int currentTotal = 0;

        for (String productId : checkoutCart.keySet()) {
            for (int counter = 0; counter < checkoutCart.get(productId).size(); counter++) {
                if (discountService.getRelevantDiscountsForProduct(productId).size() > 0) {
                    checkoutCart.replace(productId, checkoutCart.get(productId), discountService.checkDiscounts(checkoutCart.get(productId)));
                }
                currentTotal += checkoutCart.get(productId).get(counter).getProductCostPerPricingMethod();
            }

        }
        return currentTotal;
    }



    public int calculateTotal() {

       int costBeforeApplieDiscounts = 0;
       int currentTotal = 0;
       //For every list of products in the cart.
       for (String productId : checkoutCart.keySet()) {
           costBeforeApplieDiscounts = 0;
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
                           this.checkoutCart.get(productId).get(counter).getProductWeightIfWeighted();
                   costBeforeApplieDiscounts += (costOfWeightedProduct / 100);
               }


           }

           //Check for any discounts.
           if (discountService.returnAllDiscounts().size() > 0) {
               costAfterAppliedDiscounts = discountService.applyDiscountsToCostOfProducts(checkoutCart.get(productId).get(0), checkoutCart.get(productId).size());
           }

           // If the cost after a discount has been applied is less than the cost before discount, then the cost per item will be with discounts applied. Else, it will be the original price.
           if (checkoutCart.get(productId).get(0).getProductPricingMethod() == Product.PricingMethod.Unit) {
               int costPerItem = 0;
               if (costAfterAppliedDiscounts == 0) {
                   currentTotal += (checkoutCart.get(productId).get(0).getProductCostPerPricingMethod() * checkoutCart.get(productId).size());
               } else {
                   costPerItem = costAfterAppliedDiscounts < costBeforeApplieDiscounts ? costAfterAppliedDiscounts : checkoutCart.get(productId).get(0).getProductCostPerPricingMethod();
                   currentTotal = currentTotal + (costPerItem);
               }


           } else {
               currentTotal = (checkoutCart.get(productId).get(0).getProductCostPerPricingMethod());
               //currentTotal = currentTotal + ((checkoutCart.get(productId).get(0).getProductCostPerPricingMethod() * checkoutCart.get(productId).get(0).getProductWeightIfWeighted() / 1000));
           }
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

    //Singleton implementation
    /*************************************************************************************/
    private static CheckoutService obj;

    private CheckoutService() {}

    public static synchronized CheckoutService getInstance() {
        if (obj == null)
            obj = new CheckoutService();
        return obj;
    }

}

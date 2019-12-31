package Services;

import Entities.Discount;
import Entities.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static Services.DiscountService.allActiveDiscounts;

public class CheckoutService {
    HashMap<String, List<Product>> checkoutCart = new HashMap<String, List<Product>>();
    DiscountService discountService = new DiscountService();
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

        //Method overloading to allow a weight parameter to be inputted for weighted items.
        public HashMap<String, List<Product>> scanItem(Product product, double weight) {
           int integerWeight = (int) (weight * 100);
           product.setProductWeightIfWeighted((integerWeight));
           if (checkoutCart.containsKey(product.getProductId())) {
               checkoutCart.get(product.getProductId()).add(product);
           } else {
               List<Product> tempList = new ArrayList<Product>();
               tempList.add(product);
               checkoutCart.put(product.getProductId(), tempList);
           }
           return checkoutCart;
        }

        public int calculateTotal() {

           int costBeforeApplieDiscounts = 0;
           for (String productId : checkoutCart.keySet()) {
               int costAfterAppliedDiscounts = 0;

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

               if (allActiveDiscounts.size() > 0) {
                   costAfterAppliedDiscounts = discountService.applyDiscountsToCostOfProducts(checkoutCart.get(productId).get(0), checkoutCart.get(productId).size());
               }

               int costPerItem = costAfterAppliedDiscounts < costBeforeApplieDiscounts ? costAfterAppliedDiscounts : checkoutCart.get(productId).get(0).getProductCostPerPricingMethod();
                currentTotal = currentTotal + (costPerItem);
           }

           return currentTotal;
        }



        public int deleteItemFromCart(String productId, int quantity) {

           for (int i = 0; i < quantity; i++) {
               if (checkoutCart.containsKey(productId)) {
                   if (checkoutCart.size() == 1) {
                       checkoutCart = null;

                   } else {
                       //Statically setting index for removal becuase the index will always be zero.
                       checkoutCart.get(productId).remove(0);
                   }
               }
           }
           return calculateTotal();
        }

}

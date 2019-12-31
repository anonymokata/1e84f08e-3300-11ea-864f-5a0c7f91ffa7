package Services;

import Entities.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckoutService {
    HashMap<String, List<Product>> checkoutCart = new HashMap<String, List<Product>>();


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

               for (int i = 0; i < checkoutCart.get(productId).size(); i++) {
                   if (checkoutCart.get(productId).get(i).getProductPricingMethod() == Product.PricingMethod.Unit) {
                       costBeforeApplieDiscounts += checkoutCart.get(productId).get(i).getProductCostPerPricingMethod();
                   } else {
                       int costOfWeightedProduct = 0;
                       costOfWeightedProduct = checkoutCart.get(productId).get(i).getProductCostPerPricingMethod() *
                               checkoutCart.get(productId).get(i).getProductWeightIfWeighted();
                       costBeforeApplieDiscounts += (costOfWeightedProduct / 100);
                   }
               }
           }

           return costBeforeApplieDiscounts;
        }
}

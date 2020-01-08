package Services;

import Controller.Checkout;
import Entities.Discount;
import Entities.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckoutService {


    Checkout checkout;
    InventoryService inventoryService = new InventoryService(this);
    DiscountService discountService = new DiscountService(this);

    public HashMap<String, List<Product>> checkoutCart = new HashMap<String, List<Product>>();

    public HashMap<String, List<Product>> scanItem(Product scannedProduct) {

        //When an item is scanned, the new item is then added to the cart at
        // the key corresponding to the product id.
        Product product = new Product(scannedProduct.getProductId(), scannedProduct.getProductPricingMethod(), scannedProduct.getProductCostPerPricingMethod(), scannedProduct.getProductWeightIfWeighted());
        if (checkoutCart.containsKey(scannedProduct.getProductId())) {
            checkoutCart.get(scannedProduct.getProductId()).add(product);
        } else {
            List<Product> tempList = new ArrayList<>();
            tempList.add(product);
            checkoutCart.put(scannedProduct.getProductId(), tempList);
        }
        return checkoutCart;
    }

    //Assign a new product object the values of a product object in the inventory service hashmap
    public Product setProduct(String productId) {
        Product product = inventoryService.getProductFromInventory(productId);
        return product;
    }

    //Method overloading to allow a weight parameter to be inputted for weighted items.
    public HashMap<String, List<Product>> scanItem(Product product, double weight) {
       int integerWeight = (int) (weight * 100);
       product.setProductWeightIfWeighted((integerWeight));
       return scanItem(product);
    }

    //Calculate total with discounts
    public int calculateCurrentTotalWithDiscounts() {
        int currentTotal = 0;

        for (String productId : checkoutCart.keySet()) {
            if (discountService.getRelevantDiscountsForProduct(productId).size() > 0) {
                checkoutCart.replace(productId, checkoutCart.get(productId), discountService.checkDiscounts(checkoutCart.get(productId)));
            }
            for (int counter = 0; counter < checkoutCart.get(productId).size(); counter++) {
                if (checkoutCart.get(productId).get(counter).getProductPricingMethod() == Product.PricingMethod.Weighted && checkoutCart.get(productId).get(counter).getDiscountsApplied().size() == 0) {
                    currentTotal += ((checkoutCart.get(productId).get(counter).getProductCostPerPricingMethod() * checkoutCart.get(productId).get(counter).getProductWeightIfWeighted()) / 100);
                } else {
                    currentTotal += checkoutCart.get(productId).get(counter).getProductCostPerPricingMethod();
                }
            }

        }
        return currentTotal;
    }

    //Calculate Total without discounts
    public int calculateCurrentTotal() {
        int currentTotal = 0;
        for (String productId : checkoutCart.keySet()) {
            for (int counter = 0; counter < checkoutCart.get(productId).size(); counter++) {
                currentTotal += checkoutCart.get(productId).get(counter).getProductCostPerPricingMethod();
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
       return calculateCurrentTotal();
    }

    public CheckoutService(Checkout checkout) {
        this.checkout = checkout;
    }

    //Getters and Setters are used for IOC/DI purposes to mock state
    /*******************************************************************/

    public void setProductInventory(String productId, Product product) {
        checkout.productInventory.put(productId, product);

    }

    public HashMap<String, Product> getProductInventory() {
        return checkout.productInventory;
    }

    public void setDiscountInventory(String productId, List<Discount> discounts) {
        checkout.allActiveDiscounts.put(productId, discounts);
    }

    public HashMap<String, List<Discount>> getDiscountInventory() {
        return checkout.allActiveDiscounts;
    }
}

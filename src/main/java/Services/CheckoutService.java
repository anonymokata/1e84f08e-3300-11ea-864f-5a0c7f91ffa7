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
}

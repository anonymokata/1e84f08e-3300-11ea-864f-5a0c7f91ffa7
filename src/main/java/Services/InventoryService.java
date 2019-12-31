package Services;

import Entities.Product;

import java.util.HashMap;



public class InventoryService {
    // Created products will be stored in the productInventory HashMap.
    private HashMap<String, Product> productInventory = new HashMap<String, Product>();

    // Using Method overloading for API ease of use.
    //This method is used when the product is priced per unit.
    public Product createInventoryProduct(String productId, String productPricingMethod, int costOfProductPerPricingMethod) {
        Product product = new Product(productId, Product.PricingMethod.valueOf(productPricingMethod), costOfProductPerPricingMethod);
        productInventory.put(product.getProductId(), product);
        return product;
    }


    //This method is used when the product is priced per weight.
    public Product createInventoryProduct(String productId, String productPricingMethod, int costOfProductPerPricingMethod, int productWeightIfWeighted) {
        Product product = new Product(productId, Product.PricingMethod.valueOf(productPricingMethod), costOfProductPerPricingMethod, productWeightIfWeighted);
        productInventory.put(product.getProductId(), product);
        return product;
    }

    public String removeInventoryProduct(String productId) {

        //Accounts for Null Pointer Exceptions during removal of products.
        if (productInventory.containsKey(productId)) {
            if(productInventory.size() > 1 ) {
                productInventory.remove(productId);
            } else {
                productInventory = null;
            }
        }

        return productId + " removed.";
    }

    public Product getProductFromInventory(String productId) {
        return productInventory.get(productId);
    }
}

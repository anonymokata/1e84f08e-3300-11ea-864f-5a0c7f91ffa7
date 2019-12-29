package Services;

import Entities.Product;

import java.util.HashMap;



public class InventoryService {

    private HashMap<String, Product> productInventory = new HashMap<String, Product>();

    // Two createProduct methods will be used, as there are two types of Products that require variations in parameters.
    // One is Per Unit (Can of Soup), and one is per weight (1.00 per pound).
    // Created products will be stored in the productInventory HashMap.
    public Product createInventoryProduct(String productId, String productPricingMethod, int costOfProductPerPricingMethod) {
        Product product = new Product(productId, Product.PricingMethod.valueOf(productPricingMethod), costOfProductPerPricingMethod);
        productInventory.put(product.getProductId(), product);
        return product;
    }

    public Product createInventoryProduct(String productId, String productPricingMethod, int costOfProductPerPricingMethod, int productWeightIfWeighted) {
        Product product = new Product(productId, Product.PricingMethod.valueOf(productPricingMethod), costOfProductPerPricingMethod, productWeightIfWeighted);
        productInventory.put(product.getProductId(), product);
        return product;
    }

    public String removeInventoryProduct(String productId) {
        if (productInventory.containsKey(productId)) {
            if(productInventory.size() > 1 ) {
                productInventory.remove(productId);
            } else {
                productInventory = null;
            }
        }

        return productId + " removed.";
    }
}

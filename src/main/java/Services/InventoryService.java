package Services;

import Entities.Product;

import java.util.HashMap;



public class InventoryService {
    // Created products will be stored in the productInventory HashMap.
    CheckoutService checkoutService;
    private HashMap<String, Product> productInventory;

    // Using Method overloading for API ease of use.
    //This method is used when the product is priced per unit.
    public Product createInventoryProduct(String productId, String productPricingMethod, int costOfProductPerPricingMethod) {
        Product product = new Product(productId, Product.PricingMethod.valueOf(productPricingMethod), costOfProductPerPricingMethod);
        if (checkoutService.getProductInventory() == null ) {
            checkoutService.setProductInventory(productId, product);
            productInventory = checkoutService.getProductInventory();
        } else {
            productInventory = checkoutService.getProductInventory();
            checkoutService.setProductInventory(productId, product);
        }

        return product;
    }


    //This method is used when the product is priced per weight.
    public Product createInventoryProduct(String productId, String productPricingMethod, int costOfProductPerPricingMethod, int productWeightIfWeighted) {
        Product product = new Product(productId, Product.PricingMethod.valueOf(productPricingMethod), costOfProductPerPricingMethod, productWeightIfWeighted);
        checkoutService.setProductInventory(product.getProductId(), product);
        productInventory = checkoutService.getProductInventory();
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

    public Product getProductFromInventory(String productId) {
        return checkoutService.getProductInventory().get(productId);
    }

    public InventoryService(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }
}

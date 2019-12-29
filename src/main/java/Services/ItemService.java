package Services;

import Entities.Product;

import java.util.HashMap;

//Hosts services that will be used for CRUD operations
public class ItemService {

    private HashMap<String, Product> productInventory = new HashMap<String, Product>();

    /*
        Create a product that will be added to the software's inventory list.
        Under production conditions, product would be stored in a table;
        for the Kata, Products will be stored in a HashMap that keeps track of inventory
        for the duration of execution.
        Products have a name ("Soup"), a pricing method (Per Unit or Per Weight),
        and a cost (5.99 (Per Weight), 2.00 (Per Unit)).
     */
    public Product createItem(String productId, String productPricingMethod, int costOfProductPerPricingMethod) {
        Product product = new Product(productId, Product.PricingMethod.valueOf(productPricingMethod), costOfProductPerPricingMethod);
        productInventory.put(product.getProductId(), product);
        return product;
    }

    public Product createItem(String productId, String productPricingMethod, int costOfProductPerPricingMethod, int productWeightIfWeighted) {
        Product product = new Product(productId, Product.PricingMethod.valueOf(productPricingMethod), costOfProductPerPricingMethod, productWeightIfWeighted);
        productInventory.put(product.getProductId(), product);
        return product;
    }
}

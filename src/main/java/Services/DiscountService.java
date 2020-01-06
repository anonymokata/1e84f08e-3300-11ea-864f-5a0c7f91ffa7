package Services;

import Entities.Discount;
import Entities.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiscountService {


    /*
        All types of discounts can be under one umbrella.
        Since there can be multiple types of discounts per one product,
        HashMap will be used to store a list of discounts for each productId
        This map is static so that this class does not need to be instantiated by the checkout service.
     */
    InventoryService inventoryService = InventoryService.getInstance();
    public HashMap<String, List<Discount>> allActiveDiscounts = new HashMap<String, List<Discount>>();

    public Discount createDiscount(String productIdAssociatedToDiscount, String uniqueDiscountName, int valueBasedOnDiscountType, int quantityRequiredTriggerDiscount, String discountType, String valueType) {
        Discount discount = new Discount(productIdAssociatedToDiscount, uniqueDiscountName, valueBasedOnDiscountType, quantityRequiredTriggerDiscount, Discount.DiscountType.valueOf(discountType), Discount.ValueType.valueOf(valueType));

        //Conditional accounts for Null Pointer Exceptions by validating that there is a size greater than zero for the Discounts HashMap.
        //Checks if there are any discounts associated to the product for the current discount being created.
        if (allActiveDiscounts.size() > 0 && allActiveDiscounts.containsKey(productIdAssociatedToDiscount)) {
            allActiveDiscounts.get(discount.getProductIdAssociated()).add(discount);
        } else {
            List<Discount> currentDiscount = new ArrayList<Discount>();
            currentDiscount.add(discount);
            allActiveDiscounts.put(discount.getProductIdAssociated(), currentDiscount);
        }
        return discount;
    }

    public Discount createDiscount(String productIdAssociatedToDiscount, String uniqueDiscountName, int valueBasedOnDiscountType, int quantityRequiredTriggerDiscount, String discountType, String valueType, int limit) {
        Discount discount = new Discount(productIdAssociatedToDiscount, uniqueDiscountName, valueBasedOnDiscountType, quantityRequiredTriggerDiscount, Discount.DiscountType.valueOf(discountType), Discount.ValueType.valueOf(valueType), limit);

        //Conditional accounts for Null Pointer Exceptions by validating that there is a size greater than zero for the Discounts HashMap.
        //Checks if there are any discounts associated to the product for the current discount being created.
        if (allActiveDiscounts.size() > 0 && allActiveDiscounts.containsKey(productIdAssociatedToDiscount)) {
            allActiveDiscounts.get(discount.getProductIdAssociated()).add(discount);
        } else {
            List<Discount> currentDiscount = new ArrayList<Discount>();
            currentDiscount.add(discount);
            allActiveDiscounts.put(discount.getProductIdAssociated(), currentDiscount);
        }
        return discount;
    }

    public String removeDiscount(String productIdAssociatedToDiscount, String uniqueDiscountName) {
        String response = "";

        //Accounts for Null Pointers, and checks if the discount that has been entered for deletion exists.
        //Removes discount or nullifies the HashMap of discounts if the size is 1.
        for (Discount d : allActiveDiscounts.get(productIdAssociatedToDiscount)) {
            if (d.getUniqueDiscountName() == uniqueDiscountName) {
                if (allActiveDiscounts.get(productIdAssociatedToDiscount).size() > 1) {
                    allActiveDiscounts.get(productIdAssociatedToDiscount).remove(d);
                } else {
                    allActiveDiscounts = null;
                }
                response = "Successfully removed " + d.getUniqueDiscountName() + " from the discount list.";
            }
        }
        return response;
    }

    //This is used to add a new discount to the hashmap without having to call the create discount service.
    //Used for testing currently.

    public HashMap<String, List<Discount>> returnAllDiscounts() {
        return allActiveDiscounts;
    }

    /*
    This method will account for the 3 different types of discounts in requirements.
        BXGY, BMForN, Markdown
     */


    //Returns all discounts associated to a specific product.
    public List<Discount> getRelevantDiscountsForProduct(String productId) {
        List<Discount> discounts = new ArrayList<Discount>();
        for (String indexProductId : allActiveDiscounts.keySet()) {
            if (indexProductId == productId) {
                discounts = (allActiveDiscounts.get(indexProductId));
            }
        }
        return discounts;
    }

    public int applyPriceOffQuantityDiscounts(Discount discount) {
        return discount.getValueBasedOnDiscountType() / discount.getQuantityRequiredTriggerDiscount();
    }

    public List<Product> checkDiscounts(List<Product> products) {
        List<Discount> discounts = getRelevantDiscountsForProduct(products.get(0).getProductId());
        List<Product> productsAppended = new ArrayList<Product>();


        for (Discount discount : discounts) {
                if (discount.getDiscountType() == Discount.DiscountType.Markdown) {
                    productsAppended = applyMarkdownCurrencyBased(products, discount);
                } else if (discount.getDiscountType() == Discount.DiscountType.BXGY) {
                    productsAppended = applyBxgyCurrencyBased(products, discount);
                } else if (discount.getDiscountType() == Discount.DiscountType.XForY) {
                    productsAppended = applyXforyCurrencyBased(products, discount);

            }
        }
        return productsAppended;
    }

    public List<Product> applyMarkdownCurrencyBased(List<Product> products, Discount discount) {

        //To avoid setting existent instance objects to new values in the inventory, object is cloned instead of using getters and setters.
        for (int counter = 0; counter < products.size(); counter++) {
            Product cloneProduct = new Product(products.get(counter).getProductId(), products.get(counter).getProductPricingMethod(), products.get(counter).getProductCostPerPricingMethod());
            List<String> discountsApplied = new ArrayList<>();
            discountsApplied.addAll(cloneProduct.getDiscountsApplied());

            if (cloneProduct.getProductPricingMethod() == Product.PricingMethod.Unit) {
                if (!cloneProduct.getDiscountsApplied().contains(discount.getUniqueDiscountName())) {
                    if (discount.getLimitForDiscountApplication() == 0) {
                        if (discount.getValueType() == Discount.ValueType.Currency) {
                            cloneProduct.setProductCostPerPricingMethod(cloneProduct.getProductCostPerPricingMethod() - discount.getValueBasedOnDiscountType());
                        } else {
                            double valueOff = ((((double) discount.getValueBasedOnDiscountType() / 1000) * (double) cloneProduct.getProductCostPerPricingMethod()) / 10);
                            double totalCost = cloneProduct.getProductCostPerPricingMethod() - valueOff;
                            cloneProduct.setProductCostPerPricingMethod((int)totalCost);
                        }
                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                        products.set(counter, cloneProduct);
                    } else if (counter != discount.getLimitForDiscountApplication()) {
                        if (discount.getValueType() == Discount.ValueType.Currency) {
                            cloneProduct.setProductCostPerPricingMethod(cloneProduct.getProductCostPerPricingMethod() - discount.getValueBasedOnDiscountType());
                        } else {
                            double valueOff = ((((double) discount.getValueBasedOnDiscountType() / 1000) * (double) cloneProduct.getProductCostPerPricingMethod()) / 10);
                            double totalCost = cloneProduct.getProductCostPerPricingMethod() - valueOff;
                            cloneProduct.setProductCostPerPricingMethod((int)totalCost);
                        }
                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                        products.set(counter, cloneProduct);
                    }
                }
            } else {

                if (!products.get(counter).getDiscountsApplied().contains(discount.getUniqueDiscountName())) {
                    if (discount.getLimitForDiscountApplication() == 0 || (counter < discount.getLimitForDiscountApplication())) {
                        cloneProduct.setProductWeightIfWeighted(products.get(counter).getProductWeightIfWeighted());
                        int eligibilityForDiscount = (cloneProduct.getProductWeightIfWeighted() / 100);
                        int deductionFromTotalWeightCost = eligibilityForDiscount * discount.getValueBasedOnDiscountType();
                        cloneProduct.setProductCostPerPricingMethod((((products.get(counter).getProductCostPerPricingMethod() * products.get(counter).getProductWeightIfWeighted()) / 100) - deductionFromTotalWeightCost));
                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                        products.set(counter, cloneProduct);
                    } else {
                        products.get(counter).setProductCostPerPricingMethod((products.get(counter).getProductCostPerPricingMethod()) * (products.get(counter).getProductWeightIfWeighted()) / 100);
                    }
                }

            }
        }

        return products;
    }

    public List<Product> applyBxgyCurrencyBased(List<Product> products, Discount discount) {
        int discountApplicationCounter = 0;
        int limiter = 0;

            for (int counter = 0; counter < products.size(); counter++) {
                Product cloneProduct = new Product(products.get(counter).getProductId(), products.get(counter).getProductPricingMethod(), products.get(counter).getProductCostPerPricingMethod(), products.get(counter).getProductWeightIfWeighted());
                List<String> discountsApplied = new ArrayList<>();
                discountsApplied.addAll(cloneProduct.getDiscountsApplied());

                if (cloneProduct.getProductPricingMethod() == Product.PricingMethod.Unit) {
                    if (discountApplicationCounter == discount.getQuantityRequiredTriggerDiscount() && discount.getLimitForDiscountApplication() == 0
                            && !cloneProduct.getDiscountsApplied().contains(discount.getUniqueDiscountName())) {
                        cloneProduct.setProductCostPerPricingMethod(cloneProduct.getProductCostPerPricingMethod() - discount.getValueBasedOnDiscountType());
                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                        products.set(counter, cloneProduct);
                        discountApplicationCounter = 0;

                    } else if (discount.getLimitForDiscountApplication() > 0 && limiter != discount.getLimitForDiscountApplication() && !cloneProduct.getDiscountsApplied().contains(discount.getUniqueDiscountName())) {
                        cloneProduct.setProductCostPerPricingMethod(cloneProduct.getProductCostPerPricingMethod() - discount.getValueBasedOnDiscountType());
                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                        products.set(counter, cloneProduct);
                        discountApplicationCounter = 0;
                        limiter++;
                    } else {
                        discountApplicationCounter++;
                    }
                } else {
                    if (!cloneProduct.getDiscountsApplied().contains(discount.getUniqueDiscountName()) && cloneProduct.getProductWeightIfWeighted() >= discount.getQuantityRequiredTriggerDiscount() && discount.getLimitForDiscountApplication() == 0) {
                        cloneProduct.setProductCostPerPricingMethod((cloneProduct.getProductCostPerPricingMethod() * cloneProduct.getProductWeightIfWeighted()) - (cloneProduct.getProductCostPerPricingMethod()));
                    } else if (!cloneProduct.getDiscountsApplied().contains(discount.getUniqueDiscountName()) && cloneProduct.getProductWeightIfWeighted() >= discount.getQuantityRequiredTriggerDiscount() && discount.getLimitForDiscountApplication() > limiter) {
                        cloneProduct.setProductCostPerPricingMethod(((cloneProduct.getProductCostPerPricingMethod() * cloneProduct.getProductWeightIfWeighted()) / 100) - (cloneProduct.getProductCostPerPricingMethod()));
                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                        products.set(counter, cloneProduct);
                        limiter++;
                    }
                }
            }
        return products;
    }

    public List<Product> applyXforyCurrencyBased(List<Product> products, Discount discount) {
        int discountApplicationCounter = 0;
        for (int counter = 0; counter < products.size(); counter++) {
            Product cloneProduct = new Product(products.get(counter).getProductId(), products.get(counter).getProductPricingMethod(), products.get(counter).getProductCostPerPricingMethod(), products.get(counter).getProductWeightIfWeighted());
            List<String> discountsApplied = new ArrayList<>();
            discountsApplied.addAll(cloneProduct.getDiscountsApplied());
            double adjustedCostPerItem = 0;
            discountsApplied.addAll(cloneProduct.getDiscountsApplied());

            if (!cloneProduct.getDiscountsApplied().contains(discount)) {
                if (cloneProduct.getProductPricingMethod() == Product.PricingMethod.Unit) {
                    if (discount.getLimitForDiscountApplication() == 0) {
                        adjustedCostPerItem = (double) discount.getValueBasedOnDiscountType() / ((double) products.size() * 100);
                        adjustedCostPerItem *= 100;
                        cloneProduct.setProductCostPerPricingMethod((int) adjustedCostPerItem);
                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                        products.set(counter, cloneProduct);
                    } else if (discountApplicationCounter < (discount.getLimitForDiscountApplication() * discount.getQuantityRequiredTriggerDiscount())) {
                        adjustedCostPerItem = (double) discount.getValueBasedOnDiscountType() / ((double) discount.getQuantityRequiredTriggerDiscount() * 100);
                        adjustedCostPerItem *= 100;
                        cloneProduct.setProductCostPerPricingMethod((int) adjustedCostPerItem);
                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                        products.set(counter, cloneProduct);
                        discountApplicationCounter++;
                    }
                } else {
                    if (discount.getLimitForDiscountApplication() == 0 || (discount.getLimitForDiscountApplication() != 0 &&  discountApplicationCounter < discount.getLimitForDiscountApplication())) {
                        int eligibleDiscountApplication = (cloneProduct.getProductWeightIfWeighted() / discount.getQuantityRequiredTriggerDiscount() / 100);
                        if (eligibleDiscountApplication > 0) {
                            int weightRequiredForDiscount = discount.getQuantityRequiredTriggerDiscount() * 100;
                            adjustedCostPerItem = (int) ((double) discount.getValueBasedOnDiscountType() / ((double) eligibleDiscountApplication * (double) weightRequiredForDiscount) * 100);
                            int costOfNonDiscountProduct = ((products.get(counter).getProductWeightIfWeighted() - weightRequiredForDiscount) * (products.get(counter).getProductCostPerPricingMethod() / 100));
                            double totalCost = ((adjustedCostPerItem * weightRequiredForDiscount) / 100) + costOfNonDiscountProduct;
                            cloneProduct.setProductCostPerPricingMethod((int) totalCost);
                            cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                            products.set(counter, cloneProduct);
                            discountApplicationCounter++;
                        }

                    }
                }
            }
        }

        return products;
    }










    //Singleton implementation
    /*************************************************************************************/
    private static DiscountService obj;

    private DiscountService() {}

    public static synchronized DiscountService getInstance() {
        if (obj == null)
            obj = new DiscountService();
        return obj;
    }
}

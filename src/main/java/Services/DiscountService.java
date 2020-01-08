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

    private CheckoutService checkoutService;

    private HashMap<String, List<Discount>> allActiveDiscounts;
    private final int convertDoubleToInt = 100;
    private final int percentageConverter = 10000;

    //Create a discount without a limit
    public Discount createDiscount(String productIdAssociatedToDiscount, String uniqueDiscountName, int valueBasedOnDiscountType, int quantityRequiredTriggerDiscount, String discountType, String valueType) {
        Discount discount = new Discount(productIdAssociatedToDiscount, uniqueDiscountName, valueBasedOnDiscountType, quantityRequiredTriggerDiscount, Discount.DiscountType.valueOf(discountType), Discount.ValueType.valueOf(valueType));
        //Conditional accounts for Null Pointer Exceptions by validating that there is a size greater than zero for the Discounts HashMap.
        //Checks if there are any discounts associated to the product for the current discount being created.
        allActiveDiscounts = checkoutService.getDiscountInventory() != null ? checkoutService.getDiscountInventory() : new HashMap<>();
        discountConstruction(discount);
        return discount;
    }

    //Create a discount with a limit
    public Discount createDiscount(String productIdAssociatedToDiscount, String uniqueDiscountName, int valueBasedOnDiscountType, int quantityRequiredTriggerDiscount, String discountType, String valueType, int limit) {
        Discount discount = new Discount(productIdAssociatedToDiscount, uniqueDiscountName, valueBasedOnDiscountType, quantityRequiredTriggerDiscount, Discount.DiscountType.valueOf(discountType), Discount.ValueType.valueOf(valueType), limit);

        //Conditional accounts for Null Pointer Exceptions by validating that there is a size greater than zero for the Discounts HashMap.
        //Checks if there are any discounts associated to the product for the current discount being created.
        allActiveDiscounts = checkoutService.getDiscountInventory() != null ? checkoutService.getDiscountInventory() : new HashMap<>();
        discountConstruction(discount);
        return discount;
    }

    //Used to avoid code duplication with the method overloading
    //Checks if any discounts are already associated to the current discounts id. If there are, the list of discounts is appended with the new discount.
    public void discountConstruction(Discount discount) {
        allActiveDiscounts = checkoutService.getDiscountInventory() != null ? checkoutService.getDiscountInventory() : new HashMap<>();
        if (allActiveDiscounts.size() > 0 && allActiveDiscounts.containsKey(discount.getProductIdAssociated())) {
            allActiveDiscounts.get(discount.getProductIdAssociated()).add(discount);
        } else {
            List<Discount> currentDiscount = new ArrayList<Discount>();
            currentDiscount.add(discount);
            allActiveDiscounts.put(discount.getProductIdAssociated(), currentDiscount);
            checkoutService.setDiscountInventory(discount.getUniqueDiscountName(), currentDiscount);
        }
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

    //Returns all discounts associated to a specific product.
    public List<Discount> getRelevantDiscountsForProduct(String productId) {
        List<Discount> discounts = new ArrayList<Discount>();
        for (String indexProductId : checkoutService.getDiscountInventory().keySet()) {
            if (indexProductId == productId) {
                discounts = (checkoutService.getDiscountInventory().get(indexProductId));
            }
        }
        return discounts;
    }

    //Takes in a list of products (Same item) and determines which discounts are applicable.
    //Calls appropriate method for each discount type.
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

    //Markdown Discount: a discount where the new total is the difference between the old total and the valueBasedOnDiscountType
    public List<Product> applyMarkdownCurrencyBased(List<Product> products, Discount discount) {
        int discountLimit = 0;
        int weightDiscountLimit = 0;
        //To avoid setting existent instance objects to new values in the inventory, object is cloned instead of using getters and setters.
        // Allows for less getter and setter boilerplate as well.
        for (int counter = 0; counter < products.size(); counter++) {
            Product cloneProduct = new Product(products.get(counter).getProductId(), products.get(counter).getProductPricingMethod(), products.get(counter).getProductCostPerPricingMethod(), products.get(counter).getProductWeightIfWeighted());

            if (!isDiscountApplied(cloneProduct, discount)) {
                if (cloneProduct.getProductPricingMethod() == Product.PricingMethod.Unit && !isLimitExceeded(cloneProduct, discount, discountLimit)) {
                    if (discount.getValueType() == Discount.ValueType.Currency) {
                        cloneProduct.setProductCostPerPricingMethod(cloneProduct.getProductCostPerPricingMethod() - discount.getValueBasedOnDiscountType());
                    } else {
                        cloneProduct.setProductCostPerPricingMethod((int) getPercentageOffValue(cloneProduct.getProductCostPerPricingMethod(), discount.getValueBasedOnDiscountType()));
                    }

                } else if (cloneProduct.getProductPricingMethod() == Product.PricingMethod.Weighted) {
                    if (!isLimitExceeded(cloneProduct, discount, weightDiscountLimit)) {
                        int eligibilityForDiscount = (cloneProduct.getProductWeightIfWeighted() / convertDoubleToInt) / getDiscountLimit(discount);
                        int deductionFromTotalWeightCost = (eligibilityForDiscount * discount.getValueBasedOnDiscountType()) / convertDoubleToInt;
                        int productCostWithoutDiscount = calculateCostWithoutDiscountForWeightedItem(cloneProduct);
                        if (discount.getValueType() == Discount.ValueType.Currency) {
                            cloneProduct.setProductCostPerPricingMethod((cloneProduct.getProductCostPerPricingMethod() + getDeductionOfTotalWeightCost(cloneProduct.getProductWeightIfWeighted(), discount.getValueBasedOnDiscountType(), discount.getLimitForDiscountApplication())) * getDiscountLimit(discount));
                        } else {
                            cloneProduct.setProductCostPerPricingMethod((int) getPercentageOffValue(cloneProduct, deductionFromTotalWeightCost, discount.getLimitForDiscountApplication()));
                        }
                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                        weightDiscountLimit += cloneProduct.getProductWeightIfWeighted();
                    }
                }
                products.set(counter, cloneProduct);

        }discountLimit++;
    }
        return products;
    }

    public List<Product> applyBxgyCurrencyBased(List<Product> products, Discount discount) {
        int discountApplicationCounter = 0;
        int discountLimitCounter = 0;
        int weightDiscountLimit = 0;

        for (int counter = 0; counter < products.size(); counter++) {
            Product cloneProduct = new Product(products.get(counter).getProductId(), products.get(counter).getProductPricingMethod(), products.get(counter).getProductCostPerPricingMethod(), products.get(counter).getProductWeightIfWeighted());
            List<String> discountsApplied = new ArrayList<>();
            discountsApplied.addAll(cloneProduct.getDiscountsApplied());

            if (!isDiscountApplied(cloneProduct, discount) && !isLimitExceeded(cloneProduct, discount, discountLimitCounter)) {
                if (cloneProduct.getProductPricingMethod() == Product.PricingMethod.Unit) {
                    //Discount application counter validates that for unit priced items, that the required amount of items have been met before applying a new price.
                    if (discountApplicationCounter == discount.getQuantityRequiredTriggerDiscount()) {
                        if (discount.getValueType() == Discount.ValueType.Currency) {
                            cloneProduct.setProductCostPerPricingMethod(cloneProduct.getProductCostPerPricingMethod() - discount.getValueBasedOnDiscountType());
                        } else {
                            double percentOff = (double) discount.getValueBasedOnDiscountType() / percentageConverter;
                            int totalToSubtract = (int) (percentOff * (double) cloneProduct.getProductCostPerPricingMethod());
                            cloneProduct.setProductCostPerPricingMethod(cloneProduct.getProductCostPerPricingMethod() - totalToSubtract);
                        }

                        discountApplicationCounter = 0;
                        discountLimitCounter++;

                    } else {
                        discountApplicationCounter++;
                    }

                } else if (cloneProduct.getProductPricingMethod() == Product.PricingMethod.Weighted) {

                    //If an item is Weighted, the BuyXGetY application will look at the entire total weight of the item, and compare with the quantity required attribute of the discount.
                    //If the requirements are met, then the discount is applied ONLY to the portion of the item weight that is eligible.
                    //
                    int discountEligibility = (cloneProduct.getProductWeightIfWeighted() / (discount.getQuantityRequiredTriggerDiscount() * convertDoubleToInt) / 2);
                    if (cloneProduct.getProductWeightIfWeighted() >= discount.getQuantityRequiredTriggerDiscount() &&
                            !isLimitExceeded(cloneProduct, discount, weightDiscountLimit)) {

                        if (discount.getValueType() == Discount.ValueType.Currency) {
                            cloneProduct.setProductCostPerPricingMethod(((calculateCostWithoutDiscountForWeightedItem(cloneProduct))) - (cloneProduct.getProductCostPerPricingMethod()));
                        } else {
                            double percentOff = (double) discount.getValueBasedOnDiscountType() / percentageConverter;
                            double baseValue = ((((cloneProduct.getProductCostPerPricingMethod() * (discount.getQuantityRequiredTriggerDiscount()) * convertDoubleToInt) / convertDoubleToInt) * percentOff));
                            int totalToSubtract = (int) (baseValue * discountEligibility);
                            cloneProduct.setProductCostPerPricingMethod((calculateCostWithoutDiscountForWeightedItem(cloneProduct)) - totalToSubtract);
                        }
                        discountApplicationCounter++;
                    } else {
                        cloneProduct.setProductCostPerPricingMethod((calculateCostWithoutDiscountForWeightedItem(cloneProduct)));
                    }
                    discountApplicationCounter++;
                    discountLimitCounter++;
                    weightDiscountLimit += (discount.getQuantityRequiredTriggerDiscount() * convertDoubleToInt);
                }
                products.set(counter, cloneProduct);
                cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());

            }
        }
        return products;
    }


    public List<Product> applyXforyCurrencyBased(List<Product> products, Discount discount) {
        int discountApplicationCounter = 0;

        for (int counter = 0; counter < products.size(); counter++) {
            Product cloneProduct = new Product(products.get(counter).getProductId(), products.get(counter).getProductPricingMethod(), products.get(counter).getProductCostPerPricingMethod(), products.get(counter).getProductWeightIfWeighted());
            List<String> discountsApplied = new ArrayList<>();
            double adjustedCostPerItem = 0;
            int limit = discount.getLimitForDiscountApplication() * discount.getQuantityRequiredTriggerDiscount();

            discountsApplied.addAll(cloneProduct.getDiscountsApplied());
            discountsApplied.addAll(cloneProduct.getDiscountsApplied());

            if (!cloneProduct.getDiscountsApplied().contains(discount) && (discount.getLimitForDiscountApplication() == 0 || limit > discountApplicationCounter)) {
                if (cloneProduct.getProductPricingMethod() == Product.PricingMethod.Unit) {
                    if (discount.getValueType() == Discount.ValueType.Currency) {
                        adjustedCostPerItem = (double) discount.getValueBasedOnDiscountType() / ((double) discount.getQuantityRequiredTriggerDiscount() * convertDoubleToInt);
                        adjustedCostPerItem *= convertDoubleToInt;
                        cloneProduct.setProductCostPerPricingMethod((int) adjustedCostPerItem);
                    } else {
                        double percentOff = (double) discount.getValueBasedOnDiscountType() / percentageConverter;
                        adjustedCostPerItem = (percentOff * cloneProduct.getProductCostPerPricingMethod());
                        cloneProduct.setProductCostPerPricingMethod(cloneProduct.getProductCostPerPricingMethod() - (int) adjustedCostPerItem);
                    }
                    discountApplicationCounter++;
                } else {
                    int eligibleDiscountApplication = (cloneProduct.getProductWeightIfWeighted() / discount.getQuantityRequiredTriggerDiscount() / convertDoubleToInt);
                    if (eligibleDiscountApplication > 0) {
                        int weightRequiredForDiscount = discount.getQuantityRequiredTriggerDiscount() * convertDoubleToInt;
                        int costOfNonDiscountProduct = ((cloneProduct.getProductWeightIfWeighted() - weightRequiredForDiscount) * (cloneProduct.getProductCostPerPricingMethod() / convertDoubleToInt));
                        if (discount.getValueType() == Discount.ValueType.Currency) {
                            adjustedCostPerItem = (int) ((double) discount.getValueBasedOnDiscountType() / ((double) eligibleDiscountApplication * (double) weightRequiredForDiscount) * convertDoubleToInt);
                            double totalCost = ((adjustedCostPerItem * weightRequiredForDiscount) / convertDoubleToInt) + costOfNonDiscountProduct;
                            cloneProduct.setProductCostPerPricingMethod((int) totalCost);
                        } else {
                            int discountLimit = discount.getLimitForDiscountApplication() == 0 ? convertDoubleToInt : discount.getLimitForDiscountApplication() * convertDoubleToInt;
                            double percentOff = (double) discount.getValueBasedOnDiscountType() / percentageConverter;
                            double baseValue = (((((discountLimit * (discount.getQuantityRequiredTriggerDiscount()) * convertDoubleToInt) / convertDoubleToInt) * percentOff)));
                            int totalToSubtract = (int) (baseValue * discount.getQuantityRequiredTriggerDiscount());
                            cloneProduct.setProductCostPerPricingMethod(((cloneProduct.getProductCostPerPricingMethod() * cloneProduct.getProductWeightIfWeighted()) / convertDoubleToInt) - totalToSubtract);
                        }
                    }
                }
                cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                products.set(counter, cloneProduct);
            }
        }
        return products;
    }

    /*
    Helper methods for the above methods to reduce repeated code.
     */

    /********************************************************************************************************/
    private boolean isDiscountApplied(Product product, Discount discount) {
        return product.getDiscountsApplied().contains(discount);
    }

    private boolean isLimitExceeded(Product product, Discount discount, int discountLimit) {
        boolean isLimitExceeded = true;
        int limit = discount.getLimitForDiscountApplication() * convertDoubleToInt;
        int productWeight = product.getProductWeightIfWeighted();
            if (product.getProductPricingMethod() == Product.PricingMethod.Unit) {
                if (discount.getLimitForDiscountApplication() == 0 || (discount.getLimitForDiscountApplication() != 0 && discountLimit < discount.getLimitForDiscountApplication())) {
                    isLimitExceeded = false;
                }
            } else {
                if (discount.getLimitForDiscountApplication() == 0) {
                    isLimitExceeded = false;
                } else if (discountLimit < limit) {
                    isLimitExceeded = false;
                } else if (discountLimit >= limit) {
                    isLimitExceeded = true;
                }
            }

        return isLimitExceeded;
    }

    private int calculateCostWithoutDiscountForWeightedItem(Product product) {
        int result = ((product.getProductCostPerPricingMethod() * product.getProductWeightIfWeighted()) / 100);
        return result;
    }

    public double getPercentageOffValue(int totalCost, int percentOff) {
        percentOff = percentOff / convertDoubleToInt;
        return totalCost - ((totalCost * percentOff) / convertDoubleToInt);
    }

    public double getPercentageOffValue(Product product, int percentOff, int limit) {
        int totalCost = calculateCostWithoutDiscountForWeightedItem(product);
        limit = limit == 0 ? 1 : limit;
        double percentConversion = (double) percentOff / (double) convertDoubleToInt;
        double result = totalCost - (((limit * convertDoubleToInt) * percentConversion));
        return result;
    }

    public int getDeductionOfTotalWeightCost(int totalWeight, int discountValue, int limit) {
        limit = limit == 0 ? 1 : limit;
        int result = limit != 0 ? (totalWeight - (discountValue * limit)) : (totalWeight - (discountValue));
        return result;
    }

    public int getDiscountLimit(Discount discount) {
        return discount.getLimitForDiscountApplication() == 0 ? 1 : discount.getLimitForDiscountApplication();
    }

    /*********************************************************************************************************/

    //Constructor for IOC/DI design pattern
    public DiscountService(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }
}

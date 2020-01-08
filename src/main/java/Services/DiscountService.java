package Services;

import Controller.Checkout;
import Entities.Discount;
import Entities.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    CheckoutService checkoutService;

    public HashMap<String, List<Discount>> allActiveDiscounts;

    public Discount createDiscount(String productIdAssociatedToDiscount, String uniqueDiscountName, int valueBasedOnDiscountType, int quantityRequiredTriggerDiscount, String discountType, String valueType) {
        Discount discount = new Discount(productIdAssociatedToDiscount, uniqueDiscountName, valueBasedOnDiscountType, quantityRequiredTriggerDiscount, Discount.DiscountType.valueOf(discountType), Discount.ValueType.valueOf(valueType));
        //Conditional accounts for Null Pointer Exceptions by validating that there is a size greater than zero for the Discounts HashMap.
        //Checks if there are any discounts associated to the product for the current discount being created.
        allActiveDiscounts = checkoutService.getDiscountInventory() != null ? checkoutService.getDiscountInventory() : new HashMap<>();
        if (allActiveDiscounts.size() > 0 && allActiveDiscounts.containsKey(productIdAssociatedToDiscount)) {
            allActiveDiscounts.get(discount.getProductIdAssociated()).add(discount);
        } else {
            List<Discount> currentDiscount = new ArrayList<Discount>();
            currentDiscount.add(discount);
            allActiveDiscounts.put(discount.getProductIdAssociated(), currentDiscount);
            checkoutService.setDiscountInventory(discount.getUniqueDiscountName(), currentDiscount);
        }
        return discount;
    }

    public Discount createDiscount(String productIdAssociatedToDiscount, String uniqueDiscountName, int valueBasedOnDiscountType, int quantityRequiredTriggerDiscount, String discountType, String valueType, int limit) {
        Discount discount = new Discount(productIdAssociatedToDiscount, uniqueDiscountName, valueBasedOnDiscountType, quantityRequiredTriggerDiscount, Discount.DiscountType.valueOf(discountType), Discount.ValueType.valueOf(valueType), limit);

        //Conditional accounts for Null Pointer Exceptions by validating that there is a size greater than zero for the Discounts HashMap.
        //Checks if there are any discounts associated to the product for the current discount being created.
        allActiveDiscounts = checkoutService.getDiscountInventory() != null ? checkoutService.getDiscountInventory() : new HashMap<>();
        if (allActiveDiscounts.size() > 0 && allActiveDiscounts.containsKey(productIdAssociatedToDiscount)) {
            allActiveDiscounts.get(discount.getProductIdAssociated()).add(discount);
        } else {
            List<Discount> currentDiscount = new ArrayList<Discount>();
            currentDiscount.add(discount);
            allActiveDiscounts.put(discount.getProductIdAssociated(), currentDiscount);
            checkoutService.setDiscountInventory(discount.getUniqueDiscountName(), currentDiscount);
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
        for (String indexProductId : checkoutService.getDiscountInventory().keySet()) {
            if (indexProductId == productId) {
                discounts = (checkoutService.getDiscountInventory().get(indexProductId));
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
        int discountApplicationCounter = 0;
        //To avoid setting existent instance objects to new values in the inventory, object is cloned instead of using getters and setters.
        for (int counter = 0; counter < products.size(); counter++) {
            Product cloneProduct = new Product(products.get(counter).getProductId(), products.get(counter).getProductPricingMethod(), products.get(counter).getProductCostPerPricingMethod());
            List<String> discountsApplied = new ArrayList<>();
            discountsApplied.addAll(cloneProduct.getDiscountsApplied());

            if (cloneProduct.getProductPricingMethod() == Product.PricingMethod.Unit) {
                if (!cloneProduct.getDiscountsApplied().contains(discount.getUniqueDiscountName())) {
                    if (discount.getLimitForDiscountApplication() == 0 || (discount.getLimitForDiscountApplication() != 0 &&  discountApplicationCounter < discount.getLimitForDiscountApplication())) {
                        if (discount.getValueType() == Discount.ValueType.Currency) {
                            cloneProduct.setProductCostPerPricingMethod(cloneProduct.getProductCostPerPricingMethod() - discount.getValueBasedOnDiscountType());
                        } else {
                            cloneProduct.setProductCostPerPricingMethod((int) getPercentageOffValue(cloneProduct.getProductCostPerPricingMethod(), discount.getValueBasedOnDiscountType()));
                        }
                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                        products.set(counter, cloneProduct);
                        discountApplicationCounter++;
                    }
                }
            } else if (cloneProduct.getProductPricingMethod() == Product.PricingMethod.Weighted){

                if (!products.get(counter).getDiscountsApplied().contains(discount.getUniqueDiscountName())) {


                    if (discount.getLimitForDiscountApplication() == 0 || (discount.getLimitForDiscountApplication() != 0 &&  discountApplicationCounter < discount.getLimitForDiscountApplication())) {
                        cloneProduct.setProductWeightIfWeighted(products.get(counter).getProductWeightIfWeighted());
                        int eligibilityForDiscount = (cloneProduct.getProductWeightIfWeighted() / 100);
                        int deductionFromTotalWeightCost = eligibilityForDiscount * discount.getValueBasedOnDiscountType();

                        if (discount.getValueType() == Discount.ValueType.Currency) {
                            cloneProduct.setProductCostPerPricingMethod((((products.get(counter).getProductCostPerPricingMethod() * products.get(counter).getProductWeightIfWeighted()) / 100) - deductionFromTotalWeightCost));
                        } else {
                            cloneProduct.setProductCostPerPricingMethod((((int) getPercentageOffValue(cloneProduct.getProductCostPerPricingMethod(),
                                    cloneProduct.getProductWeightIfWeighted(), deductionFromTotalWeightCost) / eligibilityForDiscount) * cloneProduct.getProductWeightIfWeighted()) / 100);
                        }


                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                        products.set(counter, cloneProduct);
                        discountApplicationCounter++;
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
                    if (discountApplicationCounter == discount.getQuantityRequiredTriggerDiscount() && (discount.getLimitForDiscountApplication() == 0 || limiter < discount.getLimitForDiscountApplication())
                            && !cloneProduct.getDiscountsApplied().contains(discount.getUniqueDiscountName())) {
                        if (discount.getValueType() == Discount.ValueType.Currency) {
                            cloneProduct.setProductCostPerPricingMethod(cloneProduct.getProductCostPerPricingMethod() - discount.getValueBasedOnDiscountType());
                        } else {
                            double percentOff = (double) discount.getValueBasedOnDiscountType() / 10000;
                            int totalToSubtract = (int) (percentOff * (double) cloneProduct.getProductCostPerPricingMethod());
                            cloneProduct.setProductCostPerPricingMethod(cloneProduct.getProductCostPerPricingMethod() - totalToSubtract);
                        }
                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                        products.set(counter, cloneProduct);
                        discountApplicationCounter = 0;
                        limiter++;

//                    } else if (discount.getLimitForDiscountApplication() > 0 && limiter != discount.getLimitForDiscountApplication() && !cloneProduct.getDiscountsApplied().contains(discount.getUniqueDiscountName())) {
//                        cloneProduct.setProductCostPerPricingMethod(cloneProduct.getProductCostPerPricingMethod() - discount.getValueBasedOnDiscountType());
//                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
//                        products.set(counter, cloneProduct);
//                        discountApplicationCounter = 0;
//                        limiter++;
//                    }
                    } else {
                        discountApplicationCounter++;
                    }
                } else if (cloneProduct.getProductPricingMethod() == Product.PricingMethod.Weighted) {
                    //Get the total weight of all products in the list. Then apply discount accordingly.
//
//                    int runningTotalOfWeight = getWeightOfAllProductsAssociatedWithProductId(products);
//                    int perPriceMethodCost = cloneProduct.getProductCostPerPricingMethod();
//
//                    if (runningTotalOfWeight >= discount.getQuantityRequiredTriggerDiscount()) {
//                        int discountEligibility = runningTotalOfWeight / discount.getQuantityRequiredTriggerDiscount();
//
//                    }
//

                    if (!cloneProduct.getDiscountsApplied().contains(discount.getUniqueDiscountName()) &&
                            cloneProduct.getProductWeightIfWeighted() >= discount.getQuantityRequiredTriggerDiscount() &&
                            (discount.getLimitForDiscountApplication() == 0  ||
                                    (discount.getLimitForDiscountApplication() != 0 &&
                                            cloneProduct.getProductWeightIfWeighted() < (discount.getLimitForDiscountApplication() * 100)))) {
                        int discountEligibility = (cloneProduct.getProductWeightIfWeighted() / (discount.getQuantityRequiredTriggerDiscount() * 100) / 2);
                        if (discount.getValueType() == Discount.ValueType.Currency) {
                            cloneProduct.setProductCostPerPricingMethod(((cloneProduct.getProductCostPerPricingMethod() * cloneProduct.getProductWeightIfWeighted()) / 100) - (cloneProduct.getProductCostPerPricingMethod()));
                        } else {
                            double percentOff = (double) discount.getValueBasedOnDiscountType() / 10000;
                            double baseValue = ((((cloneProduct.getProductCostPerPricingMethod() * (discount.getQuantityRequiredTriggerDiscount()) * 100) / 100) * percentOff));
                            int totalToSubtract = (int) (baseValue * discountEligibility);
                            cloneProduct.setProductCostPerPricingMethod(((cloneProduct.getProductCostPerPricingMethod() * cloneProduct.getProductWeightIfWeighted()) / 100) - totalToSubtract);
                        }
                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                        products.set(counter, cloneProduct);
                        discountApplicationCounter++;
                    } else if (!cloneProduct.getDiscountsApplied().contains(discount.getUniqueDiscountName()) && cloneProduct.getProductWeightIfWeighted() >= discount.getQuantityRequiredTriggerDiscount() && discount.getLimitForDiscountApplication() > discountApplicationCounter) {
                        cloneProduct.setProductCostPerPricingMethod(((cloneProduct.getProductCostPerPricingMethod() * cloneProduct.getProductWeightIfWeighted()) / 100) - (cloneProduct.getProductCostPerPricingMethod()));
                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                        products.set(counter, cloneProduct);
                        limiter++;
                        discountApplicationCounter++;
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
            int limit = discount.getLimitForDiscountApplication() * discount.getQuantityRequiredTriggerDiscount();
            if (!cloneProduct.getDiscountsApplied().contains(discount) && (discount.getLimitForDiscountApplication() == 0 || limit > discountApplicationCounter)) {
                if (cloneProduct.getProductPricingMethod() == Product.PricingMethod.Unit) {

                        if (discount.getValueType() == Discount.ValueType.Currency) {
                            adjustedCostPerItem = (double) discount.getValueBasedOnDiscountType() / ((double) discount.getQuantityRequiredTriggerDiscount() * 100);
                            adjustedCostPerItem *= 100;
                            cloneProduct.setProductCostPerPricingMethod((int) adjustedCostPerItem);
                        } else {
                            double percentOff = (double) discount.getValueBasedOnDiscountType() / 10000;
                            adjustedCostPerItem = (percentOff * cloneProduct.getProductCostPerPricingMethod());
                            cloneProduct.setProductCostPerPricingMethod(cloneProduct.getProductCostPerPricingMethod() - (int) adjustedCostPerItem);
                        }
                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                        products.set(counter, cloneProduct);
                        discountApplicationCounter++;

//                    } else if (discountApplicationCounter < (discount.getLimitForDiscountApplication() * discount.getQuantityRequiredTriggerDiscount())) {
//                        adjustedCostPerItem = (double) discount.getValueBasedOnDiscountType() / ((double) discount.getQuantityRequiredTriggerDiscount() * 100);
//                        adjustedCostPerItem *= 100;
//                        cloneProduct.setProductCostPerPricingMethod((int) adjustedCostPerItem);
//                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
//                        products.set(counter, cloneProduct);
//                        discountApplicationCounter++;
//                    }
                } else {
//                    if (discount.getLimitForDiscountApplication() == 0 || (discount.getLimitForDiscountApplication() != 0 &&  discountApplicationCounter < discount.getLimitForDiscountApplication())) {

                        int eligibleDiscountApplication = (cloneProduct.getProductWeightIfWeighted() / discount.getQuantityRequiredTriggerDiscount() / 100);
                        if (eligibleDiscountApplication > 0) {
                            int weightRequiredForDiscount = discount.getQuantityRequiredTriggerDiscount() * 100;
                            int costOfNonDiscountProduct = ((cloneProduct.getProductWeightIfWeighted() - weightRequiredForDiscount) * (cloneProduct.getProductCostPerPricingMethod() / 100));
                            if (discount.getValueType() == Discount.ValueType.Currency) {
                                adjustedCostPerItem = (int) ((double) discount.getValueBasedOnDiscountType() / ((double) eligibleDiscountApplication * (double) weightRequiredForDiscount) * 100);
                                double totalCost = ((adjustedCostPerItem * weightRequiredForDiscount) / 100) + costOfNonDiscountProduct;
                                cloneProduct.setProductCostPerPricingMethod((int) totalCost);
                            } else {
                                int discountLimit = discount.getLimitForDiscountApplication() == 0 ? 100 : discount.getLimitForDiscountApplication() * 100;
                                double percentOff = (double) discount.getValueBasedOnDiscountType() / 10000;
                                double baseValue = (((((discountLimit * (discount.getQuantityRequiredTriggerDiscount()) * 100) / 100) * percentOff)));
                                int totalToSubtract = (int) (baseValue * discount.getQuantityRequiredTriggerDiscount());
                                cloneProduct.setProductCostPerPricingMethod(((cloneProduct.getProductCostPerPricingMethod() * cloneProduct.getProductWeightIfWeighted()) / 100) - totalToSubtract);
                            }
                            cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                            products.set(counter, cloneProduct);



                    }
                }
            }
        }

        return products;
    }

    public double getPercentageOffValue(int totalCost, int percentOff) {
        percentOff = percentOff / 100;
        return totalCost - ((totalCost * percentOff) / 100);
    }

    public double getPercentageOffValue(int pricePerMethod, int productWeight, int percentOff) {
        int totalCost = (pricePerMethod * productWeight) / 100;
        percentOff = percentOff / 100;
        return totalCost - ((productWeight * percentOff) / 100);
    }

    public int getWeightOfAllProductsAssociatedWithProductId(List<Product> products) {
        int runningWeightTotal = 0;
        for (int counter = 0; counter < products.size(); counter++) {
            runningWeightTotal += products.get(counter).getProductWeightIfWeighted();
        }

        return runningWeightTotal;
    }

    public DiscountService(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }


    //Singleton implementation
    /*************************************************************************************/
//    private static DiscountService obj;
//
//    private DiscountService() {}
//
//    public static synchronized DiscountService getInstance() {
//        if (obj == null)
//            obj = new DiscountService();
//        return obj;
//    }
}

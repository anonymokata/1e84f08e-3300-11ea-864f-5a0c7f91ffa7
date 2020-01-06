package Services;

import Entities.Discount;
import Entities.Product;
import Services.InventoryService;

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
    public void addExistingDiscountToDiscountInventory(Discount discount) {
        if (allActiveDiscounts.containsKey(discount.getProductIdAssociated())) {
            allActiveDiscounts.get(discount.getProductIdAssociated()).add(discount);
        } else {
            List<Discount> tempList = new ArrayList<Discount>();
            tempList.add(discount);
            allActiveDiscounts.put(discount.getProductIdAssociated(), tempList);
        }
    }
    public HashMap<String, List<Discount>> returnAllDiscounts() {
        return allActiveDiscounts;
    }

    /*
    This method will account for the 3 different types of discounts in requirements.
        BXGY, BMForN, Markdown
     */
    public int applyDiscountsToCostOfProducts(Product product, int quantity) {

        List<Discount> discounts = getRelevantDiscountsForProduct(product.getProductId());
        int runningTotalForProduct = 0;

            for (int counter = 0; counter < quantity; counter++) {

                //Set the cost of the current product
                int costOfCurrentItem = product.getProductPricingMethod() == Product.PricingMethod.Unit ? product.getProductCostPerPricingMethod() : product.getProductCostPerPricingMethod() * product.getProductWeightIfWeighted();

                for (Discount discount : discounts) {
                    //Check if the limit set in the current discount is greater than the quantity. If it is not, set the quantity to the limit.
                    int limit = quantity < discount.getLimitForDiscountApplication() ? quantity : discount.getLimitForDiscountApplication();
                    quantity = limit == 0 ? quantity : limit;

                    //Account for discount of weighted items.
                    if (product.getProductPricingMethod() == Product.PricingMethod.Weighted) {
                            runningTotalForProduct = applyDiscountForWeightedItems(product, discount);
                        }

                        //If the discount type is Bulk (BMForN), the total price is the cost defined in the discount / the number of products required for the discount.
                        if (discount.getDiscountType() == Discount.DiscountType.BXGY || discount.getDiscountType() == Discount.DiscountType.Markdown) {
                            costOfCurrentItem = applyValueOffDiscounts(product, discount, costOfCurrentItem, counter);
                            //This checks to see if the discount is a markdown. The trigger will be zero for markdowns, as no quantities are required.

                        } else {
                            costOfCurrentItem = 0;
                        }


                }
            //If the discount application has reduced the price below zero, then bring the price back to zero.
            if (costOfCurrentItem < 0) {
                costOfCurrentItem = 0;
            }

            //If discount application has reduced the price below zero, then bring price back to zero.
            if (runningTotalForProduct < 0) {
                runningTotalForProduct = 0;
            }

            runningTotalForProduct += costOfCurrentItem;

        }

            return runningTotalForProduct;
    }

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

    public int applyDiscountForWeightedItems(Product product, Discount discount) {

        int quantityRequired = discount.getQuantityRequiredTriggerDiscount() * 100;
        int costPerWeight = product.getProductCostPerPricingMethod();
        int totalWeight = product.getProductWeightIfWeighted();
        int discountApplicationEligibility;
        if (discount.getDiscountType() == Discount.DiscountType.BXGY) {
            discountApplicationEligibility = quantityRequired > 0 ? ((totalWeight / 100) / ((quantityRequired / 100)) / 2) : 0 ;
        } else {
            discountApplicationEligibility = quantityRequired > 0 ? (totalWeight / 100) / ((quantityRequired / 100)) : 0;
        }

        if (product.getProductWeightIfWeighted() >= quantityRequired) {
            //If the value type is currency, then the cost after a discount will be the Weight multiplied by the cost per weight subtracted from the discount value.
            //Else, the cost will be weight multiplied by the cost per weight subtracted by the cost per weight times the percent off.
            if (discount.getValueType() == Discount.ValueType.Currency) {
                product.setProductCostPerPricingMethod(((product.getProductWeightIfWeighted() * costPerWeight) / 100) - (discount.getValueBasedOnDiscountType() * discountApplicationEligibility));
            } else {
                int weight = product.getProductWeightIfWeighted();
                double originalCost = product.getProductCostPerPricingMethod();
                double deduction = originalCost * ((double) discount.getValueBasedOnDiscountType() / 100);
                double productCost;
                if (quantityRequired == 0) {
                    productCost = ((weight * originalCost) / 100) - ((deduction * weight) / 100);
                } else {
                    productCost = ((weight * originalCost) / 100) - (deduction * discountApplicationEligibility);
                }
                product.setProductCostPerPricingMethod((int) productCost);
            }
        }
        return product.getProductCostPerPricingMethod();
    }

    public int applyValueOffDiscounts(Product product, Discount discount, int costOfCurrentItem, int counter) {
        if (discount.getQuantityRequiredTriggerDiscount() == 0) {
            //Covers currency based totals and percentage based totals.
            if (discount.getValueType() == Discount.ValueType.Currency) {
                costOfCurrentItem -= discount.getValueBasedOnDiscountType();
            } else {
                double tempCost = 0;
                tempCost = (product.getProductCostPerPricingMethod() * (double) discount.getValueBasedOnDiscountType() / 100);
                costOfCurrentItem -= tempCost;
                System.out.println(costOfCurrentItem);
            }

            //If the trigger is equal to the counter, then the criteria has been met for the BXGY discounts.
            //Covers percentage and currency based totals.
        } else if (discount.getQuantityRequiredTriggerDiscount() == counter) {
            if (discount.getValueType() == Discount.ValueType.Currency) {
                costOfCurrentItem -= discount.getValueBasedOnDiscountType();
            } else {
                double tempCost = 0;
                tempCost = (product.getProductCostPerPricingMethod() * (double) discount.getValueBasedOnDiscountType() / 100);
                costOfCurrentItem -= (int) tempCost;
                System.out.println(costOfCurrentItem);
            }

        } else if (discount.getValueType() == Discount.ValueType.Percentage) {
            double tempCost = 0;
            tempCost = (product.getProductCostPerPricingMethod() * (double) discount.getValueBasedOnDiscountType() / 100);
            costOfCurrentItem = (int) tempCost;
            System.out.println(costOfCurrentItem);
        }

        return costOfCurrentItem;
    }

    public int applyPriceOffQuantityDiscounts(Discount discount) {
        return discount.getValueBasedOnDiscountType() / discount.getQuantityRequiredTriggerDiscount();
    }

    public List<Product> checkDiscounts(List<Product> products) {
        List<Discount> discounts = getRelevantDiscountsForProduct(products.get(0).getProductId());
        List<Product> productsAppended = new ArrayList<Product>();


        for (Discount discount : discounts) {
            if (discount.getDiscountType() == Discount.DiscountType.Markdown) {
                productsAppended = applyMarkdown(products, discount);
            } else if(discount.getDiscountType() == Discount.DiscountType.BXGY) {
                productsAppended = applyBxgy(products, discount);
            } else if (discount.getDiscountType() == Discount.DiscountType.XForY) {
                productsAppended = applyXfory(products, discount);
            }
        }
        return productsAppended;
    }

    public List<Product> applyMarkdown(List<Product> products, Discount discount) {

        //To avoid setting existent instance objects to new values in the inventory, object is cloned instead of using getters and setters.
        for (int counter = 0; counter < products.size(); counter++) {
            Product cloneProduct = new Product(products.get(counter).getProductId(), products.get(counter).getProductPricingMethod(), products.get(counter).getProductCostPerPricingMethod());
            List<String> discountsApplied = new ArrayList<>();
            discountsApplied.addAll(cloneProduct.getDiscountsApplied());
            if (cloneProduct.getProductPricingMethod() == Product.PricingMethod.Unit) {
                if (!cloneProduct.getDiscountsApplied().contains(discount.getUniqueDiscountName())) {
                    if (discount.getLimitForDiscountApplication() == 0) {
                        cloneProduct.setProductCostPerPricingMethod(cloneProduct.getProductCostPerPricingMethod() - discount.getValueBasedOnDiscountType());
                        cloneProduct.setDiscountsApplied(discount.getUniqueDiscountName());
                        products.set(counter, cloneProduct);
                    } else if (counter != discount.getLimitForDiscountApplication()) {
                        cloneProduct.setProductCostPerPricingMethod(cloneProduct.getProductCostPerPricingMethod() - discount.getValueBasedOnDiscountType());
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

    public List<Product> applyBxgy(List<Product> products, Discount discount) {
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

    public List<Product> applyXfory(List<Product> products, Discount discount) {
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

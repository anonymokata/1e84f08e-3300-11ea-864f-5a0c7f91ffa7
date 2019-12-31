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
     */
    public static HashMap<String, List<Discount>> allActiveDiscounts = new HashMap<String, List<Discount>>();

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


    //This method will account for 3 different types of discounts in requirements.
    public int applyDiscountsToCostOfProducts(Product product, int quantity) {

        List<Discount> discounts = getRelevantDiscountsForProduct(product.getProductId());
        int runningTotalForProduct = 0;


            for (int counter = 0; counter < quantity; counter++) {
                int costOfCurrentItem = product.getProductCostPerPricingMethod();
                for (Discount discount : discounts) {
                    if (discount.getDiscountType() == Discount.DiscountType.BXGY || discount.getDiscountType() == Discount.DiscountType.Markdown) {
                        if (discount.getQuantityRequiredTriggerDiscount() == 0) {
                            if (discount.getValueType() == Discount.ValueType.Currency) {
                                costOfCurrentItem -= discount.getValueBasedOnDiscountType();
                            } else {
                                double tempCost = 0;
                                tempCost = (product.getProductCostPerPricingMethod() * (double) discount.getValueBasedOnDiscountType() / 100);
                                costOfCurrentItem -= tempCost;
                                System.out.println(costOfCurrentItem);
                            }

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

                    } else {
                        costOfCurrentItem = discount.getValueOfBulkItems() / discount.getQuantityRequiredTriggerDiscount();
                    }

                }


            if (costOfCurrentItem < 0) {
                costOfCurrentItem = 0;
            }

            if (runningTotalForProduct < 0) {
                runningTotalForProduct = 0;
            }
            runningTotalForProduct += costOfCurrentItem;

        }

            return runningTotalForProduct;
    }

    public List<Discount> getRelevantDiscountsForProduct(String productId) {
        List<Discount> discounts = new ArrayList<Discount>();

        for (String indexProductId : allActiveDiscounts.keySet()) {
            if (indexProductId == productId) {
                discounts = (allActiveDiscounts.get(indexProductId));
            }
        }

        return discounts;
    }
}

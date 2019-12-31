package Services;

import Entities.Discount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiscountService {


    /*
        All types of discounts can be under one umbrella.
        Since there can be multiple types of discounts per one product,
        HashMap will be used to store a list of discounts for each productId
     */
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
}

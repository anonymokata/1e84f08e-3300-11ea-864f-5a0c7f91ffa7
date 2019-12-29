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
    public Discount createDiscount(String productIdAssociatedToDiscount, int valueBasedOnDiscountType, int quantityRequiredTriggerDiscount, String discountType, String valueType) {
        Discount discount = new Discount(productIdAssociatedToDiscount, valueBasedOnDiscountType, quantityRequiredTriggerDiscount, Discount.DiscountType.valueOf(discountType), Discount.ValueType.valueOf(valueType));

        if (allActiveDiscounts.size() > 0 && allActiveDiscounts.containsKey(productIdAssociatedToDiscount)) {
            allActiveDiscounts.get(discount.getProductIdAssociated()).add(discount);
        } else {
            List<Discount> currentDiscount = new ArrayList<Discount>();
            currentDiscount.add(discount);
            allActiveDiscounts.put(discount.getProductIdAssociated(), currentDiscount);
        }
        return discount;
    }
}

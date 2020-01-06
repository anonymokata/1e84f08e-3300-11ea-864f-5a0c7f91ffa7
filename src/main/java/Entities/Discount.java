package Entities;

import org.apache.commons.lang.builder.EqualsBuilder;

public class Discount {
    String productIdAssociated;
    String uniqueDiscountName;
    int valueBasedOnDiscountType;
    int quantityRequiredTriggerDiscount;
    int valueOfBulkItems;
    int limitForDiscountApplication;
    DiscountType discountType;
    ValueType valueType;
    int amountOffOfBxgy;

    public int getAmountOffOfBxgy() {
        return amountOffOfBxgy;
    }

    public void setAmountOffOfBxgy(int amountOffOfBxgy) {
        this.amountOffOfBxgy = amountOffOfBxgy;
    }

    public Discount(String productIdAssociated, String uniqueDiscountName, int valueBasedOnDiscountType, int quantityRequiredTriggerDiscount, DiscountType discountType, ValueType valueType) {
        this.productIdAssociated = productIdAssociated;
        this.uniqueDiscountName = uniqueDiscountName;
        this.valueBasedOnDiscountType = valueBasedOnDiscountType;
        this.quantityRequiredTriggerDiscount = quantityRequiredTriggerDiscount;
        this.discountType = discountType;
        this.valueType = valueType;
    }

    //Method overloading to incorporate limits on discounts.
    public Discount(String productIdAssociated,String uniqueDiscountName, int valueBasedOnDiscountType, int quantityRequiredTriggerDiscount, DiscountType discountType, ValueType valueType, int limitForDiscountApplication) {
        this.productIdAssociated = productIdAssociated;
        this.uniqueDiscountName = uniqueDiscountName;
        this.valueBasedOnDiscountType = valueBasedOnDiscountType;
        this.quantityRequiredTriggerDiscount = quantityRequiredTriggerDiscount;
        this.discountType = discountType;
        this.valueType = valueType;
        this.limitForDiscountApplication = limitForDiscountApplication;
    }

    public String getProductIdAssociated() {
        return productIdAssociated;
    }

    public void setProductIdAssociated(String productIdAssociated) {
        this.productIdAssociated = productIdAssociated;
    }

    public String getUniqueDiscountName() {
        return uniqueDiscountName;
    }

    public void setUniqueDiscountName(String uniqueDiscountName) {
        this.uniqueDiscountName = uniqueDiscountName;
    }

    public int getValueBasedOnDiscountType() {
        return valueBasedOnDiscountType;
    }

    public void setValueBasedOnDiscountType(int valueBasedOnDiscountType) {
        this.valueBasedOnDiscountType = valueBasedOnDiscountType;
    }

    public int getQuantityRequiredTriggerDiscount() {
        return quantityRequiredTriggerDiscount;
    }

    public void setQuantityRequiredTriggerDiscount(int quantityRequiredTriggerDiscount) {
        this.quantityRequiredTriggerDiscount = quantityRequiredTriggerDiscount;
    }

    public int getValueOfBulkItems() {
        return valueOfBulkItems;
    }

    public void setValueOfBulkItems(int valueOfBulkItems) {
        this.valueOfBulkItems = valueOfBulkItems;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public int getLimitForDiscountApplication() {
        return limitForDiscountApplication;
    }

    public void setLimitForDiscountApplication(int limitForDiscountApplication) {
        this.limitForDiscountApplication = limitForDiscountApplication;
    }

    public enum DiscountType {Markdown, BXGY, XForY}
    public enum ValueType{Currency, Percentage}

    //To compare different instances of objects during testing, the equals method must
    //be overridden

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Discount discount = (Discount) o;

        return new EqualsBuilder()
                .append(productIdAssociated, discount.productIdAssociated)
                .isEquals();
    }
}

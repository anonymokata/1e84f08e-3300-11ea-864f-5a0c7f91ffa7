package Entities;

import org.apache.commons.lang.builder.EqualsBuilder;

public class Discount {
    String productIdAssociated;
    int valueBasedOnDiscountType;
    int quantityRequiredTriggerDiscount;
    int valueOfBulkItems;
    DiscountType discountType;
    ValueType valueType;

    public Discount(String productIdAssociated, int valueBasedOnDiscountType, int quantityRequiredTriggerDiscount, DiscountType discountType, ValueType valueType) {
        this.productIdAssociated = productIdAssociated;
        this.valueBasedOnDiscountType = valueBasedOnDiscountType;
        this.quantityRequiredTriggerDiscount = quantityRequiredTriggerDiscount;
        this.discountType = discountType;
        this.valueType = valueType;
    }

    public String getProductIdAssociated() {
        return productIdAssociated;
    }

    public void setProductIdAssociated(String productIdAssociated) {
        this.productIdAssociated = productIdAssociated;
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
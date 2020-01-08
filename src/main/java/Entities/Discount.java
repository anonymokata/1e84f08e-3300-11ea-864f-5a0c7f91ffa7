package Entities;

import org.apache.commons.lang.builder.EqualsBuilder;

public class Discount {

    private String productIdAssociated;
    private String uniqueDiscountName;
    private int valueBasedOnDiscountType;
    private int quantityRequiredTriggerDiscount;
    private int limitForDiscountApplication;
    private DiscountType discountType;
    private ValueType valueType;

    //Discount without limit constructor
    public Discount(String productIdAssociated, String uniqueDiscountName, int valueBasedOnDiscountType, int quantityRequiredTriggerDiscount, DiscountType discountType, ValueType valueType) {
        this.productIdAssociated = productIdAssociated;
        this.uniqueDiscountName = uniqueDiscountName;
        this.valueBasedOnDiscountType = valueBasedOnDiscountType;
        this.quantityRequiredTriggerDiscount = quantityRequiredTriggerDiscount;
        this.discountType = discountType;
        this.valueType = valueType;
    }

    //Discount with limit constructor
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

    public String getUniqueDiscountName() {
        return uniqueDiscountName;
    }

    public int getValueBasedOnDiscountType() {
        return valueBasedOnDiscountType;
    }

    public int getQuantityRequiredTriggerDiscount() {
        return quantityRequiredTriggerDiscount;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public int getLimitForDiscountApplication() {
        return limitForDiscountApplication;
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

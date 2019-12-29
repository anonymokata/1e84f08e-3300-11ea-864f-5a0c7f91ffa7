package Entities;

import org.apache.commons.lang.builder.EqualsBuilder;

public class Discount {
    String productIdAssociated;

    public String getProductIdAssociated() {
        return productIdAssociated;
    }

    public void setProductIdAssociated(String productIdAssociated) {
        this.productIdAssociated = productIdAssociated;
    }

    public Discount() {
    }

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

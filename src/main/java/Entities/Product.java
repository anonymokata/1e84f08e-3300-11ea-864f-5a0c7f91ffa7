package Entities;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private String productId;
    private PricingMethod productPricingMethod;
    private int productCostPerPricingMethod;
    private int productWeightIfWeighted;
    private List<String> discountsApplied = new ArrayList<>();



    /*
                    There will be two constructors.
                    First Constructor will be used to assign properties of a product that
                    is not priced based on weight.
                 */
    public Product(String productId, PricingMethod productPricingMethod, int productCostPerPricingMethod) {
        this.productId = productId;
        this.productPricingMethod = productPricingMethod;
        this.productCostPerPricingMethod = productCostPerPricingMethod;
    }

    public Product(String productId, PricingMethod productPricingMethod, int productCostPerPricingMethod, int productWeightIfWeighted) {
        this.productId = productId;
        this.productPricingMethod = productPricingMethod;
        this.productCostPerPricingMethod = productCostPerPricingMethod;
        this.productWeightIfWeighted = productWeightIfWeighted;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public PricingMethod getProductPricingMethod() {
        return productPricingMethod;
    }

    public void setProductPricingMethod(PricingMethod productPricingMethod) {
        this.productPricingMethod = productPricingMethod;
    }

    public int getProductCostPerPricingMethod() {
        return productCostPerPricingMethod;
    }

    public void setProductCostPerPricingMethod(int productCostPerPricingMethod) {
        this.productCostPerPricingMethod = productCostPerPricingMethod;
    }

    public int getProductWeightIfWeighted() {
        return productWeightIfWeighted;
    }

    public void setProductWeightIfWeighted(int productWeightIfWeighted) {
        this.productWeightIfWeighted = productWeightIfWeighted;
    }

    public void setDiscountsApplied(String discountsApplied) {
        this.discountsApplied.add(discountsApplied);
    }

    public List<String> getDiscountsApplied() {
        return discountsApplied;
    }

    public enum PricingMethod{Unit, Weighted}

    //To compare different instances of objects during testing, the equals method must
    //be overridden

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;

        if(obj == null || getClass() != obj.getClass()) return false;

        Product product = (Product) obj;

        return new EqualsBuilder()
                .append(productId, product.productId)
                .isEquals();
    }

    //To compare different hashMaps, the hashcode method must be overridden.
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(productId)
                .toHashCode();
    }
}

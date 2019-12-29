package Entities;

import org.apache.commons.lang.builder.EqualsBuilder;

public class Product {
    private String productId;
    private PricingMethod productPricingMethod;
    private int productCost;
    private int productWeightIfWeighted;


    /*
        There will be two constructors.
        First Constructor will be used to assign properties of a product that
        is not priced based on weight.
     */
    public Product(String productId, PricingMethod productPricingMethod, int productCost) {
        this.productId = productId;
        this.productPricingMethod = productPricingMethod;
        this.productCost = productCost;
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

    public int getProductCost() {
        return productCost;
    }

    public void setProductCost(int productCost) {
        this.productCost = productCost;
    }

    public int getProductWeightIfWeighted() {
        return productWeightIfWeighted;
    }

    public void setProductWeightIfWeighted(int productWeightIfWeighted) {
        this.productWeightIfWeighted = productWeightIfWeighted;
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
}

package in.nmaloth.identifierValidator.model.entity.product;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;

public class ProductAuthCriteriaKey {
    @BsonProperty("org")
    private int org;
    @BsonProperty("product")
    private int product;
    @BsonProperty("criteria")
    private int criteria;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof ProductAuthCriteriaKey)) {
            return false;
        } else {
            ProductAuthCriteriaKey that = (ProductAuthCriteriaKey)o;
            return this.getOrg() == that.getOrg() && this.getProduct() == that.getProduct() && this.getCriteria() == that.getCriteria();
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getOrg(), this.getProduct(), this.getCriteria()});
    }

    public static ProductAuthCriteriaKey.ProductAuthCriteriaKeyBuilder builder() {
        return new ProductAuthCriteriaKey.ProductAuthCriteriaKeyBuilder();
    }

    public int getOrg() {
        return this.org;
    }

    public int getProduct() {
        return this.product;
    }

    public int getCriteria() {
        return this.criteria;
    }

    public void setOrg(final int org) {
        this.org = org;
    }

    public void setProduct(final int product) {
        this.product = product;
    }

    public void setCriteria(final int criteria) {
        this.criteria = criteria;
    }

    public ProductAuthCriteriaKey() {
    }

    public ProductAuthCriteriaKey(final int org, final int product, final int criteria) {
        this.org = org;
        this.product = product;
        this.criteria = criteria;
    }

    public static class ProductAuthCriteriaKeyBuilder {
        private int org;
        private int product;
        private int criteria;

        ProductAuthCriteriaKeyBuilder() {
        }

        public ProductAuthCriteriaKey.ProductAuthCriteriaKeyBuilder org(final int org) {
            this.org = org;
            return this;
        }

        public ProductAuthCriteriaKey.ProductAuthCriteriaKeyBuilder product(final int product) {
            this.product = product;
            return this;
        }

        public ProductAuthCriteriaKey.ProductAuthCriteriaKeyBuilder criteria(final int criteria) {
            this.criteria = criteria;
            return this;
        }

        public ProductAuthCriteriaKey build() {
            return new ProductAuthCriteriaKey(this.org, this.product, this.criteria);
        }

        public String toString() {
            return "ProductAuthCriteriaKey.ProductAuthCriteriaKeyBuilder(org=" + this.org + ", product=" + this.product + ", criteria=" + this.criteria + ")";
        }
    }
}

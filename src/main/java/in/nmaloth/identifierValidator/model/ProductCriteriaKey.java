package in.nmaloth.identifierValidator.model;

import java.util.Objects;

public class ProductCriteriaKey {

    private int org;
    private int product;
    private int criteria;

    public ProductCriteriaKey(int org, int product, int criteria) {
        this.org = org;
        this.product = product;
        this.criteria = criteria;
    }

    public ProductCriteriaKey() {
    }

    public int getOrg() {
        return org;
    }

    public void setOrg(int org) {
        this.org = org;
    }

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public int getCriteria() {
        return criteria;
    }

    public void setCriteria(int criteria) {
        this.criteria = criteria;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductCriteriaKey that = (ProductCriteriaKey) o;
        return getOrg() == that.getOrg() && getProduct() == that.getProduct() && getCriteria() == that.getCriteria();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrg(), getProduct(), getCriteria());
    }

    public static ProductCriteriaKeyBuilder builder(){
        return new ProductCriteriaKeyBuilder();
    }

    public static class ProductCriteriaKeyBuilder {

        private int org;
        private int product;
        private int criteria;

        public ProductCriteriaKeyBuilder org(int org){
            this.org = org;
            return this;
        }

        public ProductCriteriaKeyBuilder product(int product){
            this.product = product;
            return this;
        }

        public ProductCriteriaKeyBuilder criteria(int criteria){
            this.criteria = criteria;
            return this;
        }

        public ProductCriteriaKey build(){
            return new ProductCriteriaKey(this.org,this.product,this.criteria);
        }

    }
}

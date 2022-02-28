package in.nmaloth.identifierValidator.model.entity.customer;

import in.nmaloth.payments.constants.customer.AddressType;
import in.nmaloth.payments.constants.customer.CustomerType;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.Optional;

@MongoEntity(collection = "customer")
public class CustomerDef extends ReactivePanacheMongoEntity {

    public ObjectId id;
    @BsonProperty("customer_id")
    private String customerId;
    @BsonProperty("address_type")
    private String addressType;
    @BsonProperty("customer_type")
    private String customerType;
    @BsonProperty("customer_name")
    private String customerName;
    @BsonProperty("address_line")
    private String addressLine;
    @BsonProperty("postal_code")
    private String postalCode;
    @BsonProperty("state")
    private String state;
    @BsonProperty("country_code")
    private String countryCode;
    @BsonProperty("primary_phone_number")
    private String primaryPhoneNumber;
    @BsonProperty("primary_email")
    private String primaryEmail;

    public static CustomerDef.CustomerDefBuilder builder() {
        return new CustomerDef.CustomerDefBuilder();
    }

    public static Uni<Optional<CustomerDef>> findByCustomerId(String customerId) {
        return find("customer_id", customerId).firstResultOptional();
    }


    public String getCustomerId() {
        return this.customerId;
    }

    public String getAddressType() {
        return this.addressType;
    }

    public String getCustomerType() {
        return this.customerType;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public String getAddressLine() {
        return this.addressLine;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public String getState() {
        return this.state;
    }

    public String getCountryCode() {
        return this.countryCode;
    }


    public String getPrimaryPhoneNumber() {
        return this.primaryPhoneNumber;
    }

    public String getPrimaryEmail() {
        return this.primaryEmail;
    }

    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
    }

    public void setAddressType(final String addressType) {
        this.addressType = addressType;
    }

    public void setCustomerType(final String customerType) {
        this.customerType = customerType;
    }

    public void setCustomerName(final String customerName) {
        this.customerName = customerName;
    }

    public void setAddressLine(final String addressLine) {
        this.addressLine = addressLine;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }


    public void setPrimaryPhoneNumber(final String primaryPhoneNumber) {
        this.primaryPhoneNumber = primaryPhoneNumber;
    }

    public void setPrimaryEmail(final String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public CustomerDef() {
    }

    public CustomerDef(final String customerId, final String addressType, final String customerType, final String customerName, final String addressLine, final String postalCode, final String state, final String countryCode, final String primaryPhoneNumber, final String primaryEmail) {
        this.customerId = customerId;
        this.addressType = addressType;
        this.customerType = customerType;
        this.customerName = customerName;
        this.addressLine = addressLine;
        this.postalCode = postalCode;
        this.state = state;
        this.countryCode = countryCode;
        this.primaryPhoneNumber = primaryPhoneNumber;
        this.primaryEmail = primaryEmail;
    }

    public static class CustomerDefBuilder {
        private String customerId;
        private AddressType addressType;
        private CustomerType customerType;
        private String customerName;
        private String addressLine;
        private String postalCode;
        private String state;
        private String countryCode;
        private String primaryPhoneNumber;
        private String primaryEmail;

        CustomerDefBuilder() {
        }

        public CustomerDef.CustomerDefBuilder customerId(final String customerId) {
            this.customerId = customerId;
            return this;
        }

        public CustomerDef.CustomerDefBuilder addressType(final AddressType addressType) {
            this.addressType = addressType;
            return this;
        }

        public CustomerDef.CustomerDefBuilder customerType(final CustomerType customerType) {
            this.customerType = customerType;
            return this;
        }

        public CustomerDef.CustomerDefBuilder customerName(final String customerName) {
            this.customerName = customerName;
            return this;
        }

        public CustomerDef.CustomerDefBuilder addressLine(final String addressLine) {
            this.addressLine = addressLine;
            return this;
        }

        public CustomerDef.CustomerDefBuilder postalCode(final String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public CustomerDef.CustomerDefBuilder state(final String state) {
            this.state = state;
            return this;
        }

        public CustomerDef.CustomerDefBuilder countryCode(final String countryCode) {
            this.countryCode = countryCode;
            return this;
        }


        public CustomerDef.CustomerDefBuilder primaryPhoneNumber(final String primaryPhoneNumber) {
            this.primaryPhoneNumber = primaryPhoneNumber;
            return this;
        }

        public CustomerDef.CustomerDefBuilder primaryEmail(final String primaryEmail) {
            this.primaryEmail = primaryEmail;
            return this;
        }

        public CustomerDef build() {
            return new CustomerDef(this.customerId, this.addressType.getAddressType(), this.customerType.getCustomerType(), this.customerName, this.addressLine, this.postalCode, this.state, this.countryCode, this.primaryPhoneNumber, this.primaryEmail);
        }

        public String toString() {
            return "CustomerDef.CustomerDefBuilder(customerId=" + this.customerId + ", addressType=" + this.addressType + ", customerType=" + this.customerType + ", customerName=" + this.customerName + ", addressLine=" + this.addressLine + ", postalCode=" + this.postalCode + ", state=" + this.state + ", countryCode=" + this.countryCode + ", primaryPhoneNumber=" + this.primaryPhoneNumber + ", primaryEmail=" + this.primaryEmail + ")";
        }
    }
}
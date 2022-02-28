package in.nmaloth.identifierValidator.model.entity.card;

import in.nmaloth.payments.constants.card.LimitType;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PeriodicCardAmount  {

    @BsonProperty("limit_type")
    private String limitType;
    @BsonProperty("transaction_amt")
    private Long transactionAmount;
    @BsonProperty("transaction_nbr")
    private Integer transactionNumber;


    public static PeriodicCardAmount.PeriodicCardAmountBuilder builder() {
        return new PeriodicCardAmount.PeriodicCardAmountBuilder();
    }

    public String getLimitType() {
        return this.limitType;
    }

    public Long getTransactionAmount() {
        return this.transactionAmount;
    }

    public Integer getTransactionNumber() {
        return this.transactionNumber;
    }

    public void setLimitType(final String limitType) {
        this.limitType = limitType;
    }

    public void setTransactionAmount(final Long transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public void setTransactionNumber(final Integer transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public PeriodicCardAmount() {
    }

    public PeriodicCardAmount(final String limitType, final Long transactionAmount, final Integer transactionNumber) {
        this.limitType = limitType;
        this.transactionAmount = transactionAmount;
        this.transactionNumber = transactionNumber;
    }

    public static class PeriodicCardAmountBuilder {
        private String limitType;
        private Long transactionAmount;
        private Integer transactionNumber;

        PeriodicCardAmountBuilder() {
        }

        public PeriodicCardAmount.PeriodicCardAmountBuilder limitType(final LimitType limitType) {
            this.limitType = limitType.getLimitType();
            return this;
        }

        public PeriodicCardAmount.PeriodicCardAmountBuilder limitType(final String limitType) {
            this.limitType = limitType;
            return this;
        }

        public PeriodicCardAmount.PeriodicCardAmountBuilder transactionAmount(final Long transactionAmount) {
            this.transactionAmount = transactionAmount;
            return this;
        }

        public PeriodicCardAmount.PeriodicCardAmountBuilder transactionNumber(final Integer transactionNumber) {
            this.transactionNumber = transactionNumber;
            return this;
        }

        public PeriodicCardAmount build() {
            return new PeriodicCardAmount(this.limitType, this.transactionAmount, this.transactionNumber);
        }

        public String toString() {
            return "PeriodicCardAmount.PeriodicCardAmountBuilder(limitType=" + this.limitType + ", transactionAmount=" + this.transactionAmount + ", transactionNumber=" + this.transactionNumber + ")";
        }
    }
}

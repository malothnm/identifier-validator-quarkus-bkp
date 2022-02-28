package in.nmaloth.identifierValidator.model.entity.card;

import in.nmaloth.payments.constants.card.LimitType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class CacheCardAmount {


    private String transactionId;
    private String cardId;
    private String accountId;
    private LocalDateTime localDateTime;
    private Long amount;
    private Boolean debit;
    private List<String> limitTypes;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof CacheCardAmount)) {
            return false;
        } else {
            CacheCardAmount that = (CacheCardAmount)o;
            return this.getTransactionId().equals(that.getTransactionId());
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getTransactionId()});
    }


    public static CacheCardAmount.CacheCardAmountBuilder builder() {
        return new CacheCardAmount.CacheCardAmountBuilder();
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public LocalDateTime getLocalDateTime() {
        return this.localDateTime;
    }

    public Long getAmount() {
        return this.amount;
    }

    public Boolean getDebit() {
        return this.debit;
    }

    public List<String> getLimitTypes() {
        return this.limitTypes;
    }

    public void setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
    }

    public void setLocalDateTime(final LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public void setAmount(final Long amount) {
        this.amount = amount;
    }

    public void setDebit(final Boolean debit) {
        this.debit = debit;
    }

    public void setLimitTypes(final List<String> limitTypes) {
        this.limitTypes = limitTypes;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public CacheCardAmount() {
    }

    public CacheCardAmount(final String transactionId, final String cardId, final  String accountId,final LocalDateTime localDateTime, final Long amount, final Boolean debit, final List<String> limitTypes) {
        this.transactionId = transactionId;
        this.cardId = cardId;
        this.accountId = accountId;
            this.localDateTime = localDateTime;
        this.amount = amount;
        this.debit = debit;
        this.limitTypes = limitTypes;
    }

    public static class CacheCardAmountBuilder {
        private String transactionId;
        private String cardId;
        private String accountId;
        private LocalDateTime localDateTime;
        private Long amount;
        private Boolean debit;
        private List<String> limitTypes;

        CacheCardAmountBuilder() {
        }

        public CacheCardAmount.CacheCardAmountBuilder transactionId(final String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public CacheCardAmount.CacheCardAmountBuilder cardId(final String cardId) {
            this.cardId = cardId;
            return this;
        }
        public CacheCardAmount.CacheCardAmountBuilder accountId(final String accountId) {
            this.accountId = accountId;
            return this;
        }
        public CacheCardAmount.CacheCardAmountBuilder localDateTime(final LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
            return this;
        }

        public CacheCardAmount.CacheCardAmountBuilder amount(final Long amount) {
            this.amount = amount;
            return this;
        }

        public CacheCardAmount.CacheCardAmountBuilder debit(final Boolean debit) {
            this.debit = debit;
            return this;
        }

        public CacheCardAmount.CacheCardAmountBuilder limitTypes(final List<String> limitTypes) {
            this.limitTypes = limitTypes;
            return this;
        }

        public CacheCardAmount build() {
            return new CacheCardAmount(this.transactionId, this.cardId,this.accountId,this.localDateTime, this.amount, this.debit, this.limitTypes);
        }

        public String toString() {
            return "CacheCardAmount.CacheCardAmountBuilder(transactionId=" + this.transactionId + ", cardId=" + this.cardId + ", accountId=" + this.accountId + ", localDateTime=" + this.localDateTime + ", amount=" + this.amount + ", debit=" + this.debit + ", limitTypes=" + this.limitTypes + ")";
        }
    }
}

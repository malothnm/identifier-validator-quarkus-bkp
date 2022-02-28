package in.nmaloth.identifierValidator.model.entity.temp;

import in.nmaloth.payments.constants.card.LimitType;
import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ProtoDoc("@indexed")
public class CardTempBalance {


    private String id;
    private String cardNumber;
    private long amount;
    private List<String> limitTypes;

    @ProtoFactory
    public CardTempBalance(String id, String cardNumber, long amount, List<String> limitTypes) {
        this.id = Objects.requireNonNull(id);
        this.cardNumber = Objects.requireNonNull(cardNumber);
        this.amount = amount;
        this.limitTypes = Objects.requireNonNull(limitTypes);
    }

    public CardTempBalance() {
    }

    @ProtoField(number = 1)
    @ProtoDoc("@Field(index=INDEX.YES, analyze = Analyze.YES, store = STORE.YES)")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @ProtoField(number = 2)
    @ProtoDoc("@Field(index=INDEX.YES, analyze = Analyze.YES, store = STORE.YES)")
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @ProtoField(number = 3,defaultValue = "0")
    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    @ProtoField(number = 4)
    public List<String> getLimitTypes() {
        return limitTypes;
    }

    public void setLimitTypes(List<String> limitTypes) {
        this.limitTypes = limitTypes;
    }


    public static CardTempBalanceBuilder builder(){
        return new CardTempBalanceBuilder();
    }

    public static class CardTempBalanceBuilder {
        private String id;
        private String cardNumber;
        private long amount;
        private List<String> limitTypes = new ArrayList<>();

        CardTempBalanceBuilder() {
        }

        public CardTempBalance.CardTempBalanceBuilder id(final String id) {
            this.id = id;
            return this;
        }

        public CardTempBalance.CardTempBalanceBuilder cardNumber(final String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public CardTempBalance.CardTempBalanceBuilder amount(final Long amount) {
            this.amount = amount;
            return this;
        }

        public CardTempBalance.CardTempBalanceBuilder limitTypes(final LimitType limitTypes) {
            this.limitTypes.add(limitTypes.getLimitType());
            return this;
        }


        public CardTempBalance.CardTempBalanceBuilder limitTypes(final String limitTypes) {
            this.limitTypes.add(limitTypes);
            return this;
        }

        public CardTempBalance build() {
            return new CardTempBalance(this.id, this.cardNumber, this.amount, this.limitTypes);
        }

        public String toString() {
            return "CardTempBalance.CardTempBalanceBuilder(id=" + this.id + ", cardNumber=" + this.cardNumber + ", amount=" + this.amount + ", limitTypes=" + this.limitTypes + ")";
        }
    }
}

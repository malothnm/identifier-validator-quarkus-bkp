package in.nmaloth.identifierValidator.model.entity.temp;

import in.nmaloth.payments.constants.account.BalanceTypes;
import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ProtoDoc("@indexed")
public class AccountTempBalance {


    private String id;
    private String accountNumber;
    private long amount;
    private List<String> balanceTypes;

    @ProtoFactory
    public AccountTempBalance(String id, String accountNumber, long amount, List<String> balanceTypes) {
        this.id = Objects.requireNonNull(id);
        this.accountNumber = Objects.requireNonNull(accountNumber);
        this.amount = amount;
        this.balanceTypes = Objects.requireNonNull(balanceTypes);
    }

    public AccountTempBalance() {
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
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @ProtoField(number = 3,defaultValue = "0")
    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    @ProtoField(number = 4)
    public List<String> getBalanceTypes() {
        return balanceTypes;
    }

    public void setBalanceTypes(List<String> balanceTypes) {
        this.balanceTypes = balanceTypes;
    }


    public static AccountTempBalanceBuilder builder(){
        return new AccountTempBalanceBuilder();
    }

    public static class AccountTempBalanceBuilder {
        private String id;
        private String cardNumber;
        private long amount;
        private List<String> balanceTypes = new ArrayList<>();

        AccountTempBalanceBuilder() {
        }

        public AccountTempBalanceBuilder id(final String id) {
            this.id = id;
            return this;
        }

        public AccountTempBalanceBuilder cardNumber(final String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public AccountTempBalanceBuilder amount(final Long amount) {
            this.amount = amount;
            return this;
        }

        public AccountTempBalanceBuilder balanceTypes(final BalanceTypes balanceTypes) {
            this.balanceTypes.add(balanceTypes.getBalanceTypes());
            return this;
        }


        public AccountTempBalanceBuilder balanceTypes(final String balanceTypes) {
            this.balanceTypes.add(balanceTypes);
            return this;
        }

        public AccountTempBalance build() {
            return new AccountTempBalance(this.id, this.cardNumber, this.amount, this.balanceTypes);
        }

    }
}

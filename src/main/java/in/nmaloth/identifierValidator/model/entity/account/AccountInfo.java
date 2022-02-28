package in.nmaloth.identifierValidator.model.entity.account;

import in.nmaloth.payments.constants.BlockType;
import in.nmaloth.payments.constants.account.AccountType;
import in.nmaloth.payments.constants.account.BalanceTypes;
import in.nmaloth.payments.constants.card.LimitType;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@MongoEntity(collection = "account")
public class AccountInfo extends ReactivePanacheMongoEntity {

    @BsonProperty("account_id")
    private String accountId;
    @BsonProperty("org")
    private int org;
    @BsonProperty("product")
    private int product;
    @BsonProperty("account_type")
    private String accountType;
    @BsonProperty("block_type")
    private String blockType;
    @BsonProperty("limits_map")
    private Map<String, Long> limitsMap;
    @BsonProperty("balances_map")
    private Map<String, AccountBalances> balancesMap;


    public static AccountInfoBuilder builder() {
        return new AccountInfoBuilder();
    }

    public static Uni<Optional<AccountInfo>> findByAccountId(String accountId) {
        return find("account_id", accountId).firstResultOptional();
    }

    public String getAccountId() {
        return this.accountId;
    }

    public int getOrg() {
        return this.org;
    }

    public int getProduct() {
        return this.product;
    }

    public String getAccountType() {
        return this.accountType;
    }

    public String getBlockType() {
        return this.blockType;
    }

    public Map<String, Long> getLimitsMap() {
        return this.limitsMap;
    }

    public Map<String, AccountBalances> getBalancesMap() {
        return this.balancesMap;
    }

    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    public void setOrg(final int org) {
        this.org = org;
    }

    public void setProduct(final int product) {
        this.product = product;
    }

    public void setAccountType(final String accountType) {
        this.accountType = accountType;
    }

    public void setBlockType(final String blockType) {
        this.blockType = blockType;
    }

    public void setLimitsMap(final Map<String, Long> limitsMap) {
        this.limitsMap = limitsMap;
    }

    public void setBalancesMap(final Map<String, AccountBalances> balancesMap) {
        this.balancesMap = balancesMap;
    }

    public AccountInfo() {
    }

    public AccountInfo(final String accountId, final int org, final int product, final String accountType, final String blockType, final Map<String, Long> limitsMap, final Map<String, AccountBalances> balancesMap) {
        this.accountId = accountId;
        this.org = org;
        this.product = product;
        this.accountType = accountType;
        this.blockType = blockType;
        this.limitsMap = limitsMap;
        this.balancesMap = balancesMap;
    }

    public static class AccountInfoBuilder {
        private String accountId;
        private int org;
        private int product;
        private String accountType;
        private String blockType;
        private Map<String, Long> limitsMap;
        private Map<String, AccountBalances> balancesMap;

        AccountInfoBuilder() {
        }

        public AccountInfoBuilder accountId(final String accountId) {
            this.accountId = accountId;
            return this;
        }

        public AccountInfoBuilder org(final int org) {
            this.org = org;
            return this;
        }

        public AccountInfoBuilder product(final int product) {
            this.product = product;
            return this;
        }

        public AccountInfoBuilder accountType(final AccountType accountType) {
            this.accountType = accountType.getAccountType();
            return this;
        }

        public AccountInfoBuilder blockType(final BlockType blockType) {
            this.blockType = blockType.getBlockType();
            return this;
        }

        public AccountInfoBuilder limitsMap(final Map<BalanceTypes, Long> limitsMap) {
            this.limitsMap = new HashMap<>();
            limitsMap.entrySet().forEach(entry -> this.limitsMap.put(entry.getKey().getBalanceTypes(), entry.getValue()));
            return this;
        }

        public AccountInfoBuilder balancesMap(final Map<BalanceTypes, AccountBalances> balancesMap) {
            this.balancesMap = new HashMap<>();
            balancesMap.entrySet().forEach(entry -> this.balancesMap.put(entry.getKey().getBalanceTypes(), entry.getValue()));
            return this;
        }

        public AccountInfo build() {
            return new AccountInfo(this.accountId, this.org, this.product, this.accountType, this.blockType, this.limitsMap, this.balancesMap);
        }

        public String toString() {
            return "AccountAccumValues.AccountAccumValuesBuilder(accountId=" + this.accountId + ", org=" + this.org + ", product=" + this.product + ", accountType=" + this.accountType + ", blockType=" + this.blockType + ", limitsMap=" + this.limitsMap + ", balancesMap=" + this.balancesMap + ")";
        }
    }
}


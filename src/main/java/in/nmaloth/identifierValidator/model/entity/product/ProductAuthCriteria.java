package in.nmaloth.identifierValidator.model.entity.product;

import in.nmaloth.identifierValidator.model.entity.card.PeriodicCardAmount;
import in.nmaloth.payments.constants.IncludeExclude;
import in.nmaloth.payments.constants.PurchaseTypes;
import in.nmaloth.payments.constants.Strategy;
import in.nmaloth.payments.constants.card.LimitType;
import in.nmaloth.payments.constants.card.PeriodicType;
import in.nmaloth.payments.constants.instrument.CVM;
import in.nmaloth.payments.constants.products.*;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@MongoEntity(collection = "product_auth_criteria")
public class ProductAuthCriteria extends ReactivePanacheMongoEntity {

    public ObjectId id;

    @BsonProperty("org")
    private Integer org;

    @BsonProperty("product")
    private Integer product;

    @BsonProperty("criteria")
    private Integer criteria;

    @BsonProperty("strategy")
    private String strategy;

    @BsonProperty("ie_blocked_countries")
    private String blockingCountries;
    @BsonProperty("blocked_countries")
    private List<String> countryCodesBlocked;
    @BsonProperty("ie_blocked_currency")
    private String blockingCurrency;
    @BsonProperty("blocked_currencies")
    private List<String> currencyCodesBlocked;
    @BsonProperty("ie_block_states")
    private String blockingStates;
    @BsonProperty("blocked_states")
    private List<String> stateCodesBlocked;
    @BsonProperty("ie_block_mcc")
    private String blockingMCC;
    @BsonProperty("blocked_mcc")
    private List<MCCRange> mccBlocked;
    @BsonProperty("ie_block_purchase_type")
    private String blockingPurchaseTypes;
    @BsonProperty("blocked_purchase_types")
    private List<BlockingPurchaseType> purchaseTypesBlocked;
    @BsonProperty("ie_block_limit_type")
    private String blockingLimitTypes;
    @BsonProperty("block_limit_types")
    private List<BlockingLimitType> limitTypesBlocked;
    @BsonProperty("ie_block_balance_type")
    private String blockingBalanceTypes;
    @BsonProperty("blocked_balance_types")
    private List<BlockingBalanceType> balanceTypesBlocked;
    @BsonProperty("ie_block_transaction_type")
    private String blockingTransactionTypes;
    @BsonProperty("blocked_transaction_types")
    private List<BlockingTransactionType> transactionTypesBlocked;
    @BsonProperty("ie_block_terminal_type")
    private String blockTerminalTypes;
    @BsonProperty("blocked_terminal_types")
    private List<BlockingTerminalType> terminalTypesBlocked;
    @BsonProperty("block_installments")
    private boolean blockInstallments;
    @BsonProperty("block_cashback")
    private boolean blockCashBack;
    @BsonProperty("block_international")
    private boolean blockInternational;
    @BsonProperty("card_limits_map")
    private Map<String, Map<String, PeriodicCardAmount>> cardLimitMap;


    public static ProductAuthCriteria.ProductAuthCriteriaBuilder builder() {
        return new ProductAuthCriteria.ProductAuthCriteriaBuilder();
    }

    public static Uni<Optional<ProductAuthCriteria>> findByProductId(Integer org, Integer product, Integer criteria) {
        return find("org= ?1 and product= ?2 and criteria= ?3", org, product, criteria).firstResultOptional();
    }

    public Integer getOrg() {
        return org;
    }

    public void setOrg(Integer org) {
        this.org = org;
    }

    public Integer getProduct() {
        return product;
    }

    public void setProduct(Integer product) {
        this.product = product;
    }

    public Integer getCriteria() {
        return criteria;
    }

    public void setCriteria(Integer criteria) {
        this.criteria = criteria;
    }


    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getBlockingCountries() {
        return this.blockingCountries;
    }

    public List<String> getCountryCodesBlocked() {
        return this.countryCodesBlocked;
    }

    public String getBlockingCurrency() {
        return this.blockingCurrency;
    }

    public List<String> getCurrencyCodesBlocked() {
        return this.currencyCodesBlocked;
    }

    public String getBlockingStates() {
        return this.blockingStates;
    }

    public List<String> getStateCodesBlocked() {
        return this.stateCodesBlocked;
    }

    public String getBlockingMCC() {
        return this.blockingMCC;
    }

    public List<MCCRange> getMccBlocked() {
        return this.mccBlocked;
    }

    public String getBlockingPurchaseTypes() {
        return this.blockingPurchaseTypes;
    }

    public List<BlockingPurchaseType> getPurchaseTypesBlocked() {
        return this.purchaseTypesBlocked;
    }

    public String getBlockingLimitTypes() {
        return this.blockingLimitTypes;
    }

    public List<BlockingLimitType> getLimitTypesBlocked() {
        return this.limitTypesBlocked;
    }

    public String getBlockingBalanceTypes() {
        return this.blockingBalanceTypes;
    }

    public List<BlockingBalanceType> getBalanceTypesBlocked() {
        return this.balanceTypesBlocked;
    }

    public String getBlockingTransactionTypes() {
        return this.blockingTransactionTypes;
    }

    public List<BlockingTransactionType> getTransactionTypesBlocked() {
        return this.transactionTypesBlocked;
    }

    public String getBlockTerminalTypes() {
        return this.blockTerminalTypes;
    }

    public List<BlockingTerminalType> getTerminalTypesBlocked() {
        return this.terminalTypesBlocked;
    }

    public boolean isBlockInstallments() {
        return this.blockInstallments;
    }

    public boolean isBlockCashBack() {
        return this.blockCashBack;
    }

    public boolean isBlockInternational() {
        return this.blockInternational;
    }



    public void setBlockingCountries(final String blockingCountries) {
        this.blockingCountries = blockingCountries;
    }

    public void setCountryCodesBlocked(final List<String> countryCodesBlocked) {
        this.countryCodesBlocked = countryCodesBlocked;
    }

    public void setBlockingCurrency(final String blockingCurrency) {
        this.blockingCurrency = blockingCurrency;
    }

    public void setCurrencyCodesBlocked(final List<String> currencyCodesBlocked) {
        this.currencyCodesBlocked = currencyCodesBlocked;
    }

    public void setBlockingStates(final String blockingStates) {
        this.blockingStates = blockingStates;
    }

    public void setStateCodesBlocked(final List<String> stateCodesBlocked) {
        this.stateCodesBlocked = stateCodesBlocked;
    }

    public void setBlockingMCC(final String blockingMCC) {
        this.blockingMCC = blockingMCC;
    }

    public void setMccBlocked(final List<MCCRange> mccBlocked) {
        this.mccBlocked = mccBlocked;
    }

    public void setBlockingPurchaseTypes(final String blockingPurchaseTypes) {
        this.blockingPurchaseTypes = blockingPurchaseTypes;
    }

    public void setPurchaseTypesBlocked(final List<BlockingPurchaseType> purchaseTypesBlocked) {
        this.purchaseTypesBlocked = purchaseTypesBlocked;
    }

    public void setBlockingLimitTypes(final String blockingLimitTypes) {
        this.blockingLimitTypes = blockingLimitTypes;
    }

    public void setLimitTypesBlocked(final List<BlockingLimitType> limitTypesBlocked) {
        this.limitTypesBlocked = limitTypesBlocked;
    }

    public void setBlockingBalanceTypes(final String blockingBalanceTypes) {
        this.blockingBalanceTypes = blockingBalanceTypes;
    }

    public void setBalanceTypesBlocked(final List<BlockingBalanceType> balanceTypesBlocked) {
        this.balanceTypesBlocked = balanceTypesBlocked;
    }

    public void setBlockingTransactionTypes(final String blockingTransactionTypes) {
        this.blockingTransactionTypes = blockingTransactionTypes;
    }

    public void setTransactionTypesBlocked(final List<BlockingTransactionType> transactionTypesBlocked) {
        this.transactionTypesBlocked = transactionTypesBlocked;
    }

    public void setBlockTerminalTypes(final String blockTerminalTypes) {
        this.blockTerminalTypes = blockTerminalTypes;
    }

    public void setTerminalTypesBlocked(final List<BlockingTerminalType> terminalTypesBlocked) {
        this.terminalTypesBlocked = terminalTypesBlocked;
    }

    public void setBlockInstallments(final boolean blockInstallments) {
        this.blockInstallments = blockInstallments;
    }

    public void setBlockCashBack(final boolean blockCashBack) {
        this.blockCashBack = blockCashBack;
    }

    public void setBlockInternational(final boolean blockInternational) {
        this.blockInternational = blockInternational;
    }

    public Map<String, Map<String, PeriodicCardAmount>> getCardLimitMap() {
        return cardLimitMap;
    }

    public void setCardLimitMap(Map<String, Map<String, PeriodicCardAmount>> cardLimitMap) {
        this.cardLimitMap = cardLimitMap;
    }

    public ProductAuthCriteria() {
    }

    public ProductAuthCriteria(final Integer org, Integer product, Integer criteria,  final String strategy,
                               final String blockingCountries,
                               final List<String> countryCodesBlocked, final String blockingCurrency,
                               final List<String> currencyCodesBlocked, final String blockingStates,
                               final List<String> stateCodesBlocked, final String blockingMCC,
                               final List<MCCRange> mccBlocked, final String blockingPurchaseTypes,
                               final List<BlockingPurchaseType> purchaseTypesBlocked, final String blockingLimitTypes,
                               final List<BlockingLimitType> limitTypesBlocked, final String blockingBalanceTypes,
                               final List<BlockingBalanceType> balanceTypesBlocked, final String blockingTransactionTypes,
                               final List<BlockingTransactionType> transactionTypesBlocked, final String blockTerminalTypes,
                               final List<BlockingTerminalType> terminalTypesBlocked, final boolean blockInstallments,
                               final boolean blockCashBack, final boolean blockInternational,
                               Map<String, Map<String, PeriodicCardAmount>> cardLimitMap) {
        this.org = org;
        this.product = product;
        this.criteria = criteria;
        this.strategy = strategy;
        this.blockingCountries = blockingCountries;
        this.countryCodesBlocked = countryCodesBlocked;
        this.blockingCurrency = blockingCurrency;
        this.currencyCodesBlocked = currencyCodesBlocked;
        this.blockingStates = blockingStates;
        this.stateCodesBlocked = stateCodesBlocked;
        this.blockingMCC = blockingMCC;
        this.mccBlocked = mccBlocked;
        this.blockingPurchaseTypes = blockingPurchaseTypes;
        this.purchaseTypesBlocked = purchaseTypesBlocked;
        this.blockingLimitTypes = blockingLimitTypes;
        this.limitTypesBlocked = limitTypesBlocked;
        this.blockingBalanceTypes = blockingBalanceTypes;
        this.balanceTypesBlocked = balanceTypesBlocked;
        this.blockingTransactionTypes = blockingTransactionTypes;
        this.transactionTypesBlocked = transactionTypesBlocked;
        this.blockTerminalTypes = blockTerminalTypes;
        this.terminalTypesBlocked = terminalTypesBlocked;
        this.blockInstallments = blockInstallments;
        this.blockCashBack = blockCashBack;
        this.blockInternational = blockInternational;
        this.cardLimitMap = cardLimitMap;
    }

    public static class ProductAuthCriteriaBuilder {
        private Integer org;
        private Integer product;
        private Integer criteria;
        private Strategy strategy;

        private IncludeExclude blockingCountries;
        private List<String> countryCodesBlocked;
        private IncludeExclude blockingCurrency;
        private List<String> currencyCodesBlocked;
        private IncludeExclude blockingStates;
        private List<String> stateCodesBlocked;
        private IncludeExclude blockingMCC;
        private List<MCCRange> mccBlocked;
        private IncludeExclude blockingPurchaseTypes;
        private List<BlockingPurchaseType> purchaseTypesBlocked;
        private IncludeExclude blockingLimitTypes;
        private List<BlockingLimitType> limitTypesBlocked;
        private IncludeExclude blockingBalanceTypes;
        private List<BlockingBalanceType> balanceTypesBlocked;
        private IncludeExclude blockingTransactionTypes;
        private List<BlockingTransactionType> transactionTypesBlocked;
        private IncludeExclude blockTerminalTypes;
        private List<BlockingTerminalType> terminalTypesBlocked;
        private boolean blockInstallments;
        private boolean blockCashBack;
        private boolean blockInternational;
        private Map<String, Map<String, PeriodicCardAmount>> cardLimitMap;

        ProductAuthCriteriaBuilder() {
        }


        public ProductAuthCriteria.ProductAuthCriteriaBuilder cardLimitMap(final Map<PeriodicType, Map<LimitType, PeriodicCardAmount>> cardLimitMap) {


            Map<String,Map<String,PeriodicCardAmount>> periodicCardLimitMap = new HashMap<>();

           cardLimitMap.entrySet().forEach(periodicTypeMapEntry -> periodicCardLimitMap.put(periodicTypeMapEntry.getKey().getPeriodicType(), createNewCardLimitMap(periodicTypeMapEntry.getValue())));
            this.cardLimitMap = periodicCardLimitMap;
            ;
            return this;
        }

        private Map<String, PeriodicCardAmount> createNewCardLimitMap(Map<LimitType, PeriodicCardAmount> value) {

            Map<String, PeriodicCardAmount> periodicCardAmountMap = new HashMap<>();

            value.entrySet().forEach(entry-> periodicCardAmountMap.put(entry.getKey().getLimitType(),entry.getValue()));
            return periodicCardAmountMap;

        }


        public ProductAuthCriteria.ProductAuthCriteriaBuilder org(final Integer org) {
            this.org = org;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder product(final Integer product) {
            this.product = product;
            return this;
        }
        public ProductAuthCriteria.ProductAuthCriteriaBuilder criteria(final Integer criteria) {
            this.criteria = criteria;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder strategy(final Strategy strategy) {
            this.strategy = strategy;
            return this;
        }


        public ProductAuthCriteria.ProductAuthCriteriaBuilder blockingCountries(final IncludeExclude blockingCountries) {
            this.blockingCountries = blockingCountries;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder countryCodesBlocked(final List<String> countryCodesBlocked) {
            this.countryCodesBlocked = countryCodesBlocked;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder blockingCurrency(final IncludeExclude blockingCurrency) {
            this.blockingCurrency = blockingCurrency;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder currencyCodesBlocked(final List<String> currencyCodesBlocked) {
            this.currencyCodesBlocked = currencyCodesBlocked;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder blockingStates(final IncludeExclude blockingStates) {
            this.blockingStates = blockingStates;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder stateCodesBlocked(final List<String> stateCodesBlocked) {
            this.stateCodesBlocked = stateCodesBlocked;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder blockingMCC(final IncludeExclude blockingMCC) {
            this.blockingMCC = blockingMCC;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder mccBlocked(final List<MCCRange> mccBlocked) {
            this.mccBlocked = mccBlocked;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder blockingPurchaseTypes(final IncludeExclude blockingPurchaseTypes) {
            this.blockingPurchaseTypes = blockingPurchaseTypes;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder purchaseTypesBlocked(final List<BlockingPurchaseType> purchaseTypesBlocked) {
            this.purchaseTypesBlocked = purchaseTypesBlocked;

            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder blockingLimitTypes(final IncludeExclude blockingLimitTypes) {
            this.blockingLimitTypes = blockingLimitTypes;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder limitTypesBlocked(final List<BlockingLimitType> limitTypesBlocked) {
            this.limitTypesBlocked = limitTypesBlocked;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder blockingBalanceTypes(final IncludeExclude blockingBalanceTypes) {
            this.blockingBalanceTypes = blockingBalanceTypes;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder balanceTypesBlocked(final List<BlockingBalanceType> balanceTypesBlocked) {
            this.balanceTypesBlocked = balanceTypesBlocked;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder blockingTransactionTypes(final IncludeExclude blockingTransactionTypes) {
            this.blockingTransactionTypes = blockingTransactionTypes;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder transactionTypesBlocked(final List<BlockingTransactionType> transactionTypesBlocked) {
            this.transactionTypesBlocked = transactionTypesBlocked;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder blockTerminalTypes(final IncludeExclude blockTerminalTypes) {
            this.blockTerminalTypes = blockTerminalTypes;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder terminalTypesBlocked(final List<BlockingTerminalType> terminalTypesBlocked) {
            this.terminalTypesBlocked = terminalTypesBlocked;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder blockInstallments(final boolean blockInstallments) {
            this.blockInstallments = blockInstallments;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder blockCashBack(final boolean blockCashBack) {
            this.blockCashBack = blockCashBack;
            return this;
        }

        public ProductAuthCriteria.ProductAuthCriteriaBuilder blockInternational(final boolean blockInternational) {
            this.blockInternational = blockInternational;
            return this;
        }

        public ProductAuthCriteria build() {
            return new ProductAuthCriteria(this.org,this.product,this.criteria,this.strategy.getStrategy(),
                    this.blockingCountries.getIncludeExclude(), this.countryCodesBlocked, this.blockingCurrency.getIncludeExclude(), this.currencyCodesBlocked, this.blockingStates.getIncludeExclude(), this.stateCodesBlocked, this.blockingMCC.getIncludeExclude(),
                    this.mccBlocked, this.blockingPurchaseTypes.getIncludeExclude(),
                    this.purchaseTypesBlocked, this.blockingLimitTypes.getIncludeExclude(),
                    this.limitTypesBlocked, this.blockingBalanceTypes.getIncludeExclude(),
                    this.balanceTypesBlocked, this.blockingTransactionTypes.getIncludeExclude(),
                    this.transactionTypesBlocked, this.blockTerminalTypes.getIncludeExclude(),
                    this.terminalTypesBlocked, this.blockInstallments, this.blockCashBack, this.blockInternational,this.cardLimitMap);
        }

        public String toString() {
            return "ProductAuthCriteria.ProductAuthCriteriaBuilder(org=" + this.org + ", product=" + this.product + ", criteria=" + this.criteria + ", strategy=" + this.strategy +  ", blockingCountries=" + this.blockingCountries + ", countryCodesBlocked=" + this.countryCodesBlocked + ", blockingCurrency=" + this.blockingCurrency + ", currencyCodesBlocked=" + this.currencyCodesBlocked + ", blockingStates=" + this.blockingStates + ", stateCodesBlocked=" + this.stateCodesBlocked + ", blockingMCC=" + this.blockingMCC + ", mccBlocked=" + this.mccBlocked + ", blockingPurchaseTypes=" + this.blockingPurchaseTypes + ", purchaseTypesBlocked=" + this.purchaseTypesBlocked + ", blockingLimitTypes=" + this.blockingLimitTypes + ", limitTypesBlocked=" + this.limitTypesBlocked + ", blockingBalanceTypes=" + this.blockingBalanceTypes + ", balanceTypesBlocked=" + this.balanceTypesBlocked + ", blockingTransactionTypes=" + this.blockingTransactionTypes + ", transactionTypesBlocked=" + this.transactionTypesBlocked + ", blockTerminalTypes=" + this.blockTerminalTypes + ", terminalTypesBlocked=" + this.terminalTypesBlocked + ", blockInstallments=" + this.blockInstallments + ", blockCashBack=" + this.blockCashBack + ", blockInternational=" + this.blockInternational + ")";
        }
    }
}

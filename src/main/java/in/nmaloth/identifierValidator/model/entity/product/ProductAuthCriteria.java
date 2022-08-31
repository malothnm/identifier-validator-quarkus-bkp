package in.nmaloth.identifierValidator.model.entity.product;

import in.nmaloth.identifierValidator.model.entity.card.PeriodicCardAmount;
import in.nmaloth.payments.constants.IncludeExclude;
import in.nmaloth.payments.constants.Strategy;
import in.nmaloth.payments.constants.card.LimitType;
import in.nmaloth.payments.constants.card.PeriodicType;
import in.nmaloth.payments.constants.products.*;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@MongoEntity(collection = "product_auth_criteria")
public class ProductAuthCriteria extends ReactivePanacheMongoEntity {


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
    private List<BlockingValue> purchaseTypesBlocked;
    @BsonProperty("ie_block_limit_type")
    private String blockingLimitTypes;
    @BsonProperty("block_limit_types")
    private List<BlockingValue> limitTypesBlocked;
    @BsonProperty("ie_block_balance_type")
    private String blockingBalanceTypes;
    @BsonProperty("blocked_balance_types")
    private List<BlockingValue> balanceTypesBlocked;
    @BsonProperty("ie_block_transaction_type")
    private String blockingTransactionTypes;
    @BsonProperty("blocked_transaction_types")
    private List<BlockingValue> transactionTypesBlocked;
    @BsonProperty("ie_block_terminal_type")
    private String blockTerminalTypes;
    @BsonProperty("blocked_terminal_types")
    private List<BlockingValue> terminalTypesBlocked;
    @BsonProperty("block_installments")
    private boolean blockInstallments;
    @BsonProperty("block_cashback")
    private boolean blockCashBack;
    @BsonProperty("block_international")
    private boolean blockInternational;
    @BsonProperty("card_limits_map")
    private Map<String, Map<String, PeriodicCardAmount>> cardLimitMap;


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

    public String getBlockingLimitTypes() {
        return this.blockingLimitTypes;
    }

    public String getBlockingBalanceTypes() {
        return this.blockingBalanceTypes;
    }

    public String getBlockingTransactionTypes() {
        return this.blockingTransactionTypes;
    }

    public String getBlockTerminalTypes() {
        return this.blockTerminalTypes;
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

    public void setBlockingLimitTypes(final String blockingLimitTypes) {
        this.blockingLimitTypes = blockingLimitTypes;
    }


    public void setBlockingBalanceTypes(final String blockingBalanceTypes) {
        this.blockingBalanceTypes = blockingBalanceTypes;
    }

    public void setBlockingTransactionTypes(final String blockingTransactionTypes) {
        this.blockingTransactionTypes = blockingTransactionTypes;
    }

    public void setBlockTerminalTypes(final String blockTerminalTypes) {
        this.blockTerminalTypes = blockTerminalTypes;
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

    public List<BlockingValue> getPurchaseTypesBlocked() {
        return purchaseTypesBlocked;
    }

    public void setPurchaseTypesBlocked(List<BlockingValue> purchaseTypesBlocked) {
        this.purchaseTypesBlocked = purchaseTypesBlocked;
    }

    public List<BlockingValue> getLimitTypesBlocked() {
        return limitTypesBlocked;
    }

    public void setLimitTypesBlocked(List<BlockingValue> limitTypesBlocked) {
        this.limitTypesBlocked = limitTypesBlocked;
    }

    public List<BlockingValue> getBalanceTypesBlocked() {
        return balanceTypesBlocked;
    }

    public void setBalanceTypesBlocked(List<BlockingValue> balanceTypesBlocked) {
        this.balanceTypesBlocked = balanceTypesBlocked;
    }

    public List<BlockingValue> getTransactionTypesBlocked() {
        return transactionTypesBlocked;
    }

    public void setTransactionTypesBlocked(List<BlockingValue> transactionTypesBlocked) {
        this.transactionTypesBlocked = transactionTypesBlocked;
    }

    public List<BlockingValue> getTerminalTypesBlocked() {
        return terminalTypesBlocked;
    }

    public void setTerminalTypesBlocked(List<BlockingValue> terminalTypesBlocked) {
        this.terminalTypesBlocked = terminalTypesBlocked;
    }

    public ProductAuthCriteria() {
    }

}

package in.nmaloth.identifierValidator.model;

import in.nmaloth.identifierValidator.model.entity.card.PeriodicCardAmount;
import in.nmaloth.identifierValidator.model.entity.product.MCCRange;
import in.nmaloth.identifierValidator.model.entity.product.ProductAuthCriteria;
import in.nmaloth.payments.constants.IncludeExclude;
import in.nmaloth.payments.constants.Strategy;
import in.nmaloth.payments.constants.card.LimitType;
import in.nmaloth.payments.constants.card.PeriodicType;
import in.nmaloth.payments.constants.products.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductCriteria {

    private Strategy strategy;
    private IncludeExclude blockingCountries;
    private String[] countryCodesBlocked;
    private IncludeExclude blockingCurrency;
    private String[] currencyCodesBlocked;
    private IncludeExclude blockingStates;
    private String[] stateCodesBlocked;
    private IncludeExclude blockingMCC;
    private MCCRange[] mccBlocked;
    private IncludeExclude blockingPurchaseTypes;
    private BlockingPurchaseType[] purchaseTypesBlocked;
    private IncludeExclude blockingLimitTypes;
    private BlockingLimitType[] limitTypesBlocked;
    private IncludeExclude blockingBalanceTypes;
    private BlockingBalanceType[] balanceTypesBlocked;
    private IncludeExclude blockingTransactionTypes;
    private BlockingTransactionType[] transactionTypesBlocked;
    private IncludeExclude blockTerminalTypes;
    private BlockingTerminalType[] terminalTypesBlocked;
    private boolean blockInstallments;
    private boolean blockCashBack;
    private boolean blockInternational;
    private Map<String, Map<String, PeriodicCardAmount>> cardLimitMap;

    public ProductCriteria(Strategy strategy, IncludeExclude blockingCountries, String[] countryCodesBlocked,
                           IncludeExclude blockingCurrency, String[] currencyCodesBlocked, IncludeExclude blockingStates,
                           String[] stateCodesBlocked, IncludeExclude blockingMCC, MCCRange[] mccBlocked,
                           IncludeExclude blockingPurchaseTypes, BlockingPurchaseType[] purchaseTypesBlocked,
                           IncludeExclude blockingLimitTypes, BlockingLimitType[] limitTypesBlocked,
                           IncludeExclude blockingBalanceTypes, BlockingBalanceType[] balanceTypesBlocked,
                           IncludeExclude blockingTransactionTypes, BlockingTransactionType[] transactionTypesBlocked,
                           IncludeExclude blockTerminalTypes, BlockingTerminalType[] terminalTypesBlocked,
                           boolean blockInstallments, boolean blockCashBack, boolean blockInternational,
                           Map<String, Map<String, PeriodicCardAmount>> cardLimitMap) {
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

    public ProductCriteria() {
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public IncludeExclude getBlockingCountries() {
        return blockingCountries;
    }

    public void setBlockingCountries(IncludeExclude blockingCountries) {
        this.blockingCountries = blockingCountries;
    }

    public String[] getCountryCodesBlocked() {
        return countryCodesBlocked;
    }

    public void setCountryCodesBlocked(String[] countryCodesBlocked) {
        this.countryCodesBlocked = countryCodesBlocked;
    }

    public IncludeExclude getBlockingCurrency() {
        return blockingCurrency;
    }

    public void setBlockingCurrency(IncludeExclude blockingCurrency) {
        this.blockingCurrency = blockingCurrency;
    }

    public String[] getCurrencyCodesBlocked() {
        return currencyCodesBlocked;
    }

    public void setCurrencyCodesBlocked(String[] currencyCodesBlocked) {
        this.currencyCodesBlocked = currencyCodesBlocked;
    }

    public IncludeExclude getBlockingStates() {
        return blockingStates;
    }

    public void setBlockingStates(IncludeExclude blockingStates) {
        this.blockingStates = blockingStates;
    }

    public String[] getStateCodesBlocked() {
        return stateCodesBlocked;
    }

    public void setStateCodesBlocked(String[] stateCodesBlocked) {
        this.stateCodesBlocked = stateCodesBlocked;
    }

    public IncludeExclude getBlockingMCC() {
        return blockingMCC;
    }

    public void setBlockingMCC(IncludeExclude blockingMCC) {
        this.blockingMCC = blockingMCC;
    }

    public MCCRange[] getMccBlocked() {
        return mccBlocked;
    }

    public void setMccBlocked(MCCRange[] mccBlocked) {
        this.mccBlocked = mccBlocked;
    }

    public IncludeExclude getBlockingPurchaseTypes() {
        return blockingPurchaseTypes;
    }

    public void setBlockingPurchaseTypes(IncludeExclude blockingPurchaseTypes) {
        this.blockingPurchaseTypes = blockingPurchaseTypes;
    }

    public BlockingPurchaseType[] getPurchaseTypesBlocked() {
        return purchaseTypesBlocked;
    }

    public void setPurchaseTypesBlocked(BlockingPurchaseType[] purchaseTypesBlocked) {
        this.purchaseTypesBlocked = purchaseTypesBlocked;
    }

    public IncludeExclude getBlockingLimitTypes() {
        return blockingLimitTypes;
    }

    public void setBlockingLimitTypes(IncludeExclude blockingLimitTypes) {
        this.blockingLimitTypes = blockingLimitTypes;
    }

    public BlockingLimitType[] getLimitTypesBlocked() {
        return limitTypesBlocked;
    }

    public void setLimitTypesBlocked(BlockingLimitType[] limitTypesBlocked) {
        this.limitTypesBlocked = limitTypesBlocked;
    }

    public IncludeExclude getBlockingBalanceTypes() {
        return blockingBalanceTypes;
    }

    public void setBlockingBalanceTypes(IncludeExclude blockingBalanceTypes) {
        this.blockingBalanceTypes = blockingBalanceTypes;
    }

    public BlockingBalanceType[] getBalanceTypesBlocked() {
        return balanceTypesBlocked;
    }

    public void setBalanceTypesBlocked(BlockingBalanceType[] balanceTypesBlocked) {
        this.balanceTypesBlocked = balanceTypesBlocked;
    }

    public IncludeExclude getBlockingTransactionTypes() {
        return blockingTransactionTypes;
    }

    public void setBlockingTransactionTypes(IncludeExclude blockingTransactionTypes) {
        this.blockingTransactionTypes = blockingTransactionTypes;
    }

    public BlockingTransactionType[] getTransactionTypesBlocked() {
        return transactionTypesBlocked;
    }

    public void setTransactionTypesBlocked(BlockingTransactionType[] transactionTypesBlocked) {
        this.transactionTypesBlocked = transactionTypesBlocked;
    }

    public IncludeExclude getBlockTerminalTypes() {
        return blockTerminalTypes;
    }

    public void setBlockTerminalTypes(IncludeExclude blockTerminalTypes) {
        this.blockTerminalTypes = blockTerminalTypes;
    }

    public BlockingTerminalType[] getTerminalTypesBlocked() {
        return terminalTypesBlocked;
    }

    public void setTerminalTypesBlocked(BlockingTerminalType[] terminalTypesBlocked) {
        this.terminalTypesBlocked = terminalTypesBlocked;
    }

    public boolean isBlockInstallments() {
        return blockInstallments;
    }

    public void setBlockInstallments(boolean blockInstallments) {
        this.blockInstallments = blockInstallments;
    }

    public boolean isBlockCashBack() {
        return blockCashBack;
    }

    public void setBlockCashBack(boolean blockCashBack) {
        this.blockCashBack = blockCashBack;
    }

    public boolean isBlockInternational() {
        return blockInternational;
    }

    public void setBlockInternational(boolean blockInternational) {
        this.blockInternational = blockInternational;
    }

    public Map<String, Map<String, PeriodicCardAmount>> getCardLimitMap() {
        return cardLimitMap;
    }

    public void setCardLimitMap(Map<String, Map<String, PeriodicCardAmount>> cardLimitMap) {
        this.cardLimitMap = cardLimitMap;
    }

    public static ProductCriteriaBuilder builder(){
        return new ProductCriteriaBuilder();
    }

    public static class ProductCriteriaBuilder {

        private Strategy strategy;
        private IncludeExclude blockingCountries;
        private String[] countryCodesBlocked;
        private IncludeExclude blockingCurrency;
        private String[] currencyCodesBlocked;
        private IncludeExclude blockingStates;
        private String[] stateCodesBlocked;
        private IncludeExclude blockingMCC;
        private MCCRange[] mccBlocked;
        private IncludeExclude blockingPurchaseTypes;
        private BlockingPurchaseType[] purchaseTypesBlocked;
        private IncludeExclude blockingLimitTypes;
        private BlockingLimitType[] limitTypesBlocked;
        private IncludeExclude blockingBalanceTypes;
        private BlockingBalanceType[] balanceTypesBlocked;
        private IncludeExclude blockingTransactionTypes;
        private BlockingTransactionType[] transactionTypesBlocked;
        private IncludeExclude blockTerminalTypes;
        private BlockingTerminalType[] terminalTypesBlocked;
        private boolean blockInstallments;
        private boolean blockCashBack;
        private boolean blockInternational;
        private Map<String, Map<String, PeriodicCardAmount>> cardLimitMap;

        public ProductCriteriaBuilder cardLimitMap(final Map<PeriodicType, Map<LimitType, PeriodicCardAmount>> cardLimitMap) {

            this.cardLimitMap = new HashMap<>();
             cardLimitMap.forEach((periodicType,limitMap) -> this.cardLimitMap.put(periodicType.getPeriodicType(),convertPeriodicCardAmountMap(limitMap)));
            ;
            return this;
        }

        private Map<String, PeriodicCardAmount> convertPeriodicCardAmountMap(Map<LimitType, PeriodicCardAmount> limitMap) {

            Map<String, PeriodicCardAmount> periodicCardAmountMap = new HashMap<>();
            limitMap.forEach((limitType,cardAmountMap)-> periodicCardAmountMap.put(limitType.getLimitType(),cardAmountMap));
            return periodicCardAmountMap;

        }


        public ProductCriteriaBuilder strategy(final Strategy strategy) {
            this.strategy = strategy;
            return this;
        }


        public ProductCriteriaBuilder blockingCountries(final IncludeExclude blockingCountries) {
            this.blockingCountries = blockingCountries;
            return this;
        }

        public ProductCriteriaBuilder countryCodesBlocked(final String[] countryCodesBlocked) {
            this.countryCodesBlocked = countryCodesBlocked;
            return this;
        }

        public ProductCriteriaBuilder blockingCurrency(final IncludeExclude blockingCurrency) {
            this.blockingCurrency = blockingCurrency;
            return this;
        }

        public ProductCriteriaBuilder currencyCodesBlocked(final String[] currencyCodesBlocked) {
            this.currencyCodesBlocked = currencyCodesBlocked;
            return this;
        }

        public ProductCriteriaBuilder blockingStates(final IncludeExclude blockingStates) {
            this.blockingStates = blockingStates;
            return this;
        }

        public ProductCriteriaBuilder stateCodesBlocked(final String[] stateCodesBlocked) {
            this.stateCodesBlocked = stateCodesBlocked;
            return this;
        }

        public ProductCriteriaBuilder blockingMCC(final IncludeExclude blockingMCC) {
            this.blockingMCC = blockingMCC;
            return this;
        }

        public ProductCriteriaBuilder mccBlocked(final MCCRange[] mccBlocked) {
            this.mccBlocked = mccBlocked;
            return this;
        }

        public ProductCriteriaBuilder blockingPurchaseTypes(final IncludeExclude blockingPurchaseTypes) {
            this.blockingPurchaseTypes = blockingPurchaseTypes;
            return this;
        }

        public ProductCriteriaBuilder purchaseTypesBlocked(final BlockingPurchaseType[] purchaseTypesBlocked) {
            this.purchaseTypesBlocked = purchaseTypesBlocked;

            return this;
        }

        public ProductCriteriaBuilder blockingLimitTypes(final IncludeExclude blockingLimitTypes) {
            this.blockingLimitTypes = blockingLimitTypes;
            return this;
        }

        public ProductCriteriaBuilder limitTypesBlocked(final BlockingLimitType[] limitTypesBlocked) {
            this.limitTypesBlocked = limitTypesBlocked;
            return this;
        }

        public ProductCriteriaBuilder blockingBalanceTypes(final IncludeExclude blockingBalanceTypes) {
            this.blockingBalanceTypes = blockingBalanceTypes;
            return this;
        }

        public ProductCriteriaBuilder balanceTypesBlocked(final BlockingBalanceType[] balanceTypesBlocked) {
            this.balanceTypesBlocked = balanceTypesBlocked;
            return this;
        }

        public ProductCriteriaBuilder blockingTransactionTypes(final IncludeExclude blockingTransactionTypes) {
            this.blockingTransactionTypes = blockingTransactionTypes;
            return this;
        }

        public ProductCriteriaBuilder transactionTypesBlocked(final BlockingTransactionType[] transactionTypesBlocked) {
            this.transactionTypesBlocked = transactionTypesBlocked;
            return this;
        }

        public ProductCriteriaBuilder blockTerminalTypes(final IncludeExclude blockTerminalTypes) {
            this.blockTerminalTypes = blockTerminalTypes;
            return this;
        }

        public ProductCriteriaBuilder terminalTypesBlocked(final BlockingTerminalType[] terminalTypesBlocked) {
            this.terminalTypesBlocked = terminalTypesBlocked;
            return this;
        }

        public ProductCriteriaBuilder blockInstallments(final boolean blockInstallments) {
            this.blockInstallments = blockInstallments;
            return this;
        }

        public ProductCriteriaBuilder blockCashBack(final boolean blockCashBack) {
            this.blockCashBack = blockCashBack;
            return this;
        }

        public ProductCriteriaBuilder blockInternational(final boolean blockInternational) {
            this.blockInternational = blockInternational;
            return this;
        }

        public ProductCriteria build() {
            return new ProductCriteria(this.strategy,
                    this.blockingCountries, this.countryCodesBlocked, this.blockingCurrency, this.currencyCodesBlocked, this.blockingStates,
                    this.stateCodesBlocked, this.blockingMCC,
                    this.mccBlocked, this.blockingPurchaseTypes,
                    this.purchaseTypesBlocked, this.blockingLimitTypes,
                    this.limitTypesBlocked, this.blockingBalanceTypes,
                    this.balanceTypesBlocked, this.blockingTransactionTypes,
                    this.transactionTypesBlocked, this.blockTerminalTypes,
                    this.terminalTypesBlocked, this.blockInstallments, this.blockCashBack, this.blockInternational,this.cardLimitMap);
        }


    }
}

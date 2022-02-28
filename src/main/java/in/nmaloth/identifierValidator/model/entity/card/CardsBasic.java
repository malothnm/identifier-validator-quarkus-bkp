package in.nmaloth.identifierValidator.model.entity.card;

import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.card.CardHolderType;
import in.nmaloth.payments.constants.card.CardStatus;
import in.nmaloth.payments.constants.card.LimitType;
import in.nmaloth.payments.constants.card.PeriodicType;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@MongoEntity(collection = "cards_basic")
public class CardsBasic extends ReactivePanacheMongoEntity {

    @BsonProperty("card_id")
    private String cardId;

    @BsonProperty("product")
    private Integer product;
    @BsonProperty("org")
    private Integer org;
    @BsonProperty("card_status")
    private String cardStatus;
    @BsonProperty("cardholder_type")
    private String cardholderType;
    @BsonProperty("block_type")
    private String blockType;
    @BsonProperty("waiver_days_activation")
    private Integer waiverDaysActivation;
    @BsonProperty("card_return")
    private Integer cardReturnNumber;
    @BsonProperty("ie_transaction_type")
    private String includeExcludeBlockTransactionType;
    @BsonProperty("ie_transaction_type_map")
    private Map<String, String> blockTransactionType;
    @BsonProperty("ie_terminal_type")
    private String includeExcludeBlockTerminal;
    @BsonProperty("ie_terminal_type_map")
    private Map<String, String> blockTerminalType;
    @BsonProperty("ie_limit_type")
    private String includeExcludeBlockLimitType;
    @BsonProperty("ie_limit_type_map")
    private Map<String, String> blockingLimitType;

    @BsonProperty("ie_purchase_type")
    private String includeExcludeBlockPurchaseType;
    @BsonProperty("ie_purchase_type_map")
    private Map<String, String> blockPurchaseTypes;
    @BsonProperty("block_cash_back")
    private Boolean blockCashBack;
    @BsonProperty("block_installment")
    private Boolean blockInstallments;
    @BsonProperty("block_international")
    private Boolean blockInternational;

    @BsonProperty("ie_entry_mode")
    private String includeExcludeBlockEntryMode;
    @BsonProperty("block_entry_mode_map")
    private Map<String, String> blockEntryMode;

    @BsonProperty("limits_last_updated")
    private LocalDateTime limitsLastUpdated;

    @BsonProperty("periodic_limit_map")
    private Map<String, Map<String, PeriodicCardAmount>> periodicTypePeriodicCardLimitMap;
    @BsonProperty("periodic_accum_map")
    private Map<String, Map<String, PeriodicCardAmount>> periodicCardAccumulatedValueMap;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof CardsBasic)) {
            return false;
        } else {
            CardsBasic that = (CardsBasic) o;
            return this.getCardId().equals(that.getCardId());
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getCardId()});
    }

    public static CardsBasic.CardsBasicBuilder builder() {
        return new CardsBasic.CardsBasicBuilder();
    }

    public static Uni<Optional<CardsBasic>> findByCardId(String cardId) {

        return find("card_id", cardId).firstResultOptional();
    }

    public Map<String, Map<String, PeriodicCardAmount>> getPeriodicTypePeriodicCardLimitMap() {
        return periodicTypePeriodicCardLimitMap;
    }

    public void setPeriodicTypePeriodicCardLimitMap(Map<String, Map<String, PeriodicCardAmount>> periodicTypePeriodicCardLimitMap) {
        this.periodicTypePeriodicCardLimitMap = periodicTypePeriodicCardLimitMap;
    }

    public Map<String, Map<String, PeriodicCardAmount>> getPeriodicCardAccumulatedValueMap() {
        return periodicCardAccumulatedValueMap;
    }

    public void setPeriodicCardAccumulatedValueMap(Map<String, Map<String, PeriodicCardAmount>> periodicCardAccumulatedValueMap) {
        this.periodicCardAccumulatedValueMap = periodicCardAccumulatedValueMap;
    }

    public CardsBasic(final String cardId, final Integer product, final Integer org,
                      final String cardStatus, final String cardholderType,
                      final String blockType, final Integer waiverDaysActivation,
                      final Integer cardReturnNumber,
                      final String includeExcludeBlockTransactionType,
                      final Map<String, String> blockTransactionType,
                      final String includeExcludeBlockTerminal,
                      final Map<String, String> blockTerminalType,
                      final String includeExcludeBlockLimitType,
                      final Map<String, String> blockingLimitType,
                      final String includeExcludeBlockPurchaseType,
                      final Map<String, String> blockPurchaseTypes,
                      final Boolean blockCashBack,
                      final Boolean blockInstallments,
                      final Boolean blockInternational,
                      final String includeExcludeBlockEntryMode,
                      final Map<String, String> blockEntryMode,
                      final LocalDateTime limitsLastUpdated,
                      final Map<String, Map<String, PeriodicCardAmount>> periodicTypePeriodicCardLimitMap,
                      final Map<String, Map<String, PeriodicCardAmount>> periodicCardAccumulatedValueMap


    ) {
        this.cardId = cardId;
        this.product = product;
        this.org = org;
        this.cardStatus = cardStatus;
        this.cardholderType = cardholderType;
        this.blockType = blockType;
        this.waiverDaysActivation = waiverDaysActivation;
        this.cardReturnNumber = cardReturnNumber;
        this.includeExcludeBlockTransactionType = includeExcludeBlockTransactionType;
        this.blockTransactionType = blockTransactionType;
        this.includeExcludeBlockTerminal = includeExcludeBlockTerminal;
        this.blockTerminalType = blockTerminalType;
        this.includeExcludeBlockLimitType = includeExcludeBlockLimitType;
        this.blockingLimitType = blockingLimitType;
        this.includeExcludeBlockPurchaseType = includeExcludeBlockPurchaseType;
        this.blockPurchaseTypes = blockPurchaseTypes;
        this.blockCashBack = blockCashBack;
        this.blockInstallments = blockInstallments;
        this.blockInternational = blockInternational;
        this.includeExcludeBlockEntryMode = includeExcludeBlockEntryMode;
        this.blockEntryMode = blockEntryMode;
        this.limitsLastUpdated = limitsLastUpdated;
        this.periodicTypePeriodicCardLimitMap = periodicTypePeriodicCardLimitMap;
        this.periodicCardAccumulatedValueMap = periodicCardAccumulatedValueMap;
    }

    public CardsBasic() {
    }

    public String getCardId() {
        return this.cardId;
    }

    public Integer getProduct() {
        return this.product;
    }

    public Integer getOrg() {
        return this.org;
    }

    public String getCardStatus() {
        return this.cardStatus;
    }

    public String getCardholderType() {
        return this.cardholderType;
    }

    public String getBlockType() {
        return this.blockType;
    }

    public Integer getWaiverDaysActivation() {
        return this.waiverDaysActivation;
    }

    public Integer getCardReturnNumber() {
        return this.cardReturnNumber;
    }

    public String getIncludeExcludeBlockTransactionType() {
        return this.includeExcludeBlockTransactionType;
    }

    public Map<String, String> getBlockTransactionType() {
        return this.blockTransactionType;
    }

    public String getIncludeExcludeBlockTerminal() {
        return this.includeExcludeBlockTerminal;
    }

    public Map<String, String> getBlockTerminalType() {
        return this.blockTerminalType;
    }

    public String getIncludeExcludeBlockLimitType() {
        return this.includeExcludeBlockLimitType;
    }

    public Map<String, String> getBlockingLimitType() {
        return this.blockingLimitType;
    }

    public String getIncludeExcludeBlockPurchaseType() {
        return this.includeExcludeBlockPurchaseType;
    }

    public Map<String, String> getBlockPurchaseTypes() {
        return this.blockPurchaseTypes;
    }

    public Boolean getBlockCashBack() {
        return this.blockCashBack;
    }

    public Boolean getBlockInstallments() {
        return this.blockInstallments;
    }

    public Boolean getBlockInternational() {
        return blockInternational;
    }

    public String getIncludeExcludeBlockEntryMode() {
        return this.includeExcludeBlockEntryMode;
    }

    public Map<String, String> getBlockEntryMode() {
        return this.blockEntryMode;
    }


    public void setCardId(final String cardId) {
        this.cardId = cardId;
    }

    public void setProduct(final Integer product) {
        this.product = product;
    }

    public void setOrg(final Integer org) {
        this.org = org;
    }

    public void setCardStatus(final String cardStatus) {
        this.cardStatus = cardStatus;
    }

    public void setCardholderType(final String cardholderType) {
        this.cardholderType = cardholderType;
    }

    public void setBlockType(final String blockType) {
        this.blockType = blockType;
    }

    public void setWaiverDaysActivation(final Integer waiverDaysActivation) {
        this.waiverDaysActivation = waiverDaysActivation;
    }

    public void setCardReturnNumber(final Integer cardReturnNumber) {
        this.cardReturnNumber = cardReturnNumber;
    }

    public void setIncludeExcludeBlockTransactionType(final String includeExcludeBlockTransactionType) {
        this.includeExcludeBlockTransactionType = includeExcludeBlockTransactionType;
    }

    public void setBlockTransactionType(final Map<String, String> blockTransactionType) {
        this.blockTransactionType = blockTransactionType;
    }

    public void setIncludeExcludeBlockTerminal(final String includeExcludeBlockTerminal) {
        this.includeExcludeBlockTerminal = includeExcludeBlockTerminal;
    }

    public void setBlockTerminalType(final Map<String, String> blockTerminalType) {
        this.blockTerminalType = blockTerminalType;
    }

    public void setIncludeExcludeBlockLimitType(final String includeExcludeBlockLimitType) {
        this.includeExcludeBlockLimitType = includeExcludeBlockLimitType;
    }

    public void setBlockingLimitType(final Map<String, String> blockingLimitType) {
        this.blockingLimitType = blockingLimitType;
    }

    public void setIncludeExcludeBlockPurchaseType(final String includeExcludeBlockPurchaseType) {
        this.includeExcludeBlockPurchaseType = includeExcludeBlockPurchaseType;
    }

    public void setBlockPurchaseTypes(final Map<String, String> blockPurchaseTypes) {
        this.blockPurchaseTypes = blockPurchaseTypes;
    }

    public void setBlockCashBack(final Boolean blockCashBack) {
        this.blockCashBack = blockCashBack;
    }

    public void setBlockInstallments(final Boolean blockInstallments) {
        this.blockInstallments = blockInstallments;
    }

    public void setBlockInternational(Boolean blockInternational) {
        this.blockInternational = blockInternational;
    }

    public void setIncludeExcludeBlockEntryMode(final String includeExcludeBlockEntryMode) {
        this.includeExcludeBlockEntryMode = includeExcludeBlockEntryMode;
    }


    public void setBlockEntryMode(final Map<String, String> blockEntryMode) {
        this.blockEntryMode = blockEntryMode;
    }


    public LocalDateTime getLimitsLastUpdated() {
        return limitsLastUpdated;
    }

    public void setLimitsLastUpdated(LocalDateTime limitsLastUpdated) {
        this.limitsLastUpdated = limitsLastUpdated;
    }

    public static class CardsBasicBuilder {
        private String cardId;
        private Integer product;
        private Integer org;
        private CardStatus cardStatus;
        private CardHolderType cardholderType;
        private BlockType blockType;
        private Integer waiverDaysActivation;
        private Integer cardReturnNumber;
        private IncludeExclude includeExcludeBlockTransactionType;
        private Map<String, String> blockTransactionType;
        private IncludeExclude includeExcludeBlockTerminal;
        private Map<String, String> blockTerminalType;
        private IncludeExclude includeExcludeBlockLimitType;
        private Map<String, String> blockingLimitType;
        private IncludeExclude includeExcludeBlockPurchaseType;
        private Map<String, String> blockPurchaseTypes;
        private Boolean blockCashBack;
        private Boolean blockInstallments;
        private Boolean blockInternational;
        private IncludeExclude includeExcludeBlockEntryMode;
        private Map<String, String> blockEntryMode;
        private LocalDateTime limitsLastUpdated;
        private Map<String, Map<String, PeriodicCardAmount>> periodicTypePeriodicCardLimitMap;
        private Map<String, Map<String, PeriodicCardAmount>> periodicCardAccumulatedValueMap;


        CardsBasicBuilder() {
        }

        public CardsBasic.CardsBasicBuilder periodicTypePeriodicCardLimitMap(final Map<PeriodicType, Map<LimitType, PeriodicCardAmount>> periodicTypePeriodicCardLimitMap) {


            Map<String, Map<String, PeriodicCardAmount>> periodicCardLimitMap = new HashMap<>();

            periodicTypePeriodicCardLimitMap.forEach((periodicType,limitMap) -> periodicCardLimitMap.put(periodicType.getPeriodicType(), createNewCardLimitMap(limitMap)));
            this.periodicTypePeriodicCardLimitMap = periodicCardLimitMap;


            return this;
        }

        public CardsBasic.CardsBasicBuilder periodicCardAccumulatedValueMap(final Map<PeriodicType, Map<LimitType, PeriodicCardAmount>> periodicCardAccumulatedValueMap) {

            Map<String, Map<String, PeriodicCardAmount>> periodicCardLAccumMap = new HashMap<>();

            periodicCardAccumulatedValueMap.forEach((periodicType,limitMap) -> periodicCardLAccumMap.put(periodicType.getPeriodicType(), createNewCardLimitMap(limitMap)));
            this.periodicCardAccumulatedValueMap = periodicCardLAccumMap;

            return this;
        }


        private Map<String, PeriodicCardAmount> createNewCardLimitMap(Map<LimitType, PeriodicCardAmount> limitMap) {

            Map<String, PeriodicCardAmount> periodicCardAmountMap = new HashMap<>();

            limitMap.forEach((limitType,cardAmountMap) -> periodicCardAmountMap.put(limitType.getLimitType(), cardAmountMap));
            return periodicCardAmountMap;

        }


        public CardsBasic.CardsBasicBuilder limitsLastUpdated(LocalDateTime limitsLastUpdated){
            this.limitsLastUpdated = limitsLastUpdated;
            return this;
        }

        public CardsBasic.CardsBasicBuilder cardId(final String cardId) {
            this.cardId = cardId;
            return this;
        }

        public CardsBasic.CardsBasicBuilder product(final Integer product) {
            this.product = product;
            return this;
        }

        public CardsBasic.CardsBasicBuilder org(final Integer org) {
            this.org = org;
            return this;
        }

        public CardsBasic.CardsBasicBuilder cardStatus(final CardStatus cardStatus) {
            this.cardStatus = cardStatus;
            return this;
        }

        public CardsBasic.CardsBasicBuilder cardholderType(final CardHolderType cardholderType) {
            this.cardholderType = cardholderType;
            return this;
        }

        public CardsBasic.CardsBasicBuilder blockType(final BlockType blockType) {
            this.blockType = blockType;
            return this;
        }

        public CardsBasic.CardsBasicBuilder waiverDaysActivation(final Integer waiverDaysActivation) {
            this.waiverDaysActivation = waiverDaysActivation;
            return this;
        }

        public CardsBasic.CardsBasicBuilder cardReturnNumber(final Integer cardReturnNumber) {
            this.cardReturnNumber = cardReturnNumber;
            return this;
        }

        public CardsBasic.CardsBasicBuilder includeExcludeBlockTransactionType(final IncludeExclude includeExcludeBlockTransactionType) {
            this.includeExcludeBlockTransactionType = includeExcludeBlockTransactionType;
            return this;
        }

        public CardsBasic.CardsBasicBuilder blockTransactionType(final Map<TransactionType, InternationalApplied> blockTransactionType) {
            this.blockTransactionType = new HashMap<>();
            blockTransactionType.entrySet()
                    .forEach(entry -> this.blockTransactionType.put(entry.getKey().getTransactionType(), entry.getValue().getBlockInternational()));
            return this;
        }

        public CardsBasic.CardsBasicBuilder includeExcludeBlockTerminal(final IncludeExclude includeExcludeBlockTerminal) {
            this.includeExcludeBlockTerminal = includeExcludeBlockTerminal;
            return this;
        }

        public CardsBasic.CardsBasicBuilder blockTerminalType(final Map<TerminalType, InternationalApplied> blockTerminalType) {

            this.blockTerminalType = new HashMap<>();
            blockTerminalType.entrySet().forEach(entry -> this.blockTerminalType.put(entry.getKey().getTerminalType(), entry.getValue().getBlockInternational()));
            return this;
        }

        public CardsBasic.CardsBasicBuilder includeExcludeBlockLimitType(final IncludeExclude includeExcludeBlockLimitType) {
            this.includeExcludeBlockLimitType = includeExcludeBlockLimitType;
            return this;
        }

        public CardsBasic.CardsBasicBuilder blockingLimitType(final Map<LimitType, InternationalApplied> blockingLimitType) {
            this.blockingLimitType = new HashMap<>();
            blockingLimitType.entrySet().forEach(entry -> this.blockingLimitType.put(entry.getKey().getLimitType(), entry.getValue().getBlockInternational()));
            return this;
        }

        public CardsBasic.CardsBasicBuilder includeExcludeBlockPurchaseType(final IncludeExclude includeExcludeBlockPurchaseType) {
            this.includeExcludeBlockPurchaseType = includeExcludeBlockPurchaseType;
            return this;
        }

        public CardsBasic.CardsBasicBuilder blockPurchaseTypes(final Map<PurchaseTypes, InternationalApplied> blockPurchaseTypes) {
            this.blockPurchaseTypes = new HashMap<>();
            blockPurchaseTypes.entrySet().forEach(entry -> this.blockPurchaseTypes.put(entry.getKey().getPurchaseTypes(), entry.getValue().getBlockInternational()));
            return this;
        }

        public CardsBasic.CardsBasicBuilder blockCashBack(final Boolean blockCashBack) {
            this.blockCashBack = blockCashBack;
            return this;
        }

        public CardsBasic.CardsBasicBuilder blockInstallments(final Boolean blockInstallments) {
            this.blockInstallments = blockInstallments;
            return this;
        }

        public CardsBasic.CardsBasicBuilder blockInternational(final Boolean blockInternational) {
            this.blockInternational = blockInternational;
            return this;
        }


        public CardsBasic.CardsBasicBuilder includeExcludeBlockEntryMode(final IncludeExclude includeExcludeBlockEntryMode) {
            this.includeExcludeBlockEntryMode = includeExcludeBlockEntryMode;
            return this;
        }

        public CardsBasic.CardsBasicBuilder blockEntryMode(final Map<EntryMode, InternationalApplied> blockEntryMode) {
            this.blockEntryMode = new HashMap<>();
            blockEntryMode.entrySet().forEach(entry -> this.blockEntryMode.put(entry.getKey().getEntryMode(), entry.getValue().getBlockInternational()));
            return this;
        }


        public CardsBasic build() {
            return new CardsBasic(this.cardId, this.product, this.org, this.cardStatus.getCardStatus(), this.cardholderType.getCardHolderType(), this.blockType.getBlockType(),
                    this.waiverDaysActivation, this.cardReturnNumber,
                    this.includeExcludeBlockTransactionType.getIncludeExclude(),
                    this.blockTransactionType, this.includeExcludeBlockTerminal.getIncludeExclude(),
                    this.blockTerminalType, this.includeExcludeBlockLimitType.getIncludeExclude(),
                    this.blockingLimitType, this.includeExcludeBlockPurchaseType.getIncludeExclude(),
                    this.blockPurchaseTypes, this.blockCashBack, this.blockInstallments,this.blockInternational,
                    this.includeExcludeBlockEntryMode.getIncludeExclude(),
                    this.blockEntryMode, this.limitsLastUpdated,
                    this.periodicTypePeriodicCardLimitMap,
                    this.periodicCardAccumulatedValueMap);
        }

    }
}

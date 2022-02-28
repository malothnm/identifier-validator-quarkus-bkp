package in.nmaloth.identifierValidator.services.card;

import in.nmaloth.identifierValidator.model.ProductCriteria;
import in.nmaloth.identifierValidator.model.entity.card.CacheTempAccum;
import in.nmaloth.identifierValidator.model.entity.card.CardsBasic;
import in.nmaloth.identifierValidator.model.entity.card.PeriodicCardAmount;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.card.CardStatus;
import in.nmaloth.payments.constants.card.PeriodicType;
import in.nmaloth.payments.constants.ids.ServiceID;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;

@ApplicationScoped
public class CardsServiceImpl implements CardsService {


    @Inject
    CardTempService cardTempService;


    @Override
    public Optional<ServiceResponse> validateCardStatus(CardStatus cardStatus) {
        if (cardStatus.equals(CardStatus.ACTIVE) || (cardStatus.equals(CardStatus.INACTIVE))) {
            return Optional.empty();
        } else {
            return Optional.of(ServiceResponse.STATUS);
        }
    }

    @Override
    public Optional<ServiceResponse> validateCardBlock(BlockType blockType) {

        switch (blockType) {
            case APPROVE:
            case VIP_ALWAYS_APPROVE: {
                return Optional.empty();
            }
            case BLOCK_TEMP: {
                return Optional.of(ServiceResponse.TEMP_BLK);
            }
            case BLOCK_SUSPECTED_FRAUD: {
                return Optional.of(ServiceResponse.SUSPECT_FRAUD);
            }
            case BLOCK_FRAUD: {
                return Optional.of(ServiceResponse.FRAUD);
            }
            case BLOCK_PICKUP: {
                return Optional.of(ServiceResponse.PICK_UP);
            }
            default: {
                return Optional.of(ServiceResponse.BLK);
            }
        }
    }


    @Override
    public Optional<ServiceResponse> checkForBlockTransactionType(String includeExclude, Map<String, String> blockTransactionType, String txnType, International international) {
        if (blockTransactionType == null) {
            return Optional.empty();

        } else {
            String internationalApplied = blockTransactionType.get(txnType);

            return validateResponse(internationalApplied, includeExclude, international, ServiceResponse.TRANSACTION_TYPE);

        }
    }

    @Override
    public Optional<ServiceResponse> checkForBlockTerminalType(String includeExclude, Map<String, String> blockTerminalType, String terminalType, International international) {

        if (blockTerminalType == null) {
            return Optional.empty();
        } else {
            String internationalApplied = blockTerminalType.get(terminalType);
            return validateResponse(internationalApplied, includeExclude, international, ServiceResponse.TERMINAL_TYPE);

        }
    }

    @Override
    public Optional<ServiceResponse> checkForBlockPurchaseType(String includeExclude, Map<String, String> blockPurchaseType, String purchaseType, International international) {

        if (blockPurchaseType == null) {
            return Optional.empty();
        } else {
            String internationalApplied = blockPurchaseType.get(purchaseType);
            return validateResponse(internationalApplied, includeExclude, international, ServiceResponse.PURCHASE_TYPE);

        }
    }

    @Override
    public Optional<ServiceResponse> checkForBlockEntryMode(String includeExclude, Map<String, String> blockEntryMode, String entryMode, International international) {

        if (blockEntryMode == null) {
            return Optional.empty();
        } else {
            String internationalApplied = blockEntryMode.get(entryMode);
            return validateResponse(internationalApplied, includeExclude, international, ServiceResponse.ENTRY_MODE);

        }
    }

    @Override
    public Optional<ServiceResponse> checkForBlockLimitType(String includeExclude, Map<String, String> blockLimitTypes, List<String> limitTypes, International international) {

        if (blockLimitTypes != null) {
            for (String limitTYpe : limitTypes) {
                String internationalApplied = blockLimitTypes.get(limitTYpe);
                Optional<ServiceResponse> serviceResponseOptional = validateResponse(internationalApplied, includeExclude, international, ServiceResponse.CARD_LIMIT);
                if (serviceResponseOptional.isPresent()) {
                    return serviceResponseOptional;
                }
            }

        }
        return Optional.empty();
    }

    @Override
    public Optional<ServiceResponse> checkForBlockCashBack(Boolean blockCashBack, String cashBack) {

        if (blockCashBack == null) {
            return Optional.empty();
        }

        if (blockCashBack) {
            if (cashBack.equals(CashBack.CASH_BACK_PRESENT.getCashBack())) {
                return Optional.of(ServiceResponse.CASH_BACK);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ServiceResponse> checkForInstallment(Boolean blockInstallment, String installment) {

        if (blockInstallment == null) {
            return Optional.empty();
        }

        if (blockInstallment) {
            if (installment.equals(InstallmentType.INSTALLMENT_TYPE.getInstallmentType())) {
                return Optional.of(ServiceResponse.INSTALLMENT);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ServiceResponse> checkForInternational(Boolean blockInternational, International international) {


        if (blockInternational == null) {

            return Optional.empty();
        }

        if (blockInternational) {
            if (international.equals(International.INTERNATIONAL)) {
                return Optional.of(ServiceResponse.INTERNATIONAL);
            }
        }
        return Optional.empty();
    }

    @Override
    public Uni<Optional<ServiceResponse>> validateCardLimits(CardsBasic cardsBasic, IdentifierValidator identifierValidator, ProductCriteria productCriteria, long convertAmount) {

        if (cardsBasic.getLimitsLastUpdated() == null) {
            cardsBasic.setLimitsLastUpdated(LocalDateTime.now());
        }

        boolean initializeDaily = false;
        boolean initializeMonth = false;
        boolean initializeYear = false;

        LocalDateTime lastUpdatedDateTime = cardsBasic.getLimitsLastUpdated();
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (lastUpdatedDateTime.getYear() == currentDateTime.getYear()) {
            if (lastUpdatedDateTime.getMonthValue() == currentDateTime.getMonthValue()) {
                if (lastUpdatedDateTime.getDayOfMonth() == currentDateTime.getDayOfMonth()) {
                } else {
                    initializeDaily = true;
                }
            } else {
                initializeMonth = true;
                initializeDaily = true;
            }

        } else {
            initializeYear = true;
            initializeMonth = true;
            initializeDaily = true;

        }

        boolean finalInitializeDaily = initializeDaily;
        boolean finalInitializeMonth = initializeMonth;
        boolean finalInitializeYear = initializeYear;

        if (cardsBasic.getPeriodicTypePeriodicCardLimitMap() == null) {
            return Uni.createFrom().item(Optional.empty());
        }

        if (cardsBasic.getPeriodicCardAccumulatedValueMap() == null) {

            cardsBasic.setPeriodicTypePeriodicCardLimitMap(new HashMap<>());
            cardsBasic.getPeriodicTypePeriodicCardLimitMap().forEach((periodicType, limitmap) -> {
                Map<String, PeriodicCardAmount> limitmapAccum = new HashMap<>();
                limitmap.forEach((limitType, cardAmount) -> limitmapAccum
                        .put(limitType,
                                PeriodicCardAmount.builder().limitType(limitType).transactionAmount(0L).transactionNumber(0).build()));
                cardsBasic.getPeriodicCardAccumulatedValueMap().put(periodicType, limitmapAccum);

            });
        }

        return cardTempService.getAllCardTempBalance(identifierValidator, convertAmount)
                .onItem().transform(cacheTempAccumMap -> evaluateAllLimits(productCriteria.getCardLimitMap(), cardsBasic.getPeriodicTypePeriodicCardLimitMap(), cardsBasic.getPeriodicCardAccumulatedValueMap(),
                        identifierValidator, cacheTempAccumMap, finalInitializeDaily, finalInitializeMonth, finalInitializeYear, convertAmount));


    }

    @Override
    public Uni<ValidationResponse> validateCards(IdentifierValidator identifierValidator, ProductCriteria productCriteria, long convertAmount) {

        return CardsBasic.findByCardId(identifierValidator.getCardNumber())
                .onItem().transformToUni(cardsBasicOptional -> validateCardsAndLimts(cardsBasicOptional, identifierValidator, productCriteria, convertAmount))
                .onItem().transform(responseList -> {

                            if (responseList.size() == 0) {
                                responseList.add(ServiceResponse.OK.getServiceResponse());
                            }
                            return ValidationResponse.newBuilder()
                                    .setServiceId(ServiceID.CARD_VALIDATOR)
                                    .addAllValidationResponse(responseList)
                                    .build();
                        }
                )

                ;
    }

    private Uni<List<String>> validateCardsAndLimts(Optional<CardsBasic> cardsBasicOptional, IdentifierValidator identifierValidator, ProductCriteria productCriteria, long convertAmount) {

        List<String> responseList = new ArrayList<>();

        if (cardsBasicOptional.isEmpty()) {
            responseList.add(ServiceResponse.NO_ENTRY.getServiceResponse());
            return Uni.createFrom().item(responseList);
        }

        CardsBasic cardsBasic = cardsBasicOptional.get();

        International international = International.identify(identifierValidator.getInternational());

        validateCardStatus(CardStatus.identify(cardsBasic.getCardStatus())).ifPresent(serviceResponse -> responseList.add(serviceResponse.getServiceResponse()));

        validateCardBlock(BlockType.identify(cardsBasic.getBlockType())).ifPresent(serviceResponse -> responseList.add(serviceResponse.getServiceResponse()));

        checkForBlockTransactionType(cardsBasic.getIncludeExcludeBlockTransactionType(), cardsBasic.getBlockTransactionType(), identifierValidator.getTransactionType(), international)
                .ifPresent(serviceResponse -> responseList.add(serviceResponse.getServiceResponse()));


        checkForBlockTerminalType(cardsBasic.getIncludeExcludeBlockTerminal(), cardsBasic.getBlockTerminalType(), identifierValidator.getTerminalType(), international)
                .ifPresent(serviceResponse -> responseList.add(serviceResponse.getServiceResponse()));


        checkForBlockPurchaseType(cardsBasic.getIncludeExcludeBlockPurchaseType(), cardsBasic.getBlockPurchaseTypes(), identifierValidator.getPurchaseTypes(), international)
                .ifPresent(serviceResponse -> responseList.add(serviceResponse.getServiceResponse()));


        checkForBlockEntryMode(cardsBasic.getIncludeExcludeBlockEntryMode(), cardsBasic.getBlockEntryMode(), identifierValidator.getEntryMode(), international)
                .ifPresent(serviceResponse -> responseList.add(serviceResponse.getServiceResponse()));


        checkForBlockLimitType(cardsBasic.getIncludeExcludeBlockLimitType(), cardsBasic.getBlockingLimitType(), identifierValidator.getLimitTypesList(), international)
                .ifPresent(serviceResponse -> responseList.add(serviceResponse.getServiceResponse()));


        checkForBlockCashBack(cardsBasic.getBlockCashBack(), identifierValidator.getCashBack())
                .ifPresent(serviceResponse -> responseList.add(serviceResponse.getServiceResponse()));

        checkForInstallment(cardsBasic.getBlockInstallments(), identifierValidator.getInstallmentType())
                .ifPresent(serviceResponse -> responseList.add(serviceResponse.getServiceResponse()));

        checkForInternational(cardsBasic.getBlockInternational(), international)
                .ifPresent(serviceResponse -> responseList.add(serviceResponse.getServiceResponse()));


        return validateCardLimits(cardsBasic, identifierValidator, productCriteria, convertAmount)
                .onItem().transform(serviceResponseOptional -> {

                    serviceResponseOptional.ifPresent(serviceResponse -> responseList.add(serviceResponse.getServiceResponse()));
                    return responseList;
                });

    }


    private Optional<ServiceResponse> validateResponse(String internationalApplied,
                                                       String includeExclude,
                                                       International international,
                                                       ServiceResponse serviceResponse) {

        if (internationalApplied == null) {
            if (includeExclude.equals(IncludeExclude.INCLUDE.getIncludeExclude())) {
                return Optional.of(serviceResponse);
            }

        } else {
            if (internationalApplied.equals(InternationalApplied.BOTH.getBlockInternational())) {
                if (includeExclude.equals(IncludeExclude.EXCLUDE.getIncludeExclude())) {
                    return Optional.of(serviceResponse);
                }
            } else if (internationalApplied.equals(InternationalApplied.INTERNATIONAL.getBlockInternational())) {
                if (international.equals(International.INTERNATIONAL)) {
                    if (includeExclude.equals(IncludeExclude.EXCLUDE.getIncludeExclude())) {
                        return Optional.of(serviceResponse);
                    }
                } else {
                    if (includeExclude.equals(IncludeExclude.INCLUDE.getIncludeExclude())) {
                        return Optional.of(serviceResponse);
                    }
                }
            } else {
                if (international.equals(International.DOMESTIC)) {
                    if (includeExclude.equals(IncludeExclude.EXCLUDE.getIncludeExclude())) {
                        return Optional.of(serviceResponse);
                    }
                } else {
                    if (includeExclude.equals(IncludeExclude.INCLUDE.getIncludeExclude())) {
                        return Optional.of(serviceResponse);
                    }
                }

            }
        }
        return Optional.empty();

    }


    private Optional<ServiceResponse> evaluateAllLimits(Map<String, Map<String, PeriodicCardAmount>> productLimitMap,
                                                        Map<String, Map<String, PeriodicCardAmount>> periodicTypePeriodicCardLimitMap,
                                                        Map<String, Map<String, PeriodicCardAmount>> periodicCardAccumulatedValueMap,
                                                        IdentifierValidator identifierValidator, Map<String, CacheTempAccum> cacheCardTempAccumMap, boolean initializeDaily,
                                                        boolean initializeMonth, boolean initializeYear, long convertAmount) {

        if (periodicCardAccumulatedValueMap == null) {
            periodicCardAccumulatedValueMap = new HashMap<>();
        }

        if (periodicTypePeriodicCardLimitMap == null) {
            periodicTypePeriodicCardLimitMap = new HashMap<>();
        }

        if (productLimitMap == null) {
            productLimitMap = new HashMap<>();
        }


//        if(productLimitMap.get(PeriodicType.SINGLE.getPeriodicType()) == null){
//            productLimitMap.put(PeriodicType.SINGLE.getPeriodicType(),new HashMap<>());
//        }
//
//        if(periodicTypePeriodicCardLimitMap.get(PeriodicType.SINGLE.getPeriodicType()) == null){
//            periodicTypePeriodicCardLimitMap.put(PeriodicType.SINGLE.getPeriodicType(),new HashMap<>());
//        }

        boolean checkResult = evaluatePeriodicLimitSingle(productLimitMap.get(PeriodicType.SINGLE.getPeriodicType()),
                periodicTypePeriodicCardLimitMap.get(PeriodicType.SINGLE.getPeriodicType()),
                identifierValidator, convertAmount);

        if (checkResult) {
            return Optional.of(ServiceResponse.CARD_LIMIT);
        }

//        if(productLimitMap.get(PeriodicType.DAILY.getPeriodicType()) == null){
//            productLimitMap.put(PeriodicType.DAILY.getPeriodicType(),new HashMap<>());
//        }
//
//        if(periodicTypePeriodicCardLimitMap.get(PeriodicType.DAILY.getPeriodicType()) == null){
//            periodicTypePeriodicCardLimitMap.put(PeriodicType.DAILY.getPeriodicType(),new HashMap<>());
//            periodicCardAccumulatedValueMap.put(PeriodicType.DAILY.getPeriodicType(),new HashMap<>());
//
//        }

        checkResult = evaluatePeriodicLimit(productLimitMap.get(PeriodicType.DAILY.getPeriodicType()),
                periodicTypePeriodicCardLimitMap.get(PeriodicType.DAILY.getPeriodicType()),
                periodicCardAccumulatedValueMap.get(PeriodicType.DAILY.getPeriodicType()),
                identifierValidator, cacheCardTempAccumMap, initializeDaily);

        if (checkResult) {
            return Optional.of(ServiceResponse.CARD_LIMIT);
        }

//        if(productLimitMap.get(PeriodicType.MONTHLY.getPeriodicType()) == null){
//            productLimitMap.put(PeriodicType.MONTHLY.getPeriodicType(),new HashMap<>());
//        }
//
//        if(periodicTypePeriodicCardLimitMap.get(PeriodicType.MONTHLY.getPeriodicType()) == null){
//            periodicTypePeriodicCardLimitMap.put(PeriodicType.MONTHLY.getPeriodicType(),new HashMap<>());
//            periodicCardAccumulatedValueMap.put(PeriodicType.MONTHLY.getPeriodicType(),new HashMap<>());
//
//        }

        checkResult = evaluatePeriodicLimit(productLimitMap.get(PeriodicType.MONTHLY.getPeriodicType()),
                periodicTypePeriodicCardLimitMap.get(PeriodicType.MONTHLY.getPeriodicType()),
                periodicCardAccumulatedValueMap.get(PeriodicType.MONTHLY.getPeriodicType()),
                identifierValidator, cacheCardTempAccumMap, initializeMonth);

        if (checkResult) {
            return Optional.of(ServiceResponse.CARD_LIMIT);
        }


//        if(productLimitMap.get(PeriodicType.YEARLY.getPeriodicType()) == null){
//            productLimitMap.put(PeriodicType.YEARLY.getPeriodicType(),new HashMap<>());
//        }
//
//        if(periodicTypePeriodicCardLimitMap.get(PeriodicType.YEARLY.getPeriodicType()) == null){
//            periodicTypePeriodicCardLimitMap.put(PeriodicType.YEARLY.getPeriodicType(),new HashMap<>());
//            periodicCardAccumulatedValueMap.put(PeriodicType.YEARLY.getPeriodicType(),new HashMap<>());
//
//        }

        checkResult = evaluatePeriodicLimit(productLimitMap.get(PeriodicType.YEARLY.getPeriodicType()),
                periodicTypePeriodicCardLimitMap.get(PeriodicType.YEARLY.getPeriodicType()),
                periodicCardAccumulatedValueMap.get(PeriodicType.YEARLY.getPeriodicType()),
                identifierValidator, cacheCardTempAccumMap, initializeYear);


        if (checkResult) {
            return Optional.of(ServiceResponse.CARD_LIMIT);
        }

        return Optional.empty();

    }

    private boolean evaluatePeriodicLimitSingle(Map<String, PeriodicCardAmount> productLimitMap,
                                                Map<String, PeriodicCardAmount> cardLimitMap,
                                                IdentifierValidator identifierValidator, long convertAmount) {

        for (int i = 0; i < identifierValidator.getLimitTypesCount(); i++) {

            String limitType = identifierValidator.getLimitTypes(i);

            PeriodicCardAmount productPeriodicCardAmount;
            PeriodicCardAmount cardLimitPeriodic;


            if (productLimitMap == null) {
                productPeriodicCardAmount = null;
            } else {
                productPeriodicCardAmount = productLimitMap.get(limitType);
            }

            if (cardLimitMap == null) {
                cardLimitPeriodic = null;
            } else {
                cardLimitPeriodic = cardLimitMap.get(limitType);
            }


            if (cardLimitPeriodic == null && productPeriodicCardAmount == null) {

            } else {


                if (productPeriodicCardAmount != null) {

                    if (productPeriodicCardAmount.getTransactionAmount() < convertAmount) {
                        return true;
                    }
                }

                if (cardLimitPeriodic != null) {

                    if (cardLimitPeriodic.getTransactionAmount() < convertAmount) {
                        return true;
                    }
                }
            }

        }


        return false;
    }


    private boolean evaluatePeriodicLimit(Map<String, PeriodicCardAmount> productLimitMap,
                                          Map<String, PeriodicCardAmount> cardLimitMap,
                                          Map<String, PeriodicCardAmount> periodicCardAmountMap,
                                          IdentifierValidator identifierValidator,
                                          Map<String, CacheTempAccum> cacheCardTempAccumMap, boolean initialize) {


        for (int i = 0; i < identifierValidator.getLimitTypesCount(); i++) {

            String limitType = identifierValidator.getLimitTypes(i);

            PeriodicCardAmount cardAccumPeriodicCardAmount;
            CacheTempAccum cacheCardTempAccum;
            PeriodicCardAmount productPeriodicCardAmount;
            PeriodicCardAmount cardLimitPeriodic;


            if (periodicCardAmountMap == null) {
                cardAccumPeriodicCardAmount = null;
            } else {
                cardAccumPeriodicCardAmount = periodicCardAmountMap.get(limitType);
            }

            if (cacheCardTempAccumMap == null) {
                cacheCardTempAccum = null;
            } else {
                cacheCardTempAccum = cacheCardTempAccumMap.get(limitType);
            }

            if (productLimitMap == null) {
                productPeriodicCardAmount = null;
            } else {
                productPeriodicCardAmount = productLimitMap.get(limitType);
            }

            if (cardLimitMap == null) {
                cardLimitPeriodic = null;
            } else {
                cardLimitPeriodic = cardLimitMap.get(limitType);
            }


            if (cardLimitPeriodic == null && productPeriodicCardAmount == null) {

            } else {

                int transactionCount = 0;
                long transactionAmount = 0;


                if (cacheCardTempAccum != null) {
                    transactionCount = cacheCardTempAccum.getAccumCount() + transactionCount;
                    transactionAmount = transactionAmount + cacheCardTempAccum.getAccumAmount();
                }


                if (cardAccumPeriodicCardAmount != null && !initialize) {
                    transactionAmount = transactionAmount + cardAccumPeriodicCardAmount.getTransactionAmount();
                    transactionCount = transactionCount + cardAccumPeriodicCardAmount.getTransactionNumber();
                }

                if (productPeriodicCardAmount != null) {
                    if (productPeriodicCardAmount.getTransactionNumber() < transactionCount) {
                        return true;
                    }
                    if (productPeriodicCardAmount.getTransactionAmount() < transactionAmount) {
                        return true;
                    }
                }

                if (cardLimitPeriodic != null) {
                    if (cardLimitPeriodic.getTransactionNumber() < transactionCount) {
                        return true;
                    }
                    if (cardLimitPeriodic.getTransactionAmount() < transactionAmount) {
                        return true;
                    }
                }
            }

        }


        return false;
    }


}

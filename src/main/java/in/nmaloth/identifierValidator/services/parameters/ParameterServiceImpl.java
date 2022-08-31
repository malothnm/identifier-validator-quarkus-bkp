package in.nmaloth.identifierValidator.services.parameters;

import in.nmaloth.identifierValidator.model.ProductCriteria;
import in.nmaloth.identifierValidator.model.ProductCriteriaKey;
import in.nmaloth.identifierValidator.model.entity.product.MCCRange;
import in.nmaloth.identifierValidator.model.entity.product.ProductAuthCriteria;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.account.BalanceTypes;
import in.nmaloth.payments.constants.card.LimitType;
import in.nmaloth.payments.constants.products.*;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.*;

@ApplicationScoped
public class ParameterServiceImpl implements ParameterService {

    private final Logger logger = LoggerFactory.getLogger(ParameterServiceImpl.class);

    private final Map<ProductCriteriaKey, ProductCriteria> productCriteriaMap = new HashMap<>();


    public void startup(@Observes StartupEvent startupEvent) {

        loadAllAuthProductCriteria();

        logger.info(" ###############All products are loaded");

    }

    @Override
    public void loadAllAuthProductCriteria() {

        Uni<List<ProductAuthCriteria>> criteriaListUni = ProductAuthCriteria.listAll();

        criteriaListUni.await().indefinitely()
                .forEach(productAuthCriteria -> productCriteriaMap.put(
                        ProductCriteriaKey.builder()
                                .org(productAuthCriteria.getOrg())
                                .criteria(productAuthCriteria.getCriteria())
                                .product(productAuthCriteria.getProduct())
                                .build(), convertProductCriteria(productAuthCriteria)
                ));

    }

    @Override
    public Optional<ServiceResponse> blockingCountryCode(ProductCriteria productCriteria, String countryCode) {
        IncludeExclude includeExclude = productCriteria.getBlockingCountries();
        if (includeExclude.equals(IncludeExclude.NOT_APPLICABLE)) {
            return Optional.empty();
        }

        int index = Arrays.binarySearch(productCriteria.getCountryCodesBlocked(), countryCode);

        if (index < 0) {
            if (includeExclude.equals(IncludeExclude.INCLUDE)) {
                return Optional.of(ServiceResponse.BLOCKED_COUNTRY);
            }
        } else {
            if (includeExclude.equals(IncludeExclude.EXCLUDE)) {
                return Optional.of(ServiceResponse.BLOCKED_COUNTRY);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<ServiceResponse> blockingCurrencyCode(ProductCriteria productCriteria, String currencyCode) {
        IncludeExclude includeExclude = productCriteria.getBlockingCurrency();
        if (includeExclude.equals(IncludeExclude.NOT_APPLICABLE)) {
            return Optional.empty();
        }

        int index = Arrays.binarySearch(productCriteria.getCurrencyCodesBlocked(), currencyCode);

        if (index < 0) {
            if (includeExclude.equals(IncludeExclude.INCLUDE)) {
                return Optional.of(ServiceResponse.BLOCKED_CURRENCY);
            }
        } else {
            if (includeExclude.equals(IncludeExclude.EXCLUDE)) {
                return Optional.of(ServiceResponse.BLOCKED_CURRENCY);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<ServiceResponse> blockingMCC(ProductCriteria productCriteria, Integer mcc) {
        IncludeExclude includeExclude = productCriteria.getBlockingMCC();
        if (includeExclude.equals(IncludeExclude.NOT_APPLICABLE)) {
            return Optional.empty();
        }

        MCCRange mccRange = MCCRange.builder().mccStart(mcc).build();
        int index = Arrays.binarySearch(productCriteria.getMccBlocked(), mccRange);

        if (index < 0) {
            if (includeExclude.equals(IncludeExclude.INCLUDE)) {
                return Optional.of(ServiceResponse.BLOCKED_MCC);
            }
        } else {
            if (includeExclude.equals(IncludeExclude.EXCLUDE)) {
                return Optional.of(ServiceResponse.BLOCKED_MCC);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<ServiceResponse> blockingPurchaseTypes(ProductCriteria productCriteria, PurchaseTypes purchaseTypes, International international) {

        IncludeExclude includeExclude = productCriteria.getBlockingPurchaseTypes();
        if (includeExclude.equals(IncludeExclude.NOT_APPLICABLE)) {
            return Optional.empty();
        }

        if (purchaseTypes == null) {
            return Optional.empty();
        }
        BlockingPurchaseType blockingPurchaseType = new BlockingPurchaseType();
        blockingPurchaseType.setPurchaseTypes(purchaseTypes.getPurchaseTypes());

        return validateBlockingPurchaseType(productCriteria.getPurchaseTypesBlocked(), blockingPurchaseType,
                includeExclude, international);
    }

    private Optional<ServiceResponse> validateBlockingPurchaseType(BlockingPurchaseType[] purchaseTypesBlocked, BlockingPurchaseType blockingPurchaseType,
                                                                   IncludeExclude includeExclude, International international) {

        int index = Arrays.binarySearch(purchaseTypesBlocked, blockingPurchaseType);

        if (index < 0) {
            if (includeExclude.equals(IncludeExclude.INCLUDE)) {
                return Optional.of(ServiceResponse.BLOCKED_PURCHASE_TYPE);
            }
        } else {
            InternationalApplied internationalApplied;
            if (international.equals(International.INTERNATIONAL)) {
                internationalApplied = InternationalApplied.INTERNATIONAL;
            } else {
                internationalApplied = InternationalApplied.DOMESTIC;
            }
            blockingPurchaseType = purchaseTypesBlocked[index];
            if (blockingPurchaseType.getInternationalApplied().equals(InternationalApplied.BOTH.getBlockInternational())) {
                if (includeExclude.equals(IncludeExclude.EXCLUDE)) {
                    return Optional.of(ServiceResponse.BLOCKED_PURCHASE_TYPE);
                }
            } else if (blockingPurchaseType.getInternationalApplied().equals(internationalApplied.getBlockInternational())) {
                if (includeExclude.equals(IncludeExclude.EXCLUDE)) {
                    return Optional.of(ServiceResponse.BLOCKED_PURCHASE_TYPE);
                }
            } else {
                if (includeExclude.equals(IncludeExclude.INCLUDE)) {
                    return Optional.of(ServiceResponse.BLOCKED_PURCHASE_TYPE);
                }
            }
        }
        return Optional.empty();

    }

    @Override
    public Optional<ServiceResponse> blockingLimitTypes(ProductCriteria productCriteria, LimitType[] limitTypes, International international) {
        IncludeExclude includeExclude = productCriteria.getBlockingLimitTypes();
        if (includeExclude.equals(IncludeExclude.NOT_APPLICABLE)) {
            return Optional.empty();
        }

        if (limitTypes == null) {
            return Optional.empty();
        }
        for (LimitType limitType : limitTypes) {
            BlockingLimitType blockingLimitType = new BlockingLimitType();
            blockingLimitType.setLimitType(limitType.getLimitType());
            Optional<ServiceResponse> serviceResponseOptional =
                    validateBlockingLimit(productCriteria.getLimitTypesBlocked(), blockingLimitType,
                            includeExclude, international);
            if (serviceResponseOptional.isPresent()) {
                return serviceResponseOptional;
            }
        }


        return Optional.empty();
    }


    private Optional<ServiceResponse> validateBlockingLimit(BlockingLimitType[] blockingLimitTypes,
                                                            BlockingLimitType blockingLimitType,
                                                            IncludeExclude includeExclude, International international) {
        int index = Arrays.binarySearch(blockingLimitTypes, blockingLimitType);

        if (index < 0) {
            if (includeExclude.equals(IncludeExclude.INCLUDE)) {
                return Optional.of(ServiceResponse.BLOCKED_LIMIT_TYPE);
            }
        } else {
            InternationalApplied internationalApplied;
            if (international.equals(International.INTERNATIONAL)) {
                internationalApplied = InternationalApplied.INTERNATIONAL;
            } else {
                internationalApplied = InternationalApplied.DOMESTIC;
            }
            blockingLimitType = blockingLimitTypes[index];
            if (blockingLimitType.getInternationalApplied().equals(InternationalApplied.BOTH.getBlockInternational())) {
                if (includeExclude.equals(IncludeExclude.EXCLUDE)) {
                    return Optional.of(ServiceResponse.BLOCKED_LIMIT_TYPE);
                }
            } else if (blockingLimitType.getInternationalApplied().equals(internationalApplied.getBlockInternational())) {
                if (includeExclude.equals(IncludeExclude.EXCLUDE)) {
                    return Optional.of(ServiceResponse.BLOCKED_LIMIT_TYPE);
                }
            } else {
                if (includeExclude.equals(IncludeExclude.INCLUDE)) {
                    return Optional.of(ServiceResponse.BLOCKED_LIMIT_TYPE);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ServiceResponse> blockingBalanceTypes(ProductCriteria productCriteria, BalanceTypes[] balanceTypes, International international) {
        IncludeExclude includeExclude = productCriteria.getBlockingBalanceTypes();
        if (includeExclude.equals(IncludeExclude.NOT_APPLICABLE)) {
            return Optional.empty();
        }

        if (balanceTypes == null) {
            return Optional.empty();
        }
        for (BalanceTypes balanceType : balanceTypes) {

            BlockingBalanceType blockingBalanceType = new BlockingBalanceType();
            blockingBalanceType.setBalanceTypes(balanceType.getBalanceTypes());
            Optional<ServiceResponse> optionalServiceResponse = validateBlockingBalanceTypes(productCriteria.getBalanceTypesBlocked(), blockingBalanceType
                    , includeExclude, international);

            if (optionalServiceResponse.isPresent()) {
                return optionalServiceResponse;
            }

        }


        return Optional.empty();
    }

    private Optional<ServiceResponse> validateBlockingBalanceTypes(BlockingBalanceType[] blockingBalanceTypes, BlockingBalanceType blockingBalanceType,
                                                                   IncludeExclude includeExclude, International international) {

        int index = Arrays.binarySearch(blockingBalanceTypes, blockingBalanceType);

        if (index < 0) {
            if (includeExclude.equals(IncludeExclude.INCLUDE)) {
                return Optional.of(ServiceResponse.BLOCKED_BALANCE_TYPE);
            }
        } else {
            InternationalApplied internationalApplied;
            if (international.equals(International.INTERNATIONAL)) {
                internationalApplied = InternationalApplied.INTERNATIONAL;
            } else {
                internationalApplied = InternationalApplied.DOMESTIC;
            }
            blockingBalanceType = blockingBalanceTypes[index];
            if (blockingBalanceType.getInternationalApplied().equals(InternationalApplied.BOTH.getBlockInternational())) {
                if (includeExclude.equals(IncludeExclude.EXCLUDE)) {
                    return Optional.of(ServiceResponse.BLOCKED_BALANCE_TYPE);
                }
            } else if (blockingBalanceType.getInternationalApplied().equals(internationalApplied.getBlockInternational())) {
                if (includeExclude.equals(IncludeExclude.EXCLUDE)) {
                    return Optional.of(ServiceResponse.BLOCKED_BALANCE_TYPE);
                }
            } else {
                if (includeExclude.equals(IncludeExclude.INCLUDE)) {
                    return Optional.of(ServiceResponse.BLOCKED_BALANCE_TYPE);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ServiceResponse> blockingTransactionTypes(ProductCriteria productCriteria, TransactionType transactionType, International international) {
        IncludeExclude includeExclude = productCriteria.getBlockingTransactionTypes();
        if (includeExclude.equals(IncludeExclude.NOT_APPLICABLE)) {
            return Optional.empty();
        }

        if (transactionType == null) {
            return Optional.empty();
        }
        BlockingTransactionType blockingTransactionType = new BlockingTransactionType();
        blockingTransactionType.setTransactionType(transactionType.getTransactionType());

        return validateBlockingTransactionType(productCriteria.getTransactionTypesBlocked(), blockingTransactionType,
                includeExclude, international);
    }

    private Optional<ServiceResponse> validateBlockingTransactionType(BlockingTransactionType[] transactionTypesBlocked, BlockingTransactionType blockingTransactionType,
                                                                      IncludeExclude includeExclude, International international) {

        int index = Arrays.binarySearch(transactionTypesBlocked, blockingTransactionType);

        if (index < 0) {
            if (includeExclude.equals(IncludeExclude.INCLUDE)) {
                return Optional.of(ServiceResponse.BLOCKED_TRANSACTION_TYPE);
            }
        } else {
            InternationalApplied internationalApplied;
            if (international.equals(International.INTERNATIONAL)) {
                internationalApplied = InternationalApplied.INTERNATIONAL;
            } else {
                internationalApplied = InternationalApplied.DOMESTIC;
            }
            blockingTransactionType = transactionTypesBlocked[index];
            if (blockingTransactionType.getInternationalApplied().equals(InternationalApplied.BOTH.getBlockInternational())) {
                if (includeExclude.equals(IncludeExclude.EXCLUDE)) {
                    return Optional.of(ServiceResponse.BLOCKED_TRANSACTION_TYPE);
                }
            } else if (blockingTransactionType.getInternationalApplied().equals(internationalApplied.getBlockInternational())) {
                if (includeExclude.equals(IncludeExclude.EXCLUDE)) {
                    return Optional.of(ServiceResponse.BLOCKED_TRANSACTION_TYPE);
                }
            } else {
                if (includeExclude.equals(IncludeExclude.INCLUDE)) {
                    return Optional.of(ServiceResponse.BLOCKED_TRANSACTION_TYPE);
                }
            }
        }
        return Optional.empty();

    }

    @Override
    public Optional<ServiceResponse> blockingTerminalTypes(ProductCriteria productCriteria, TerminalType terminalType, International international) {
        IncludeExclude includeExclude = productCriteria.getBlockTerminalTypes();
        if (includeExclude.equals(IncludeExclude.NOT_APPLICABLE)) {
            return Optional.empty();
        }

        if (terminalType == null) {
            return Optional.empty();
        }
        BlockingTerminalType blockingTerminalType = new BlockingTerminalType();
        blockingTerminalType.setTerminalType(terminalType.getTerminalType());

        return validateBlockingTerminalType(productCriteria.getTerminalTypesBlocked(), blockingTerminalType,
                includeExclude, international);
    }


    private Optional<ServiceResponse> validateBlockingTerminalType(BlockingTerminalType[] terminalTypesBlocked, BlockingTerminalType blockingTerminalType, IncludeExclude includeExclude, International international) {


        int index = Arrays.binarySearch(terminalTypesBlocked, blockingTerminalType);

        if (index < 0) {
            if (includeExclude.equals(IncludeExclude.INCLUDE)) {
                return Optional.of(ServiceResponse.BLOCKED_TERMINAL_TYPE);
            }
        } else {
            InternationalApplied internationalApplied;
            if (international.equals(International.INTERNATIONAL)) {
                internationalApplied = InternationalApplied.INTERNATIONAL;
            } else {
                internationalApplied = InternationalApplied.DOMESTIC;
            }
            blockingTerminalType = terminalTypesBlocked[index];
            if (blockingTerminalType.getInternationalApplied().equals(InternationalApplied.BOTH.getBlockInternational())) {
                if (includeExclude.equals(IncludeExclude.EXCLUDE)) {
                    return Optional.of(ServiceResponse.BLOCKED_TERMINAL_TYPE);
                }
            } else if (blockingTerminalType.getInternationalApplied().equals(internationalApplied.getBlockInternational())) {
                if (includeExclude.equals(IncludeExclude.EXCLUDE)) {
                    return Optional.of(ServiceResponse.BLOCKED_TERMINAL_TYPE);
                }
            } else {
                if (includeExclude.equals(IncludeExclude.INCLUDE)) {
                    return Optional.of(ServiceResponse.BLOCKED_TERMINAL_TYPE);
                }
            }
        }
        return Optional.empty();

    }

    @Override
    public Optional<ServiceResponse> blockInternational(ProductCriteria productCriteria, International international) {
        if (international.equals(International.INTERNATIONAL) && productCriteria.isBlockInternational()) {
            return Optional.of(ServiceResponse.INTERNATIONAL);
        }

        return Optional.empty();
    }

    @Override
    public Optional<ServiceResponse> blockCashBack(ProductCriteria productCriteria, CashBack cashBack) {
        if (cashBack.equals(CashBack.CASH_BACK_PRESENT) && productCriteria.isBlockCashBack()) {
            return Optional.of(ServiceResponse.CASH_BACK);
        }

        return Optional.empty();
    }

    @Override
    public Optional<ServiceResponse> blockInstallment(ProductCriteria productCriteria, InstallmentType installmentType) {
        if (installmentType.equals(InstallmentType.INSTALLMENT_TYPE) && productCriteria.isBlockInstallments()) {
            return Optional.of(ServiceResponse.INSTALLMENT);
        }

        return Optional.empty();
    }

    @Override
    public List<String> validateParameters(IdentifierValidator identifierValidator, ProductCriteria productCriteria) {

        List<String> serviceResponseList = new ArrayList<>();
//
//        Optional<ProductCriteria> optionalProductCriteria = findCriteriaRecord(identifierValidator.getOrg(), identifierValidator.getProduct(), identifierValidator.getCriteria());
//        if (optionalProductCriteria.isEmpty()) {
//            return new ArrayList<>();
//        }
//
//        ProductCriteria productCriteria = optionalProductCriteria.get();

        International international = International.identify(identifierValidator.getInternational());

        blockingCountryCode(productCriteria, identifierValidator.getAcquirerCountry())
                .ifPresent(serviceResponse -> serviceResponseList.add(serviceResponse.getServiceResponse()));
        blockingCurrencyCode(productCriteria, identifierValidator.getTransactionCurrencyCode())
                .ifPresent(serviceResponse -> serviceResponseList.add(serviceResponse.getServiceResponse()));
        blockingMCC(productCriteria, identifierValidator.getMcc())
                .ifPresent(serviceResponse -> serviceResponseList.add(serviceResponse.getServiceResponse()));

        blockingPurchaseTypes(productCriteria, PurchaseTypes.identify(identifierValidator.getPurchaseTypes()), international)
                .ifPresent(serviceResponse -> serviceResponseList.add(serviceResponse.getServiceResponse()));

        if (identifierValidator.getLimitTypesCount() > 0) {
            LimitType[] limitTypes = new LimitType[identifierValidator.getLimitTypesCount()];
            for (int i = 0; i < identifierValidator.getLimitTypesCount(); i++) {
                limitTypes[i] = LimitType.identify(identifierValidator.getLimitTypes(i));
            }
            blockingLimitTypes(productCriteria, limitTypes, international)
                    .ifPresent(serviceResponse -> serviceResponseList.add(serviceResponse.getServiceResponse()));

        }


        if (identifierValidator.getBalanceTypesCount() > 0) {
            BalanceTypes[] balanceTypes = new BalanceTypes[identifierValidator.getBalanceTypesCount()];
            for (int i = 0; i < identifierValidator.getBalanceTypesCount(); i++) {
                balanceTypes[i] = BalanceTypes.identify(identifierValidator.getBalanceTypes(i));
            }
            blockingBalanceTypes(productCriteria, balanceTypes, international)
                    .ifPresent(serviceResponse -> serviceResponseList.add(serviceResponse.getServiceResponse()));

        }


        blockingTransactionTypes(productCriteria, TransactionType.identify(identifierValidator.getTransactionType()), international)
                .ifPresent(serviceResponse -> serviceResponseList.add(serviceResponse.getServiceResponse()));

        blockingTerminalTypes(productCriteria, TerminalType.identify(identifierValidator.getTerminalType()), international)
                .ifPresent(serviceResponse -> serviceResponseList.add(serviceResponse.getServiceResponse()));


        blockInternational(productCriteria, international)
                .ifPresent(serviceResponse -> serviceResponseList.add(serviceResponse.getServiceResponse()));

        blockCashBack(productCriteria, CashBack.identify(identifierValidator.getCashBack()))
                .ifPresent(serviceResponse -> serviceResponseList.add(serviceResponse.getServiceResponse()));

        blockInstallment(productCriteria, InstallmentType.identify(identifierValidator.getInstallmentType()))
                .ifPresent(serviceResponse -> serviceResponseList.add(serviceResponse.getServiceResponse()));


        return serviceResponseList;
    }

    @Override
    public Optional<ProductCriteria> findCriteriaRecord(IdentifierValidator identifierValidator) {

        ProductCriteriaKey productCriteriaKey = ProductCriteriaKey.builder()
                .org(identifierValidator.getOrg())
                .product(identifierValidator.getProduct())
                .criteria(identifierValidator.getCriteria())
                .build();

        ProductCriteria productCriteria = productCriteriaMap.get(productCriteriaKey);

        if (productCriteria == null) {
            productCriteriaKey = ProductCriteriaKey.builder()
                    .org(identifierValidator.getOrg())
                    .product(identifierValidator.getProduct())
                    .criteria(0)
                    .build();
            productCriteria = productCriteriaMap.get(productCriteriaKey);
            if (productCriteria == null) {
                productCriteriaKey = ProductCriteriaKey.builder()
                        .org(identifierValidator.getOrg())
                        .product(0)
                        .criteria(0)
                        .build();
                productCriteria = productCriteriaMap.get(productCriteriaKey);
                if (productCriteria == null) {
                    productCriteriaKey = ProductCriteriaKey.builder()
                            .org(0)
                            .product(0)
                            .criteria(0)
                            .build();
                    productCriteria = productCriteriaMap.get(productCriteriaKey);
                    if (productCriteria == null) {
                        return Optional.empty();
                    }
                    return Optional.of(productCriteria);
                } else {

                    return Optional.of(productCriteria);
                }

            } else {
                return Optional.of(productCriteria);
            }

        } else {
            return Optional.of(productCriteria);
        }
    }


    @Override
    public Optional<ProductCriteria> findCriteriaRecord(Integer org, Integer product, Integer criteria) {

        ProductCriteriaKey productCriteriaKey = ProductCriteriaKey.builder()
                .org(org)
                .product(product)
                .criteria(criteria)
                .build();

        ProductCriteria productCriteria = productCriteriaMap.get(productCriteriaKey);

        return Optional.ofNullable(productCriteriaMap.get(productCriteriaKey));
    }


    private ProductCriteria convertProductCriteria(ProductAuthCriteria productAuthCriteria) {

        ProductCriteria.ProductCriteriaBuilder builder = ProductCriteria.builder()
                .blockInternational(productAuthCriteria.isBlockInternational())
                .blockCashBack(productAuthCriteria.isBlockCashBack())
                .blockInstallments(productAuthCriteria.isBlockInstallments());

        if (productAuthCriteria.getBlockingCountries() == null) {
            builder.blockingCountries(IncludeExclude.NOT_APPLICABLE);
        } else {
            builder.blockingCountries(IncludeExclude.identify(productAuthCriteria.getBlockingCountries()));

        }
        if (productAuthCriteria.getBlockingCurrency() == null) {
            builder.blockingCurrency(IncludeExclude.NOT_APPLICABLE);
        } else {
            builder.blockingCurrency(IncludeExclude.identify(productAuthCriteria.getBlockingCurrency()));

        }
        if (productAuthCriteria.getBlockingStates() == null) {
            builder.blockingStates(IncludeExclude.NOT_APPLICABLE);
        } else {
            builder.blockingStates(IncludeExclude.identify(productAuthCriteria.getBlockingStates()));
        }

        if (productAuthCriteria.getBlockingMCC() == null) {
            builder.blockingMCC(IncludeExclude.NOT_APPLICABLE);
        } else {
            builder.blockingMCC(IncludeExclude.identify(productAuthCriteria.getBlockingMCC()));

        }
        if (productAuthCriteria.getBlockingPurchaseTypes() == null) {
            builder.blockingPurchaseTypes(IncludeExclude.NOT_APPLICABLE);
        } else {
            builder.blockingPurchaseTypes(IncludeExclude.identify(productAuthCriteria.getBlockingPurchaseTypes()));

        }

        if (productAuthCriteria.getBlockingLimitTypes() == null) {
            builder.blockingLimitTypes(IncludeExclude.NOT_APPLICABLE);
        } else {
            builder.blockingLimitTypes(IncludeExclude.identify(productAuthCriteria.getBlockingLimitTypes()));

        }

        if (productAuthCriteria.getBlockingBalanceTypes() == null) {
            builder.blockingBalanceTypes(IncludeExclude.NOT_APPLICABLE);
        } else {
            builder.blockingBalanceTypes(IncludeExclude.identify(productAuthCriteria.getBlockingBalanceTypes()));
        }

        if (productAuthCriteria.getBlockingTransactionTypes() == null) {
            builder.blockingTransactionTypes(IncludeExclude.NOT_APPLICABLE);

        } else {
            builder.blockingTransactionTypes(IncludeExclude.identify(productAuthCriteria.getBlockingTransactionTypes()));

        }

        if (productAuthCriteria.getBlockTerminalTypes() == null) {
            builder.blockTerminalTypes(IncludeExclude.NOT_APPLICABLE);
        } else {
            builder.blockTerminalTypes(IncludeExclude.identify(productAuthCriteria.getBlockTerminalTypes()));
        }

        if (productAuthCriteria.getStrategy() != null) {
            builder.strategy(Strategy.valueOf(productAuthCriteria.getStrategy()));
        }

        if (productAuthCriteria.getCountryCodesBlocked() != null) {

            String[] countryCodesBlocked = productAuthCriteria
                    .getCountryCodesBlocked().toArray(new String[0]);
            Arrays.sort(countryCodesBlocked);
            builder.countryCodesBlocked(countryCodesBlocked);
        }

        if (productAuthCriteria.getCurrencyCodesBlocked() != null &&
                productAuthCriteria.getCurrencyCodesBlocked().size() > 0) {
            String[] currencyCodesBlocked = productAuthCriteria.getCurrencyCodesBlocked()
                    .toArray(new String[0]);
            Arrays.sort(currencyCodesBlocked);
            builder.currencyCodesBlocked(currencyCodesBlocked);
        }

        if (productAuthCriteria.getStateCodesBlocked() != null && productAuthCriteria.getStateCodesBlocked().size() > 0) {
            String[] stateCodesBlocked = productAuthCriteria
                    .getStateCodesBlocked().toArray(new String[0]);
            Arrays.sort(stateCodesBlocked);
            builder.stateCodesBlocked(stateCodesBlocked);
        }

        if (productAuthCriteria.getMccBlocked() != null && productAuthCriteria.getMccBlocked().size() > 0) {
            MCCRange[] mccBlocked = productAuthCriteria.getMccBlocked().toArray(new MCCRange[0]);
            Arrays.sort(mccBlocked);
            builder.mccBlocked(mccBlocked);
        }

        if (productAuthCriteria.getPurchaseTypesBlocked() != null &&
                productAuthCriteria.getPurchaseTypesBlocked().size() > 0) {
            BlockingPurchaseType[] purchaseTypesBlocked = productAuthCriteria
                    .getPurchaseTypesBlocked()
                    .stream()
                    .map(blockingValue -> BlockingPurchaseType.builder()
                            .purchaseTypes(PurchaseTypes.identify(blockingValue.getValue()))
                            .internationalApplied(InternationalApplied.identify(blockingValue.getInternationalApplied()))
                            .build()
                    )
                    .toArray( BlockingPurchaseType[]:: new);

            Arrays.sort(purchaseTypesBlocked);
            builder.purchaseTypesBlocked(purchaseTypesBlocked);
        }

        if (productAuthCriteria.getLimitTypesBlocked() != null &&
                productAuthCriteria.getLimitTypesBlocked().size() > 0) {

            BlockingLimitType[] limitTypesBlocked = productAuthCriteria
                    .getLimitTypesBlocked()
                    .stream()
                    .map(blockingValue -> BlockingLimitType.builder()
                            .internationalApplied(InternationalApplied.identify(blockingValue.getInternationalApplied()))
                            .limitType(LimitType.identify(blockingValue.getValue()))
                            .build()
                    )
                    .toArray(BlockingLimitType[]::new);

            Arrays.sort(limitTypesBlocked);
            builder.limitTypesBlocked(limitTypesBlocked);

        }

        if (productAuthCriteria.getBalanceTypesBlocked() != null
                && productAuthCriteria.getBalanceTypesBlocked().size() > 0) {
            BlockingBalanceType[] balanceTypesBlocked = productAuthCriteria
                    .getBalanceTypesBlocked().stream()
                    .map(blockingValue -> BlockingBalanceType.builder()
                            .balanceTypes(BalanceTypes.identify(blockingValue.getValue()))
                            .internationalApplied(InternationalApplied.identify(blockingValue.getInternationalApplied()))
                            .build()
                    )
                    .toArray(BlockingBalanceType[]::new);
            Arrays.sort(balanceTypesBlocked);
            builder.balanceTypesBlocked(balanceTypesBlocked);
        }

        if (productAuthCriteria.getTransactionTypesBlocked() != null &&
                productAuthCriteria.getTransactionTypesBlocked().size() > 0) {
            BlockingTransactionType[] transactionTypesBlocked = productAuthCriteria
                    .getTransactionTypesBlocked()
                    .stream()
                    .map(blockingValue -> BlockingTransactionType.builder()
                            .transactionType(TransactionType.identify(blockingValue.getValue()))
                            .internationalApplied(InternationalApplied.identify(blockingValue.getInternationalApplied()))
                            .build()
                    )
                    .toArray(BlockingTransactionType[]::new);

            Arrays.sort(transactionTypesBlocked);
            builder.transactionTypesBlocked(transactionTypesBlocked);
        }

        if (productAuthCriteria.getTerminalTypesBlocked() != null &&
                productAuthCriteria.getTerminalTypesBlocked().size() > 0) {
            BlockingTerminalType[] terminalTypesBlocked = productAuthCriteria
                    .getTerminalTypesBlocked().stream()
                    .map(blockingValue -> BlockingTerminalType.builder()
                            .terminalType(TerminalType.identify(blockingValue.getValue()))
                            .internationalApplied(InternationalApplied.identify(blockingValue.getInternationalApplied()))
                            .build()
                    )
                    .toArray(BlockingTerminalType[]::new);

            Arrays.sort(terminalTypesBlocked);
            builder.terminalTypesBlocked(terminalTypesBlocked);
        }

        return builder.build();

    }

}

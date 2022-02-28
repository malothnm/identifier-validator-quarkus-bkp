package in.nmaloth.identifierValidator.services.parameters;


import in.nmaloth.identifierValidator.model.ProductCriteria;
import in.nmaloth.identifierValidator.model.entity.product.MCCRange;
import in.nmaloth.identifierValidator.model.entity.product.ProductAuthCriteria;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.account.BalanceTypes;
import in.nmaloth.payments.constants.card.LimitType;
import in.nmaloth.payments.constants.instrument.CVM;
import in.nmaloth.payments.constants.products.*;
import in.nmaloth.testResource.GRPCWireResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(GRPCWireResource.class)
class ProductCriteriaTableLoadTest {

    @Inject
    ParameterService parameterService;


    @BeforeEach
    void cleanUp() throws InterruptedException {
        Uni<List<ProductAuthCriteria>> productListUni = ProductAuthCriteria.listAll();

        productListUni.await().indefinitely()
                .forEach(productAuthCriteria -> {
                        productAuthCriteria.delete();

                });
    }


    @Test
    void productChangeQuery() throws InterruptedException {
        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();


        MCCRange[] mccRangeVerification = new MCCRange[]{
                MCCRange.builder().mccStart(3042).mccEnd(3045).build(),
                MCCRange.builder().mccStart(4042).mccEnd(4045).build(),
                MCCRange.builder().mccStart(5042).mccEnd(5045).build()

        };
        BlockingLimitType[] blockingLimitTypes = new BlockingLimitType[]{
                BlockingLimitType.builder().limitType(LimitType.OTC).internationalApplied(InternationalApplied.BOTH).build(),
                BlockingLimitType.builder().limitType(LimitType.QUASI_CASH).internationalApplied(InternationalApplied.INTERNATIONAL).build(),
        };

        BlockingBalanceType[] blockingBalanceTypes = new BlockingBalanceType[]{

                BlockingBalanceType.builder().internationalApplied(InternationalApplied
                        .INTERNATIONAL).balanceTypes(BalanceTypes.CASH_BALANCE).build(),
                BlockingBalanceType.builder().internationalApplied(InternationalApplied
                        .BOTH).balanceTypes(BalanceTypes.INSTALLMENT_BALANCE).build()

        };

        BlockingTransactionType[] blockingTransactionTypes = new BlockingTransactionType[]{

                BlockingTransactionType.builder().internationalApplied(InternationalApplied.BOTH)
                        .transactionType(TransactionType.BILL_PAYMENT).build(),
                BlockingTransactionType.builder().internationalApplied(InternationalApplied.BOTH)
                        .transactionType(TransactionType.CASH).build()

        };

        BlockingPurchaseType[] blockingPurchaseTypes = new BlockingPurchaseType[]{

                BlockingPurchaseType.builder().internationalApplied(InternationalApplied.BOTH)
                        .purchaseTypes(PurchaseTypes.GAMBLING).build(),
                BlockingPurchaseType.builder().internationalApplied(InternationalApplied.INTERNATIONAL)
                        .purchaseTypes(PurchaseTypes.LODGING).build(),
                BlockingPurchaseType.builder().internationalApplied(InternationalApplied.DOMESTIC)
                        .purchaseTypes(PurchaseTypes.RESTAURANT).build(),

        };

        BlockingTerminalType[] blockingTerminalTypes = new BlockingTerminalType[]{

                BlockingTerminalType.builder().terminalType(TerminalType.HOME_TERMINALS).internationalApplied(InternationalApplied.BOTH).build(),
                BlockingTerminalType.builder().terminalType(TerminalType.MOTO).internationalApplied(InternationalApplied.BOTH).build()

        };

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        assertAll(
                () -> assertNotNull(productCriteria),
                () -> assertEquals(Strategy.CHAMPION, productCriteria.getStrategy()),
                () -> assertEquals(IncludeExclude.EXCLUDE, productCriteria.getBlockingCountries()),
                () -> assertArrayEquals(new String[]{"INA", "IND", "PAK"}, productCriteria.getCountryCodesBlocked()),
                () -> assertEquals(IncludeExclude.EXCLUDE, productCriteria.getBlockingCurrency()),
                () -> assertArrayEquals(new String[]{"IDR", "INR", "PKR"}, productCriteria.getCurrencyCodesBlocked()),
                () -> assertEquals(IncludeExclude.EXCLUDE, productCriteria.getBlockingStates()),
                () -> assertNull(productCriteria.getStateCodesBlocked()),
                () -> assertEquals(IncludeExclude.EXCLUDE, productCriteria.getBlockingMCC()),
                () -> assertArrayEquals(mccRangeVerification, productCriteria.getMccBlocked()),
                () -> assertEquals(IncludeExclude.EXCLUDE, productCriteria.getBlockingPurchaseTypes()),
                () -> assertArrayEquals(blockingPurchaseTypes,productCriteria.getPurchaseTypesBlocked()),
//                () -> assertArrayEquals(Arrays.stream(blockingPurchaseTypes).map(blockingPurchaseType -> blockingPurchaseType.getPurchaseTypes()).toArray(),
//                        Arrays.stream(productCriteria.getPurchaseTypesBlocked()).map(blockingPurchaseType -> blockingPurchaseType.getPurchaseTypes()).toArray()),
                () -> assertEquals(IncludeExclude.EXCLUDE, productCriteria.getBlockingLimitTypes()),
                () -> assertArrayEquals(blockingLimitTypes, productCriteria.getLimitTypesBlocked()),
                () -> assertEquals(IncludeExclude.EXCLUDE, productCriteria.getBlockingBalanceTypes()),
                () -> assertArrayEquals(blockingBalanceTypes, productCriteria.getBalanceTypesBlocked()),
                () -> assertEquals(IncludeExclude.EXCLUDE, productCriteria.getBlockingTransactionTypes()),
                () -> assertArrayEquals(blockingTransactionTypes, productCriteria.getTransactionTypesBlocked()),
                () -> assertArrayEquals(blockingTerminalTypes, productCriteria.getTerminalTypesBlocked()),
                () -> assertEquals(IncludeExclude.EXCLUDE, productCriteria.getBlockTerminalTypes())

        );

    }

    @Test
    void productChangeQuery1() throws InterruptedException {
        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 202, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();

        parameterService.loadAllAuthProductCriteria();

        MCCRange[] mccRangeVerification = new MCCRange[]{
                MCCRange.builder().mccStart(3042).mccEnd(3045).build(),
                MCCRange.builder().mccStart(4042).mccEnd(4045).build(),
                MCCRange.builder().mccStart(5042).mccEnd(5045).build()

        };
        BlockingLimitType[] blockingLimitTypes = new BlockingLimitType[]{
                BlockingLimitType.builder().limitType(LimitType.OTC).internationalApplied(InternationalApplied.BOTH).build(),
                BlockingLimitType.builder().limitType(LimitType.QUASI_CASH).internationalApplied(InternationalApplied.INTERNATIONAL).build()
        };

        BlockingBalanceType[] blockingBalanceTypes = new BlockingBalanceType[]{

                BlockingBalanceType.builder().internationalApplied(InternationalApplied
                        .INTERNATIONAL).balanceTypes(BalanceTypes.CASH_BALANCE).build(),
                BlockingBalanceType.builder().internationalApplied(InternationalApplied
                        .BOTH).balanceTypes(BalanceTypes.INSTALLMENT_BALANCE).build()

        };

        BlockingTransactionType[] blockingTransactionTypes = new BlockingTransactionType[]{

                BlockingTransactionType.builder().internationalApplied(InternationalApplied.BOTH)
                        .transactionType(TransactionType.BILL_PAYMENT).build(),
                BlockingTransactionType.builder().internationalApplied(InternationalApplied.BOTH)
                        .transactionType(TransactionType.CASH).build()

        };

        BlockingPurchaseType[] blockingPurchaseTypes = new BlockingPurchaseType[]{

                BlockingPurchaseType.builder().internationalApplied(InternationalApplied.BOTH)
                        .purchaseTypes(PurchaseTypes.GAMBLING).build(),
                BlockingPurchaseType.builder().internationalApplied(InternationalApplied.INTERNATIONAL)
                        .purchaseTypes(PurchaseTypes.LODGING).build(),
                BlockingPurchaseType.builder().internationalApplied(InternationalApplied.DOMESTIC)
                        .purchaseTypes(PurchaseTypes.RESTAURANT).build(),


        };


        BlockingTerminalType[] blockingTerminalTypes = new BlockingTerminalType[]{

                BlockingTerminalType.builder().terminalType(TerminalType.HOME_TERMINALS).internationalApplied(InternationalApplied.BOTH).build(),
                BlockingTerminalType.builder().terminalType(TerminalType.MOTO).internationalApplied(InternationalApplied.BOTH).build()

        };
        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,202,100).get();

        assertAll(
                () -> assertNotNull(productCriteria),
                () -> assertEquals(Strategy.CHAMPION, productCriteria.getStrategy()),
                () -> assertEquals(IncludeExclude.INCLUDE, productCriteria.getBlockingCountries()),
                () -> assertArrayEquals(new String[]{"INA", "IND", "PAK"}, productCriteria.getCountryCodesBlocked()),
                () -> assertEquals(IncludeExclude.INCLUDE, productCriteria.getBlockingCurrency()),
                () -> assertArrayEquals(new String[]{"IDR", "INR", "PKR"}, productCriteria.getCurrencyCodesBlocked()),
                () -> assertEquals(IncludeExclude.INCLUDE, productCriteria.getBlockingStates()),
                () -> assertNull(productCriteria.getStateCodesBlocked()),
                () -> assertEquals(IncludeExclude.INCLUDE, productCriteria.getBlockingMCC()),
                () -> assertArrayEquals(mccRangeVerification, productCriteria.getMccBlocked()),
                () -> assertEquals(IncludeExclude.INCLUDE, productCriteria.getBlockingPurchaseTypes()),
                () -> assertArrayEquals(blockingPurchaseTypes, productCriteria.getPurchaseTypesBlocked()),
                () -> assertEquals(IncludeExclude.INCLUDE, productCriteria.getBlockingLimitTypes()),
                () -> assertArrayEquals(blockingLimitTypes, productCriteria.getLimitTypesBlocked()),
                () -> assertEquals(IncludeExclude.INCLUDE, productCriteria.getBlockingBalanceTypes()),
                () -> assertArrayEquals(blockingBalanceTypes, productCriteria.getBalanceTypesBlocked()),
                () -> assertEquals(IncludeExclude.INCLUDE, productCriteria.getBlockingTransactionTypes()),
                () -> assertArrayEquals(blockingTransactionTypes, productCriteria.getTransactionTypesBlocked()),
                () -> assertArrayEquals(blockingTerminalTypes, productCriteria.getTerminalTypesBlocked()),
                () -> assertEquals(IncludeExclude.INCLUDE, productCriteria.getBlockTerminalTypes())

        );

    }

    @Test
    void productChangeQuery2() throws InterruptedException {
        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 203, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();

        parameterService.loadAllAuthProductCriteria();

        MCCRange[] mccRangeVerification = new MCCRange[]{
                MCCRange.builder().mccStart(3042).mccEnd(3045).build(),
                MCCRange.builder().mccStart(4042).mccEnd(4045).build(),
                MCCRange.builder().mccStart(5042).mccEnd(5045).build()

        };
        BlockingLimitType[] blockingLimitTypes = new BlockingLimitType[]{
                BlockingLimitType.builder().limitType(LimitType.QUASI_CASH).internationalApplied(InternationalApplied.INTERNATIONAL).build(),
                BlockingLimitType.builder().limitType(LimitType.OTC).internationalApplied(InternationalApplied.BOTH).build()
        };

        BlockingBalanceType[] blockingBalanceTypes = new BlockingBalanceType[]{

                BlockingBalanceType.builder().internationalApplied(InternationalApplied
                        .INTERNATIONAL).balanceTypes(BalanceTypes.CASH_BALANCE).build(),
                BlockingBalanceType.builder().internationalApplied(InternationalApplied
                        .BOTH).balanceTypes(BalanceTypes.INSTALLMENT_BALANCE).build()

        };

        BlockingTransactionType[] blockingTransactionTypes = new BlockingTransactionType[]{

                BlockingTransactionType.builder().internationalApplied(InternationalApplied.BOTH)
                        .transactionType(TransactionType.BILL_PAYMENT).build(),
                BlockingTransactionType.builder().internationalApplied(InternationalApplied.BOTH)
                        .transactionType(TransactionType.CASH).build()

        };
        BlockingTerminalType[] blockingTerminalTypes = new BlockingTerminalType[]{

                BlockingTerminalType.builder().terminalType(TerminalType.HOME_TERMINALS).internationalApplied(InternationalApplied.BOTH).build(),
                BlockingTerminalType.builder().terminalType(TerminalType.MOTO).internationalApplied(InternationalApplied.BOTH).build()

        };

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,203,100).get();

        assertAll(
                () -> assertNotNull(productCriteria),
                () -> assertEquals(Strategy.CHAMPION, productCriteria.getStrategy()),
                () -> assertEquals(IncludeExclude.NOT_APPLICABLE, productCriteria.getBlockingCountries()),
                () -> assertNull(productCriteria.getCountryCodesBlocked()),
                () -> assertEquals(IncludeExclude.NOT_APPLICABLE, productCriteria.getBlockingCurrency()),
                () -> assertNull(productCriteria.getCurrencyCodesBlocked()),
                () -> assertEquals(IncludeExclude.NOT_APPLICABLE, productCriteria.getBlockingStates()),
                () -> assertNull(productCriteria.getStateCodesBlocked()),
                () -> assertEquals(IncludeExclude.NOT_APPLICABLE, productCriteria.getBlockingMCC()),
                () -> assertNull(productCriteria.getMccBlocked()),
                () -> assertEquals(IncludeExclude.NOT_APPLICABLE, productCriteria.getBlockingPurchaseTypes()),
                () -> assertNull(productCriteria.getPurchaseTypesBlocked()),
                () -> assertEquals(IncludeExclude.NOT_APPLICABLE, productCriteria.getBlockingLimitTypes()),
                () -> assertNull(productCriteria.getLimitTypesBlocked()),
                () -> assertEquals(IncludeExclude.NOT_APPLICABLE, productCriteria.getBlockingBalanceTypes()),
                () -> assertNull(productCriteria.getBalanceTypesBlocked()),
                () -> assertEquals(IncludeExclude.NOT_APPLICABLE, productCriteria.getBlockingTransactionTypes()),
                () -> assertNull(productCriteria.getTransactionTypesBlocked()),
                () -> assertNull(productCriteria.getTerminalTypesBlocked()),
                () -> assertEquals(IncludeExclude.NOT_APPLICABLE, productCriteria.getBlockTerminalTypes())

        );

    }


    @Test
    void productChangeQuery3() throws InterruptedException {
        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 203, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();

        productAuthCriteria = createProductAuthCriteria(1, 203, 0, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();

        productAuthCriteria = createProductAuthCriteria(1, 0, 0, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();

        productAuthCriteria = createProductAuthCriteria(0, 0, 0, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();

        parameterService.loadAllAuthProductCriteria();

        Optional<ProductCriteria> productCriteriaOptional = parameterService.findCriteriaRecord(1,203,105);

        IdentifierValidator identifierValidator = IdentifierValidator.newBuilder()
                        .setOrg(1).setProduct(203).setCriteria(105).build();

        Optional<ProductCriteria> productCriteriaOptional1 = parameterService.findCriteriaRecord(1,204,105);

        IdentifierValidator identifierValidator1 = IdentifierValidator.newBuilder()
                .setOrg(1).setProduct(204).setCriteria(105).build();


        Optional<ProductCriteria> productCriteriaOptional2 = parameterService.findCriteriaRecord(2,204,105);

        IdentifierValidator identifierValidator2 = IdentifierValidator.newBuilder()
                .setOrg(2).setProduct(204).setCriteria(105).build();


        assertAll(
                ()-> assertTrue(productCriteriaOptional.isEmpty()),
                ()-> assertTrue(parameterService.findCriteriaRecord(identifierValidator).isPresent()),
                ()-> assertTrue(productCriteriaOptional1.isEmpty()),
                ()-> assertTrue(parameterService.findCriteriaRecord(identifierValidator1).isPresent()),
                ()-> assertTrue(productCriteriaOptional2.isEmpty()),
                ()-> assertTrue(parameterService.findCriteriaRecord(identifierValidator2).isPresent())

        );

    }

    private ProductAuthCriteria createProductAuthCriteria(Integer org, Integer product,
                                                          Integer criteria, IncludeExclude includeExclude) {

        List<CVM> cvmList = new ArrayList<>();
        cvmList.add(CVM.SIGNATURE);
        cvmList.add(CVM.ONLINE_PIN);
        cvmList.add(CVM.OFFLINE_PIN);
        cvmList.add(CVM.NO_VERIFICATION);

        List<String> countriesList = new ArrayList<>();
        countriesList.add("IND");
        countriesList.add("INA");
        countriesList.add("PAK");
        List<String> currenciesList = new ArrayList<>();
        currenciesList.add("INR");
        currenciesList.add("IDR");
        currenciesList.add("PKR");
        List<MCCRange> mccRangeList = new ArrayList<>();
        mccRangeList.add(MCCRange.builder().mccStart(5042).mccEnd(5045).build());
        mccRangeList.add(MCCRange.builder().mccStart(4042).mccEnd(4045).build());
        mccRangeList.add(MCCRange.builder().mccStart(3042).mccEnd(3045).build());


        List<BlockingLimitType> limitTypeList = new ArrayList<>();
        limitTypeList.add(BlockingLimitType.builder().limitType(LimitType.OTC).internationalApplied(InternationalApplied.BOTH).build());
        limitTypeList.add(BlockingLimitType.builder().limitType(LimitType.QUASI_CASH).internationalApplied(InternationalApplied.INTERNATIONAL).build());

        List<BlockingBalanceType> blockingBalanceTypeList = new ArrayList<>();
        blockingBalanceTypeList.add(BlockingBalanceType.builder().internationalApplied(InternationalApplied
                .BOTH).balanceTypes(BalanceTypes.INSTALLMENT_BALANCE).build());
        blockingBalanceTypeList.add(BlockingBalanceType.builder().internationalApplied(InternationalApplied
                .INTERNATIONAL).balanceTypes(BalanceTypes.CASH_BALANCE).build());

        List<BlockingTransactionType> blockingTransactionTypeList = new ArrayList<>();
        blockingTransactionTypeList.add(BlockingTransactionType.builder().internationalApplied(InternationalApplied.BOTH)
                .transactionType(TransactionType.CASH).build());
        blockingTransactionTypeList.add(BlockingTransactionType.builder().internationalApplied(InternationalApplied.BOTH)
                .transactionType(TransactionType.BILL_PAYMENT).build());


        List<BlockingTerminalType> terminalTypeList = new ArrayList<>();
        terminalTypeList.add(BlockingTerminalType.builder().terminalType(TerminalType.MOTO).internationalApplied(InternationalApplied.BOTH).build());
        terminalTypeList.add(BlockingTerminalType.builder().terminalType(TerminalType.HOME_TERMINALS).internationalApplied(InternationalApplied.BOTH).build());

        List<BlockingPurchaseType> purchaseTypesList = new ArrayList<>();
        purchaseTypesList.add(BlockingPurchaseType.builder()
                .purchaseTypes(PurchaseTypes.GAMBLING).internationalApplied(InternationalApplied.BOTH)
                .build());

        purchaseTypesList.add(BlockingPurchaseType.builder()
                .purchaseTypes(PurchaseTypes.LODGING).internationalApplied(InternationalApplied.INTERNATIONAL)
                .build());

        purchaseTypesList.add(BlockingPurchaseType.builder()
                .purchaseTypes(PurchaseTypes.RESTAURANT).internationalApplied(InternationalApplied.DOMESTIC)
                .build());


        ProductAuthCriteria.ProductAuthCriteriaBuilder builder =
                ProductAuthCriteria.builder()
                        .org(org)
                        .product(product)
                        .criteria(criteria)
                        .strategy(Strategy.CHAMPION);

        if (includeExclude.equals(IncludeExclude.EXCLUDE)) {

            builder
                    .blockingCountries(IncludeExclude.EXCLUDE)
                    .countryCodesBlocked(countriesList)
                    .blockingCurrency(IncludeExclude.EXCLUDE)
                    .currencyCodesBlocked(currenciesList)
                    .blockingStates(IncludeExclude.EXCLUDE)
                    .stateCodesBlocked(new ArrayList<>())
                    .blockingMCC(IncludeExclude.EXCLUDE)
                    .mccBlocked(mccRangeList)
                    .blockingLimitTypes(IncludeExclude.EXCLUDE)
                    .limitTypesBlocked(limitTypeList)
                    .blockingPurchaseTypes(IncludeExclude.EXCLUDE)
                    .purchaseTypesBlocked(purchaseTypesList)
                    .blockingBalanceTypes(IncludeExclude.EXCLUDE)
                    .balanceTypesBlocked(blockingBalanceTypeList)
                    .blockingTransactionTypes(IncludeExclude.EXCLUDE)
                    .transactionTypesBlocked(blockingTransactionTypeList)
                    .blockTerminalTypes(IncludeExclude.EXCLUDE)
                    .terminalTypesBlocked(terminalTypeList)
            ;
        } else if (includeExclude.equals(IncludeExclude.INCLUDE)) {

            builder
                    .blockingCountries(IncludeExclude.INCLUDE)
                    .countryCodesBlocked(countriesList)
                    .blockingCurrency(IncludeExclude.INCLUDE)
                    .currencyCodesBlocked(currenciesList)
                    .blockingStates(IncludeExclude.INCLUDE)
                    .stateCodesBlocked(new ArrayList<>())
                    .blockingMCC(IncludeExclude.INCLUDE)
                    .mccBlocked(mccRangeList)
                    .blockingLimitTypes(IncludeExclude.INCLUDE)
                    .limitTypesBlocked(limitTypeList)
                    .blockingPurchaseTypes(IncludeExclude.INCLUDE)
                    .purchaseTypesBlocked(purchaseTypesList)
                    .blockingBalanceTypes(IncludeExclude.INCLUDE)
                    .balanceTypesBlocked(blockingBalanceTypeList)
                    .blockingTransactionTypes(IncludeExclude.INCLUDE)
                    .transactionTypesBlocked(blockingTransactionTypeList)
                    .blockTerminalTypes(IncludeExclude.INCLUDE)
                    .terminalTypesBlocked(terminalTypeList)
            ;
        } else {

            builder
                    .blockingCountries(IncludeExclude.NOT_APPLICABLE)
                    .blockingCurrency(IncludeExclude.NOT_APPLICABLE)
                    .blockingStates(IncludeExclude.NOT_APPLICABLE)
                    .blockingMCC(IncludeExclude.NOT_APPLICABLE)
                    .blockingLimitTypes(IncludeExclude.NOT_APPLICABLE)
                    .blockingPurchaseTypes(IncludeExclude.NOT_APPLICABLE)
                    .blockingBalanceTypes(IncludeExclude.NOT_APPLICABLE)
                    .blockingTransactionTypes(IncludeExclude.NOT_APPLICABLE)
                    .blockTerminalTypes(IncludeExclude.NOT_APPLICABLE)
            ;
        }

        return builder.build();


    }
}
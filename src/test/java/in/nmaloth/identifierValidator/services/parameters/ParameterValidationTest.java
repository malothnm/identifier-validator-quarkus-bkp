package in.nmaloth.identifierValidator.services.parameters;

import in.nmaloth.identifierValidator.model.ProductCriteria;
import in.nmaloth.identifierValidator.model.entity.product.MCCRange;
import in.nmaloth.identifierValidator.model.entity.product.ProductAuthCriteria;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.account.BalanceTypes;
import in.nmaloth.payments.constants.card.LimitType;
import in.nmaloth.payments.constants.products.*;
import in.nmaloth.testResource.GRPCWireResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(GRPCWireResource.class)
public class ParameterValidationTest {


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
    void allPass_Exclude() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());



        IdentifierValidator.Builder builder = createIdentifierValidator(containerId,
                1,201,100,
                accountId, cardId, instrument,
                customerId, aggregatorInstance,
                CashBack.NO_CASH_BACK,
                International.DOMESTIC,
                InstallmentType.INSTALLMENT_TYPE,
                TerminalType.ATM,
                TransactionType.PURCHASE,
                PurchaseTypes.AIRLINE,
                6011,
                "USA",
                "USD",
                balanceTypeList,
                limitTypeList);


        IdentifierValidator identifierValidator = builder.build();

        Optional<ProductCriteria> optionalProductCriteria = parameterService.findCriteriaRecord(identifierValidator.getOrg(), identifierValidator.getProduct(), identifierValidator.getCriteria());

        ProductCriteria productCriteria = optionalProductCriteria.get();


        List<String> responseList = parameterService.validateParameters(identifierValidator,productCriteria);

        assertAll(
                ()-> assertEquals(0, responseList.size())
        );

    }

    @Test
    void blockInstallment_Exclude() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.setBlockInstallments(true);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());



        IdentifierValidator.Builder builder = createIdentifierValidator(containerId,
                1,201,100,
                accountId, cardId, instrument,
                customerId, aggregatorInstance,
                CashBack.NO_CASH_BACK,
                International.DOMESTIC,
                InstallmentType.INSTALLMENT_TYPE,
                TerminalType.ATM,
                TransactionType.PURCHASE,
                PurchaseTypes.AIRLINE,
                6011,
                "USA",
                "USD",
                balanceTypeList,
                limitTypeList);


        IdentifierValidator identifierValidator = builder.build();

        Optional<ProductCriteria> optionalProductCriteria = parameterService.findCriteriaRecord(identifierValidator.getOrg(), identifierValidator.getProduct(), identifierValidator.getCriteria());

        ProductCriteria productCriteria = optionalProductCriteria.get();


        List<String> responseList = parameterService.validateParameters(identifierValidator,productCriteria);

        assertAll(
                ()-> assertEquals(1, responseList.size()),
                ()-> assertEquals(ServiceResponse.INSTALLMENT.getServiceResponse(), responseList.get(0))

        );

    }


    @Test
    void blockCashBack_Exclude() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.setBlockCashBack(true);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());



        IdentifierValidator.Builder builder = createIdentifierValidator(containerId,
                1,201,100,
                accountId, cardId, instrument,
                customerId, aggregatorInstance,
                CashBack.CASH_BACK_PRESENT,
                International.DOMESTIC,
                InstallmentType.INSTALLMENT_TYPE,
                TerminalType.ATM,
                TransactionType.PURCHASE,
                PurchaseTypes.AIRLINE,
                6011,
                "USA",
                "USD",
                balanceTypeList,
                limitTypeList);


        IdentifierValidator identifierValidator = builder.build();

        Optional<ProductCriteria> optionalProductCriteria = parameterService.findCriteriaRecord(identifierValidator.getOrg(), identifierValidator.getProduct(), identifierValidator.getCriteria());

        ProductCriteria productCriteria = optionalProductCriteria.get();


        List<String> responseList = parameterService.validateParameters(identifierValidator,productCriteria);

        assertAll(
                ()-> assertEquals(1, responseList.size()),
                ()-> assertEquals(ServiceResponse.CASH_BACK.getServiceResponse(),responseList.get(0))
        );

    }


    @Test
    void blockInternational_Exclude() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.setBlockInternational(true);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());



        IdentifierValidator.Builder builder = createIdentifierValidator(containerId,
                1,201,100,
                accountId, cardId, instrument,
                customerId, aggregatorInstance,
                CashBack.NO_CASH_BACK,
                International.INTERNATIONAL,
                InstallmentType.INSTALLMENT_TYPE,
                TerminalType.ATM,
                TransactionType.PURCHASE,
                PurchaseTypes.AIRLINE,
                6011,
                "USA",
                "USD",
                balanceTypeList,
                limitTypeList);


        IdentifierValidator identifierValidator = builder.build();

        Optional<ProductCriteria> optionalProductCriteria = parameterService.findCriteriaRecord(identifierValidator.getOrg(), identifierValidator.getProduct(), identifierValidator.getCriteria());

        ProductCriteria productCriteria = optionalProductCriteria.get();


        List<String> responseList = parameterService.validateParameters(identifierValidator,productCriteria);
        assertAll(
                ()-> assertEquals(1, responseList.size()),
                ()-> assertEquals(ServiceResponse.INTERNATIONAL.getServiceResponse(),responseList.get(0))
        );

    }


    @Test
    void blockTerminalType_Exclude() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());



        IdentifierValidator.Builder builder = createIdentifierValidator(containerId,
                1,201,100,
                accountId, cardId, instrument,
                customerId, aggregatorInstance,
                CashBack.NO_CASH_BACK,
                International.DOMESTIC,
                InstallmentType.INSTALLMENT_TYPE,
                TerminalType.ATM,
                TransactionType.PURCHASE,
                PurchaseTypes.AIRLINE,
                6011,
                "USA",
                "USD",
                balanceTypeList,
                limitTypeList);


        IdentifierValidator identifierValidator = builder.build();

        Optional<ProductCriteria> optionalProductCriteria = parameterService.findCriteriaRecord(identifierValidator.getOrg(), identifierValidator.getProduct(), identifierValidator.getCriteria());

        ProductCriteria productCriteria = optionalProductCriteria.get();


        List<String> responseList = parameterService.validateParameters(identifierValidator,productCriteria);
        assertAll(
                ()-> assertEquals(0, responseList.size())
        );

    }


    @Test
    void blockTransactionType_Exclude() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());



        IdentifierValidator.Builder builder = createIdentifierValidator(containerId,
                1,201,100,
                accountId, cardId, instrument,
                customerId, aggregatorInstance,
                CashBack.NO_CASH_BACK,
                International.DOMESTIC,
                InstallmentType.INSTALLMENT_TYPE,
                TerminalType.ATM,
                TransactionType.CASH,
                PurchaseTypes.AIRLINE,
                6011,
                "USA",
                "USD",
                balanceTypeList,
                limitTypeList);


        IdentifierValidator identifierValidator = builder.build();

        Optional<ProductCriteria> optionalProductCriteria = parameterService.findCriteriaRecord(identifierValidator.getOrg(), identifierValidator.getProduct(), identifierValidator.getCriteria());

        ProductCriteria productCriteria = optionalProductCriteria.get();


        List<String> responseList = parameterService.validateParameters(identifierValidator,productCriteria);
        assertAll(
                ()-> assertEquals(1, responseList.size()),
                ()-> assertEquals(ServiceResponse.BLOCKED_TRANSACTION_TYPE.getServiceResponse(), responseList.get(0))

        );

    }


    @Test
    void blockBalanceType_Exclude() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.INSTALLMENT_BALANCE.getBalanceTypes());



        IdentifierValidator.Builder builder = createIdentifierValidator(containerId,
                1,201,100,
                accountId, cardId, instrument,
                customerId, aggregatorInstance,
                CashBack.NO_CASH_BACK,
                International.DOMESTIC,
                InstallmentType.INSTALLMENT_TYPE,
                TerminalType.ATM,
                TransactionType.PURCHASE,
                PurchaseTypes.AIRLINE,
                6011,
                "USA",
                "USD",
                balanceTypeList,
                limitTypeList);


        IdentifierValidator identifierValidator = builder.build();

        Optional<ProductCriteria> optionalProductCriteria = parameterService.findCriteriaRecord(identifierValidator.getOrg(), identifierValidator.getProduct(), identifierValidator.getCriteria());

        ProductCriteria productCriteria = optionalProductCriteria.get();


        List<String> responseList = parameterService.validateParameters(identifierValidator,productCriteria);
        assertAll(
                ()-> assertEquals(1, responseList.size()),
                ()-> assertEquals(ServiceResponse.BLOCKED_BALANCE_TYPE.getServiceResponse(), responseList.get(0))

        );

    }

    @Test
    void blockLimitType_Exclude() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());



        IdentifierValidator.Builder builder = createIdentifierValidator(containerId,
                1,201,100,
                accountId, cardId, instrument,
                customerId, aggregatorInstance,
                CashBack.NO_CASH_BACK,
                International.DOMESTIC,
                InstallmentType.INSTALLMENT_TYPE,
                TerminalType.ATM,
                TransactionType.PURCHASE,
                PurchaseTypes.AIRLINE,
                6011,
                "USA",
                "USD",
                balanceTypeList,
                limitTypeList);


        IdentifierValidator identifierValidator = builder.build();

        Optional<ProductCriteria> optionalProductCriteria = parameterService.findCriteriaRecord(identifierValidator.getOrg(), identifierValidator.getProduct(), identifierValidator.getCriteria());

        ProductCriteria productCriteria = optionalProductCriteria.get();


        List<String> responseList = parameterService.validateParameters(identifierValidator,productCriteria);
        assertAll(
                ()-> assertEquals(1, responseList.size()),
                ()-> assertEquals(ServiceResponse.BLOCKED_LIMIT_TYPE.getServiceResponse(), responseList.get(0))

        );

    }



    @Test
    void blockingPurchaseType_Exclude() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());



        IdentifierValidator.Builder builder = createIdentifierValidator(containerId,
                1,201,100,
                accountId, cardId, instrument,
                customerId, aggregatorInstance,
                CashBack.NO_CASH_BACK,
                International.DOMESTIC,
                InstallmentType.INSTALLMENT_TYPE,
                TerminalType.ATM,
                TransactionType.PURCHASE,
                PurchaseTypes.RESTAURANT,
                6011,
                "USA",
                "USD",
                balanceTypeList,
                limitTypeList);


        IdentifierValidator identifierValidator = builder.build();

        Optional<ProductCriteria> optionalProductCriteria = parameterService.findCriteriaRecord(identifierValidator.getOrg(), identifierValidator.getProduct(), identifierValidator.getCriteria());

        ProductCriteria productCriteria = optionalProductCriteria.get();


        List<String> responseList = parameterService.validateParameters(identifierValidator,productCriteria);

        assertAll(
                ()-> assertEquals(1, responseList.size()),
                ()-> assertEquals(ServiceResponse.BLOCKED_PURCHASE_TYPE.getServiceResponse(),responseList.get(0))
        );

    }


    @Test
    void blockMcc_Exclude() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());



        IdentifierValidator.Builder builder = createIdentifierValidator(containerId,
                1,201,100,
                accountId, cardId, instrument,
                customerId, aggregatorInstance,
                CashBack.NO_CASH_BACK,
                International.DOMESTIC,
                InstallmentType.INSTALLMENT_TYPE,
                TerminalType.ATM,
                TransactionType.PURCHASE,
                PurchaseTypes.AIRLINE,
                5042,
                "USA",
                "USD",
                balanceTypeList,
                limitTypeList);


        IdentifierValidator identifierValidator = builder.build();

        Optional<ProductCriteria> optionalProductCriteria = parameterService.findCriteriaRecord(identifierValidator.getOrg(), identifierValidator.getProduct(), identifierValidator.getCriteria());

        ProductCriteria productCriteria = optionalProductCriteria.get();


        List<String> responseList = parameterService.validateParameters(identifierValidator,productCriteria);

        assertAll(
                ()-> assertEquals(1, responseList.size()),
                ()-> assertEquals(ServiceResponse.BLOCKED_MCC.getServiceResponse(),responseList.get(0))
        );

    }



    @Test
    void blockCurrencyCode_Exclude() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());



        IdentifierValidator.Builder builder = createIdentifierValidator(containerId,
                1,201,100,
                accountId, cardId, instrument,
                customerId, aggregatorInstance,
                CashBack.NO_CASH_BACK,
                International.DOMESTIC,
                InstallmentType.INSTALLMENT_TYPE,
                TerminalType.ATM,
                TransactionType.PURCHASE,
                PurchaseTypes.AIRLINE,
                6011,
                "USA",
                "INR",
                balanceTypeList,
                limitTypeList);


        IdentifierValidator identifierValidator = builder.build();

        Optional<ProductCriteria> optionalProductCriteria = parameterService.findCriteriaRecord(identifierValidator.getOrg(), identifierValidator.getProduct(), identifierValidator.getCriteria());

        ProductCriteria productCriteria = optionalProductCriteria.get();


        List<String> responseList = parameterService.validateParameters(identifierValidator,productCriteria);

        assertAll(
                ()-> assertEquals(1, responseList.size()),
                ()-> assertEquals(ServiceResponse.BLOCKED_CURRENCY.getServiceResponse(), responseList.get(0))
        );

    }



    @Test
    void blockingCountryCode_Exclude() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());



        IdentifierValidator.Builder builder = createIdentifierValidator(containerId,
                1,201,100,
                accountId, cardId, instrument,
                customerId, aggregatorInstance,
                CashBack.NO_CASH_BACK,
                International.DOMESTIC,
                InstallmentType.INSTALLMENT_TYPE,
                TerminalType.ATM,
                TransactionType.PURCHASE,
                PurchaseTypes.AIRLINE,
                6011,
                "IND",
                "USD",
                balanceTypeList,
                limitTypeList);


        IdentifierValidator identifierValidator = builder.build();

        Optional<ProductCriteria> optionalProductCriteria = parameterService.findCriteriaRecord(identifierValidator.getOrg(), identifierValidator.getProduct(), identifierValidator.getCriteria());

        ProductCriteria productCriteria = optionalProductCriteria.get();


        List<String> responseList = parameterService.validateParameters(identifierValidator,productCriteria);

        assertAll(
                ()-> assertEquals(1, responseList.size()),
                ()-> assertEquals(ServiceResponse.BLOCKED_COUNTRY.getServiceResponse(), responseList.get(0))
        );

    }




    private ProductAuthCriteria createProductAuthCriteria(Integer org, Integer product,
                                                          Integer criteria, IncludeExclude includeExclude) {

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
        limitTypeList.add(BlockingLimitType.builder().limitType(LimitType.ATM).internationalApplied(InternationalApplied.DOMESTIC).build());

        List<BlockingBalanceType> blockingBalanceTypeList = new ArrayList<>();
        blockingBalanceTypeList.add(BlockingBalanceType.builder().internationalApplied(InternationalApplied
                .BOTH).balanceTypes(BalanceTypes.INSTALLMENT_BALANCE).build());
        blockingBalanceTypeList.add(BlockingBalanceType.builder().internationalApplied(InternationalApplied
                .INTERNATIONAL).balanceTypes(BalanceTypes.CASH_BALANCE).build());
        blockingBalanceTypeList.add(BlockingBalanceType.builder().internationalApplied(InternationalApplied
                .DOMESTIC).balanceTypes(BalanceTypes.INSTALLMENT_CASH).build());

        List<BlockingTransactionType> blockingTransactionTypeList = new ArrayList<>();
        blockingTransactionTypeList.add(BlockingTransactionType.builder().internationalApplied(InternationalApplied.BOTH)
                .transactionType(TransactionType.CASH).build());
        blockingTransactionTypeList.add(BlockingTransactionType.builder().internationalApplied(InternationalApplied.INTERNATIONAL)
                .transactionType(TransactionType.BILL_PAYMENT).build());
        blockingTransactionTypeList.add(BlockingTransactionType.builder().internationalApplied(InternationalApplied.DOMESTIC)
                .transactionType(TransactionType.OCT).build());


        List<BlockingTerminalType> terminalTypeList = new ArrayList<>();
        terminalTypeList.add(BlockingTerminalType.builder().terminalType(TerminalType.MOTO).internationalApplied(InternationalApplied.BOTH).build());
        terminalTypeList.add(BlockingTerminalType.builder().terminalType(TerminalType.HOME_TERMINALS).internationalApplied(InternationalApplied.INTERNATIONAL).build());
        terminalTypeList.add(BlockingTerminalType.builder().terminalType(TerminalType.ELECTRONIC_CASH_REGISTER).internationalApplied(InternationalApplied.DOMESTIC).build());

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


    IdentifierValidator.Builder createIdentifierValidator(String containerId, Integer org, Integer product, Integer criteria,  String accountId,
                                                          String cardId, String instrument,
                                                          String customerId,
                                                          String aggregatorInstance,
                                                          CashBack cashBack,
                                                          International international,
                                                          InstallmentType installmentType,
                                                          TerminalType terminalType,
                                                          TransactionType transactionType,
                                                          PurchaseTypes purchaseTypes,
                                                          Integer mcc,
                                                          String countryCode,
                                                          String txnCurrencyCode,
                                                          List<String> balanceTypesList, List<String> limitTypeList) {

        return IdentifierValidator.newBuilder()
                .setMessageId(UUID.randomUUID().toString().replace("-",""))
                .setMessageTypeId("0100")
                .setContainerId(containerId)
                .setChannelId(UUID.randomUUID().toString().replace("-",""))
                .setAggregatorInstance(aggregatorInstance)
                .setCardNumber(cardId)
                .setAccountNumber(accountId)
                .setInstrument(instrument)
                .setCustomerNumber(customerId)
                .setAddress("1234,test address 78")
                .setPostalCode("623456")
                .setCustomerName("Test")
                .setAvsType(AVSType.AVS_NOT_PRESENT.getAvsType())
                .setTransactionAmount(1000L)
                .setBillingAmount(2000L)
                .setTransactionCurrencyCode(txnCurrencyCode)
                .setBillingCurrencyCode("124")
                .setTransactionType(transactionType.getTransactionType())
                .setCountryCode(countryCode)
                .setAuthorizationType(AuthorizationType.AUTH.getAuthorizationType())
                .setInstallmentType(installmentType.getInstallmentType())
                .setCashBack(cashBack.getCashBack())
                .setCashBackAmount(0)
                .setCardAcceptorId("123456789012")
                .setCardAcceptorTerminalID("123456790123456")
                .setMerchantName("Best Buy")
                .setMerchantCountry("124")
                .setMerchantState("Ottwa")
                .setMerchantCity("Ottawa")
                .setPostalCode("682019")
                .setAcquirerId("123456789")
                .setAcquirerCountry(countryCode)
                .setMerchantNumber("1234567")
                .setMcc(mcc)
                .setTerminalType(terminalType.getTerminalType())
                .setRecurringTrans(RecurringTrans.NOT_RECURRING_TRANS.getRecurringTrans())
                .setPurchaseTypes(purchaseTypes.getPurchaseTypes())
                .setEntryMode(EntryMode.ICC.getEntryMode())
                .setExpiryDate("20210330")
                .setInternational(international.getInternational())
                .addAllBalanceTypes(balanceTypesList)
                .addAllLimitTypes(limitTypeList)
                .setOrg(org)
                .setProduct(product)
                .setCriteria(criteria)
                ;
    }

}

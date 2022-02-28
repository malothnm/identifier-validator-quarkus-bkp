package in.nmaloth.identifierValidator;

import in.nmaloth.identifierValidator.config.EventProcessors;
import in.nmaloth.identifierValidator.config.ServiceNames;
import in.nmaloth.identifierValidator.model.entity.account.AccountBalances;
import in.nmaloth.identifierValidator.model.entity.account.AccountInfo;
import in.nmaloth.identifierValidator.model.entity.card.CardsBasic;
import in.nmaloth.identifierValidator.model.entity.card.PeriodicCardAmount;
import in.nmaloth.identifierValidator.model.entity.customer.CustomerDef;
import in.nmaloth.identifierValidator.model.entity.global.CurrencyConversionTable;
import in.nmaloth.identifierValidator.model.entity.product.MCCRange;
import in.nmaloth.identifierValidator.model.entity.product.ProductAuthCriteria;
import in.nmaloth.identifierValidator.model.entity.product.ProductAuthCriteriaKey;
import in.nmaloth.identifierValidator.model.proto.aggregator.*;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierResponse;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.identifierValidator.services.CurrencyConversionService;
import in.nmaloth.identifierValidator.services.parameters.ParameterService;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.account.AccountType;
import in.nmaloth.payments.constants.account.BalanceTypes;
import in.nmaloth.payments.constants.card.CardHolderType;
import in.nmaloth.payments.constants.card.CardStatus;
import in.nmaloth.payments.constants.card.LimitType;
import in.nmaloth.payments.constants.card.PeriodicType;
import in.nmaloth.payments.constants.customer.AddressType;
import in.nmaloth.payments.constants.customer.CustomerType;
import in.nmaloth.payments.constants.ids.ServiceID;
import in.nmaloth.payments.constants.instrument.CVM;
import in.nmaloth.payments.constants.network.NetworkType;
import in.nmaloth.payments.constants.products.*;
import in.nmaloth.testResource.ConnectionServer;
import in.nmaloth.testResource.GRPCWireResource;
import in.nmaloth.testResource.InjectConnectionServer;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.infinispan.client.Remote;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.subscription.MultiEmitter;
import io.smallrye.mutiny.tuples.Tuple2;
import org.infinispan.client.hotrod.RemoteCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(GRPCWireResource.class)
public class MessageTest {

    @InjectConnectionServer
    ConnectionServer connectionServer;

    @Inject
    EventProcessors eventProcessors;


    @Inject
    ParameterService parameterService;


    @GrpcClient
    AggregatorService aggregatorService;

    @Inject
    CurrencyConversionService currencyConversionService;

    @Inject
    @Remote("backupIdentifier")
    RemoteCache<String, byte[]> backupCache;


    RegistrationAggregator registrationAggregator;

    private List<ValidationResponseSummary> validationResponseSummaryList = new ArrayList<>();



    private final String aggregatorInstance = UUID.randomUUID().toString().replace("-","");
    private final String distributorInstance = UUID.randomUUID().toString().replace("-","");

    MultiEmitter<? super AggregatorResponse> aggregatorResponseMultiEmitter;

    boolean initialConnect = true;



    @BeforeEach
    void setUp(){

        Uni<List<ProductAuthCriteria>> productAuthCriteriaUni = ProductAuthCriteria.listAll();
        productAuthCriteriaUni.await().indefinitely()
                .forEach(productAuthCriteria -> productAuthCriteria.delete().await().indefinitely());


        if(initialConnect){

            initialConnect = false;

            createAggregatorStream();

            List<CurrencyConversionTable> currencyConversionTableList = new ArrayList<>();
            currencyConversionTableList.add(buildCurrencyTable("124","840",500000L, LocalDate.now(), NetworkType.VISA_VIP));
            currencyConversionTableList.add(buildCurrencyTable("840","124",2000000L,LocalDate.now(),NetworkType.VISA_VIP));

            currencyConversionTableList.add(buildCurrencyTable("484","840",100000L,LocalDate.now(),NetworkType.VISA_VIP));
            currencyConversionTableList.add(buildCurrencyTable("840","484",10000000L,LocalDate.now(),NetworkType.VISA_VIP));


            CurrencyConversionTable.persist(currencyConversionTableList).await().indefinitely();

            currencyConversionService.loadCurrencyTables();

        }


    }


    @Test
    void testConnectivityWithIdentifierServer() throws InterruptedException {

        List<Tuple2<String, String>> distributorList =  eventProcessors.distributorProcessor.getOutgoingFluxInfo();

        String[] stringArray = distributorList.stream()
                        .map(tuple2-> tuple2.getItem1())
                                .collect(Collectors.toList()).toArray(new String[0]);

        String[] testArray = new String[]{ServiceNames.DISTRIBUTOR, ServiceNames.DISTRIBUTOR};

        assertAll(
                ()-> assertNotNull(connectionServer.messageListener.getServiceInstance()),
                ()-> assertEquals(ServiceNames.IDENTIFIER_SERVICE,connectionServer.messageListener.getServiceName()),
                ()-> assertEquals(2, distributorList.size()),
                ()-> assertArrayEquals(testArray,stringArray)

        );
    }


    @Test
    void sendMessageIdentifierServer() throws InterruptedException {

        List<Tuple2<String, String>> distributorList =  eventProcessors.distributorProcessor.getOutgoingFluxInfo();

        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");
        String  cardId = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());



        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardId,instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,"840","840",
                        balanceTypeList,limitTypeList).build();


        connectionServer.distributorProcessor.processMessage(identifierValidator);

        Thread.sleep(100);

        Optional<IdentifierResponse> identifierResponseOptional = connectionServer.distributorResponseList.stream().filter(identifierResponse -> identifierResponse.getMessageId().equals(identifierValidator.getMessageId())).findFirst();

        byte[] messages = backupCache.get(identifierValidator.getMessageId());

        assertAll(
                ()-> assertTrue(identifierResponseOptional.isPresent()),
                ()-> assertNotNull(messages)


        );
    }


    @Test
    void sendMessageNoCustomer() throws InterruptedException {


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = setupCustomer("test   name", "123 testAddress 456", "672019");
        String  instrument = UUID.randomUUID().toString().replace("-","");


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();


        List<TerminalType> terminalTypeListCard = new ArrayList<>();
        terminalTypeListCard.add(TerminalType.ATM);
        terminalTypeListCard.add(TerminalType.HOME_TERMINALS);
        List<PurchaseTypes> purchaseTypesListCard = List.of(PurchaseTypes.AIRLINE,PurchaseTypes.LODGING);
        List<TransactionType> transactionTypeListCard = List.of(TransactionType.ACCOUNT_FUND_TRANSACTION, TransactionType.OCT);
        List<LimitType> limitTypeListCard = List.of(LimitType.RETAIL, LimitType.OTC);
        List<EntryMode> entryModeListCard = List.of(EntryMode.ICC,EntryMode.MAG);

        List<LimitType> limitTypeList1Card = new ArrayList<>();
        limitTypeList1Card.add(LimitType.RETAIL);
        limitTypeList1Card.add(LimitType.NO_SPECIFIC);

        Map<PeriodicType,Map<LimitType,PeriodicCardAmount>> periodicTypeMapLimit = new HashMap<>();


        Map<LimitType, PeriodicCardAmount> cardLimitMapSingle= createProductTypeMap(500L, 1, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardLimitMapDaily = createProductTypeMap(1000L, 10, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardLimitMapMonthly= createProductTypeMap(10000L, 100, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardLimitMapYearly= createProductTypeMap(100000L, 1000, 1, List.of(LimitType.RETAIL,LimitType.ATM));

        periodicTypeMapLimit.put(PeriodicType.SINGLE,cardLimitMapSingle);
        periodicTypeMapLimit.put(PeriodicType.DAILY,cardLimitMapDaily);
        periodicTypeMapLimit.put(PeriodicType.MONTHLY,cardLimitMapMonthly);
        periodicTypeMapLimit.put(PeriodicType.YEARLY,cardLimitMapYearly);

        Map<PeriodicType,Map<LimitType,PeriodicCardAmount>> periodicTypeMapAmount = new HashMap<>();

        Map<LimitType, PeriodicCardAmount> cardAmountMapSingle= createProductTypeMap(0L, 0, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapDaily = createProductTypeMap(300L, 8, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapMonthly= createProductTypeMap(300L, 10, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapYearly= createProductTypeMap(99800L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));

        periodicTypeMapAmount.put(PeriodicType.SINGLE,cardAmountMapSingle);
        periodicTypeMapAmount.put(PeriodicType.DAILY,cardAmountMapDaily);
        periodicTypeMapAmount.put(PeriodicType.MONTHLY,cardAmountMapMonthly);
        periodicTypeMapAmount.put(PeriodicType.YEARLY,cardAmountMapYearly);

        CardsBasic cardsBasic = setupDataForTest(CardStatus.ACTIVE,BlockType.APPROVE,
                terminalTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                purchaseTypesListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                transactionTypeListCard, InternationalApplied.INTERNATIONAL,IncludeExclude.NOT_APPLICABLE,
                limitTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                entryModeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                false,false,false,periodicTypeMapLimit,periodicTypeMapAmount,1,201);

        String  cardId = cardsBasic.getCardId();


        cardsBasic.persist().await().indefinitely();

        Map<BalanceTypes,Long> limitMap = Map.of(BalanceTypes.CURRENT_BALANCE,10000L, BalanceTypes.CASH_BALANCE,5000L);
        Map<BalanceTypes, AccountBalances>  balancesMap = Map.of(BalanceTypes.CURRENT_BALANCE,createAccountBalances(8000, 1000,500),
                BalanceTypes.CASH_BALANCE, createAccountBalances(4000,0,0));

        AccountInfo accountInfo = createAccountInfo(accountId,BlockType.APPROVE,balancesMap,limitMap);

        accountInfo.persist().await().indefinitely();



        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());



        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardId,instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,"840","840",
                        balanceTypeList,limitTypeList).build();


        connectionServer.distributorProcessor.processMessage(identifierValidator);

        Thread.sleep(500);

        Optional<ValidationResponseSummary> validationResponseSummaryOptional = validationResponseSummaryList.stream()
                        .filter(validationResponseSummary -> validationResponseSummary.getMessageId().equals(identifierValidator.getMessageId()))
                                .findFirst();


        ValidationResponseSummary validationResponseSummary = validationResponseSummaryOptional.get();

        ValidationResponse validationResponseAccount = validationResponseSummary.getValidationResponseListList()
                        .stream().filter(validationResponse1 -> validationResponse1.getServiceId().equals(ServiceID.ACCOUNT_VALIDATOR))
                        .findFirst().get();


        ValidationResponse validationResponseCard = validationResponseSummary.getValidationResponseListList()
                .stream().filter(validationResponse1 -> validationResponse1.getServiceId().equals(ServiceID.CARD_VALIDATOR))
                .findFirst().get();

        Optional<ValidationResponse> validationResponseCustomerOptional = validationResponseSummary.getValidationResponseListList()
                .stream().filter(validationResponse1 -> validationResponse1.getServiceId().equals(ServiceID.CUSTOMER_VALIDATOR))
                .findFirst();


        assertAll(
                ()-> assertTrue(validationResponseSummaryOptional.isPresent()),
                ()-> assertEquals(identifierValidator.getMessageId(),validationResponseSummary.getMessageId()),
                ()-> assertEquals(ServiceNames.IDENTIFIER_SERVICE,validationResponseSummary.getMicroServiceId()),
                ()-> assertEquals(identifierValidator.getAggregatorInstance(),validationResponseSummary.getAggregatorContainerId()),
                ()-> assertEquals(2, validationResponseSummary.getValidationResponseListCount()),
                ()-> assertEquals(1,validationResponseAccount.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponseAccount.getValidationResponse(0)),

                ()-> assertEquals(1,validationResponseCard.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponseCard.getValidationResponse(0)),
                ()-> assertTrue(validationResponseCustomerOptional.isEmpty())

                );
    }




    @Test
    void sendMessageInvalidCurrencyCode() throws InterruptedException {


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = setupCustomer("test   name", "123 testAddress 456", "672019");
        String  instrument = UUID.randomUUID().toString().replace("-","");


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();


        List<TerminalType> terminalTypeListCard = new ArrayList<>();
        terminalTypeListCard.add(TerminalType.ATM);
        terminalTypeListCard.add(TerminalType.HOME_TERMINALS);
        List<PurchaseTypes> purchaseTypesListCard = List.of(PurchaseTypes.AIRLINE,PurchaseTypes.LODGING);
        List<TransactionType> transactionTypeListCard = List.of(TransactionType.ACCOUNT_FUND_TRANSACTION, TransactionType.OCT);
        List<LimitType> limitTypeListCard = List.of(LimitType.RETAIL, LimitType.OTC);
        List<EntryMode> entryModeListCard = List.of(EntryMode.ICC,EntryMode.MAG);

        List<LimitType> limitTypeList1Card = new ArrayList<>();
        limitTypeList1Card.add(LimitType.RETAIL);
        limitTypeList1Card.add(LimitType.NO_SPECIFIC);

        Map<PeriodicType,Map<LimitType,PeriodicCardAmount>> periodicTypeMapLimit = new HashMap<>();


        Map<LimitType, PeriodicCardAmount> cardLimitMapSingle= createProductTypeMap(500L, 1, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardLimitMapDaily = createProductTypeMap(1000L, 10, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardLimitMapMonthly= createProductTypeMap(10000L, 100, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardLimitMapYearly= createProductTypeMap(100000L, 1000, 1, List.of(LimitType.RETAIL,LimitType.ATM));

        periodicTypeMapLimit.put(PeriodicType.SINGLE,cardLimitMapSingle);
        periodicTypeMapLimit.put(PeriodicType.DAILY,cardLimitMapDaily);
        periodicTypeMapLimit.put(PeriodicType.MONTHLY,cardLimitMapMonthly);
        periodicTypeMapLimit.put(PeriodicType.YEARLY,cardLimitMapYearly);

        Map<PeriodicType,Map<LimitType,PeriodicCardAmount>> periodicTypeMapAmount = new HashMap<>();

        Map<LimitType, PeriodicCardAmount> cardAmountMapSingle= createProductTypeMap(0L, 0, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapDaily = createProductTypeMap(300L, 8, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapMonthly= createProductTypeMap(300L, 10, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapYearly= createProductTypeMap(99800L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));

        periodicTypeMapAmount.put(PeriodicType.SINGLE,cardAmountMapSingle);
        periodicTypeMapAmount.put(PeriodicType.DAILY,cardAmountMapDaily);
        periodicTypeMapAmount.put(PeriodicType.MONTHLY,cardAmountMapMonthly);
        periodicTypeMapAmount.put(PeriodicType.YEARLY,cardAmountMapYearly);

        CardsBasic cardsBasic = setupDataForTest(CardStatus.ACTIVE,BlockType.APPROVE,
                terminalTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                purchaseTypesListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                transactionTypeListCard, InternationalApplied.INTERNATIONAL,IncludeExclude.NOT_APPLICABLE,
                limitTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                entryModeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                false,false,false,periodicTypeMapLimit,periodicTypeMapAmount,1,201);

        String  cardId = cardsBasic.getCardId();


        cardsBasic.persist().await().indefinitely();

        Map<BalanceTypes,Long> limitMap = Map.of(BalanceTypes.CURRENT_BALANCE,10000L, BalanceTypes.CASH_BALANCE,5000L);
        Map<BalanceTypes, AccountBalances>  balancesMap = Map.of(BalanceTypes.CURRENT_BALANCE,createAccountBalances(8000, 1000,500),
                BalanceTypes.CASH_BALANCE, createAccountBalances(4000,0,0));

        AccountInfo accountInfo = createAccountInfo(accountId,BlockType.APPROVE,balancesMap,limitMap);

        accountInfo.persist().await().indefinitely();



        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());



        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardId,instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,"840","326",
                        balanceTypeList,limitTypeList).build();


        connectionServer.distributorProcessor.processMessage(identifierValidator);

        Thread.sleep(500);

        Optional<ValidationResponseSummary> validationResponseSummaryOptional = validationResponseSummaryList.stream()
                .filter(validationResponseSummary -> validationResponseSummary.getMessageId().equals(identifierValidator.getMessageId()))
                .findFirst();


        ValidationResponseSummary validationResponseSummary = validationResponseSummaryOptional.get();

        Optional<ValidationResponse> validationResponseAccountOptional = validationResponseSummary.getValidationResponseListList()
                .stream().filter(validationResponse1 -> validationResponse1.getServiceId().equals(ServiceID.ACCOUNT_VALIDATOR))
                .findFirst();


        Optional<ValidationResponse> validationResponseCardOptional = validationResponseSummary.getValidationResponseListList()
                .stream().filter(validationResponse1 -> validationResponse1.getServiceId().equals(ServiceID.CARD_VALIDATOR))
                .findFirst();

        Optional<ValidationResponse> validationResponseCustomerOptional = validationResponseSummary.getValidationResponseListList()
                .stream().filter(validationResponse1 -> validationResponse1.getServiceId().equals(ServiceID.CUSTOMER_VALIDATOR))
                .findFirst();

        Optional<ValidationResponse> validationResponseCurrencyOptional = validationResponseSummary.getValidationResponseListList()
                .stream().filter(validationResponse1 -> validationResponse1.getServiceId().equals(ServiceID.CURRENCY_CONVERSION))
                .findFirst();


        assertAll(
                ()-> assertTrue(validationResponseSummaryOptional.isPresent()),
                ()-> assertEquals(identifierValidator.getMessageId(),validationResponseSummary.getMessageId()),
                ()-> assertEquals(ServiceNames.IDENTIFIER_SERVICE,validationResponseSummary.getMicroServiceId()),
                ()-> assertEquals(identifierValidator.getAggregatorInstance(),validationResponseSummary.getAggregatorContainerId()),
                ()-> assertEquals(1, validationResponseSummary.getValidationResponseListCount()),
                ()-> assertTrue(validationResponseAccountOptional.isEmpty()),
                ()-> assertTrue(validationResponseCardOptional.isEmpty()),
                ()-> assertTrue(validationResponseCustomerOptional.isEmpty()),
                ()-> assertTrue(validationResponseCurrencyOptional.isPresent()),
                ()-> assertEquals(ServiceResponse.INVALID_CURRENCY_CODE.getServiceResponse(),validationResponseCurrencyOptional.get().getValidationResponse(0))



                );
    }


    @Test
    void sendMessageValidCurrencyCode() throws InterruptedException {


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = setupCustomer("test   name", "123 testAddress 456", "672019");
        String  instrument = UUID.randomUUID().toString().replace("-","");


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();


        List<TerminalType> terminalTypeListCard = new ArrayList<>();
        terminalTypeListCard.add(TerminalType.ATM);
        terminalTypeListCard.add(TerminalType.HOME_TERMINALS);
        List<PurchaseTypes> purchaseTypesListCard = List.of(PurchaseTypes.AIRLINE,PurchaseTypes.LODGING);
        List<TransactionType> transactionTypeListCard = List.of(TransactionType.ACCOUNT_FUND_TRANSACTION, TransactionType.OCT);
        List<LimitType> limitTypeListCard = List.of(LimitType.RETAIL, LimitType.OTC);
        List<EntryMode> entryModeListCard = List.of(EntryMode.ICC,EntryMode.MAG);

        List<LimitType> limitTypeList1Card = new ArrayList<>();
        limitTypeList1Card.add(LimitType.RETAIL);
        limitTypeList1Card.add(LimitType.NO_SPECIFIC);

        Map<PeriodicType,Map<LimitType,PeriodicCardAmount>> periodicTypeMapLimit = new HashMap<>();


        Map<LimitType, PeriodicCardAmount> cardLimitMapSingle= createProductTypeMap(500L, 1, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardLimitMapDaily = createProductTypeMap(1000L, 10, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardLimitMapMonthly= createProductTypeMap(10000L, 100, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardLimitMapYearly= createProductTypeMap(100000L, 1000, 1, List.of(LimitType.RETAIL,LimitType.ATM));

        periodicTypeMapLimit.put(PeriodicType.SINGLE,cardLimitMapSingle);
        periodicTypeMapLimit.put(PeriodicType.DAILY,cardLimitMapDaily);
        periodicTypeMapLimit.put(PeriodicType.MONTHLY,cardLimitMapMonthly);
        periodicTypeMapLimit.put(PeriodicType.YEARLY,cardLimitMapYearly);

        Map<PeriodicType,Map<LimitType,PeriodicCardAmount>> periodicTypeMapAmount = new HashMap<>();

        Map<LimitType, PeriodicCardAmount> cardAmountMapSingle= createProductTypeMap(0L, 0, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapDaily = createProductTypeMap(300L, 8, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapMonthly= createProductTypeMap(300L, 10, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapYearly= createProductTypeMap(99800L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));

        periodicTypeMapAmount.put(PeriodicType.SINGLE,cardAmountMapSingle);
        periodicTypeMapAmount.put(PeriodicType.DAILY,cardAmountMapDaily);
        periodicTypeMapAmount.put(PeriodicType.MONTHLY,cardAmountMapMonthly);
        periodicTypeMapAmount.put(PeriodicType.YEARLY,cardAmountMapYearly);

        CardsBasic cardsBasic = setupDataForTest(CardStatus.ACTIVE,BlockType.APPROVE,
                terminalTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                purchaseTypesListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                transactionTypeListCard, InternationalApplied.INTERNATIONAL,IncludeExclude.NOT_APPLICABLE,
                limitTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                entryModeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                false,false,false,periodicTypeMapLimit,periodicTypeMapAmount,1,201);

        String  cardId = cardsBasic.getCardId();


        cardsBasic.persist().await().indefinitely();

        Map<BalanceTypes,Long> limitMap = Map.of(BalanceTypes.CURRENT_BALANCE,10000L, BalanceTypes.CASH_BALANCE,5000L);
        Map<BalanceTypes, AccountBalances>  balancesMap = Map.of(BalanceTypes.CURRENT_BALANCE,createAccountBalances(8000, 1000,500),
                BalanceTypes.CASH_BALANCE, createAccountBalances(4000,0,0));

        AccountInfo accountInfo = createAccountInfo(accountId,BlockType.APPROVE,balancesMap,limitMap);

        accountInfo.persist().await().indefinitely();



        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());



        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardId,instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,"840","124",
                        balanceTypeList,limitTypeList).build();


        connectionServer.distributorProcessor.processMessage(identifierValidator);

        Thread.sleep(500);

        Optional<ValidationResponseSummary> validationResponseSummaryOptional = validationResponseSummaryList.stream()
                .filter(validationResponseSummary -> validationResponseSummary.getMessageId().equals(identifierValidator.getMessageId()))
                .findFirst();


        ValidationResponseSummary validationResponseSummary = validationResponseSummaryOptional.get();

        Optional<ValidationResponse> validationResponseAccountOptional = validationResponseSummary.getValidationResponseListList()
                .stream().filter(validationResponse1 -> validationResponse1.getServiceId().equals(ServiceID.ACCOUNT_VALIDATOR))
                .findFirst();


        Optional<ValidationResponse> validationResponseCardOptional = validationResponseSummary.getValidationResponseListList()
                .stream().filter(validationResponse1 -> validationResponse1.getServiceId().equals(ServiceID.CARD_VALIDATOR))
                .findFirst();

        Optional<ValidationResponse> validationResponseCustomerOptional = validationResponseSummary.getValidationResponseListList()
                .stream().filter(validationResponse1 -> validationResponse1.getServiceId().equals(ServiceID.CUSTOMER_VALIDATOR))
                .findFirst();

        Optional<ValidationResponse> validationResponseCurrencyOptional = validationResponseSummary.getValidationResponseListList()
                .stream().filter(validationResponse1 -> validationResponse1.getServiceId().equals(ServiceID.CURRENCY_CONVERSION))
                .findFirst();


        assertAll(
                ()-> assertTrue(validationResponseSummaryOptional.isPresent()),
                ()-> assertEquals(identifierValidator.getMessageId(),validationResponseSummary.getMessageId()),
                ()-> assertEquals(ServiceNames.IDENTIFIER_SERVICE,validationResponseSummary.getMicroServiceId()),
                ()-> assertEquals(identifierValidator.getAggregatorInstance(),validationResponseSummary.getAggregatorContainerId()),
                ()-> assertEquals(3, validationResponseSummary.getValidationResponseListCount()),
                ()-> assertEquals(1,validationResponseAccountOptional.get().getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponseAccountOptional.get().getValidationResponse(0)),

                ()-> assertEquals(1,validationResponseCardOptional.get().getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponseCardOptional.get().getValidationResponse(0)),
                ()-> assertTrue(validationResponseCustomerOptional.isEmpty()),
                ()-> assertTrue(validationResponseCurrencyOptional.isPresent()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponseCurrencyOptional.get().getValidationResponse(0))



        );
    }



    @Test
    void sendMessageValidCurrencyCodeCustomer() throws InterruptedException {


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = setupCustomer("test   name", "123 testAddress 456", "672019");
        String  instrument = UUID.randomUUID().toString().replace("-","");


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();


        List<TerminalType> terminalTypeListCard = new ArrayList<>();
        terminalTypeListCard.add(TerminalType.ATM);
        terminalTypeListCard.add(TerminalType.HOME_TERMINALS);
        List<PurchaseTypes> purchaseTypesListCard = List.of(PurchaseTypes.AIRLINE,PurchaseTypes.LODGING);
        List<TransactionType> transactionTypeListCard = List.of(TransactionType.ACCOUNT_FUND_TRANSACTION, TransactionType.OCT);
        List<LimitType> limitTypeListCard = List.of(LimitType.RETAIL, LimitType.OTC);
        List<EntryMode> entryModeListCard = List.of(EntryMode.ICC,EntryMode.MAG);

        List<LimitType> limitTypeList1Card = new ArrayList<>();
        limitTypeList1Card.add(LimitType.RETAIL);
        limitTypeList1Card.add(LimitType.NO_SPECIFIC);

        Map<PeriodicType,Map<LimitType,PeriodicCardAmount>> periodicTypeMapLimit = new HashMap<>();


        Map<LimitType, PeriodicCardAmount> cardLimitMapSingle= createProductTypeMap(500L, 1, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardLimitMapDaily = createProductTypeMap(1000L, 10, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardLimitMapMonthly= createProductTypeMap(10000L, 100, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardLimitMapYearly= createProductTypeMap(100000L, 1000, 1, List.of(LimitType.RETAIL,LimitType.ATM));

        periodicTypeMapLimit.put(PeriodicType.SINGLE,cardLimitMapSingle);
        periodicTypeMapLimit.put(PeriodicType.DAILY,cardLimitMapDaily);
        periodicTypeMapLimit.put(PeriodicType.MONTHLY,cardLimitMapMonthly);
        periodicTypeMapLimit.put(PeriodicType.YEARLY,cardLimitMapYearly);

        Map<PeriodicType,Map<LimitType,PeriodicCardAmount>> periodicTypeMapAmount = new HashMap<>();

        Map<LimitType, PeriodicCardAmount> cardAmountMapSingle= createProductTypeMap(0L, 0, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapDaily = createProductTypeMap(300L, 8, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapMonthly= createProductTypeMap(300L, 10, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapYearly= createProductTypeMap(99800L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));

        periodicTypeMapAmount.put(PeriodicType.SINGLE,cardAmountMapSingle);
        periodicTypeMapAmount.put(PeriodicType.DAILY,cardAmountMapDaily);
        periodicTypeMapAmount.put(PeriodicType.MONTHLY,cardAmountMapMonthly);
        periodicTypeMapAmount.put(PeriodicType.YEARLY,cardAmountMapYearly);

        CardsBasic cardsBasic = setupDataForTest(CardStatus.ACTIVE,BlockType.APPROVE,
                terminalTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                purchaseTypesListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                transactionTypeListCard, InternationalApplied.INTERNATIONAL,IncludeExclude.NOT_APPLICABLE,
                limitTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                entryModeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                false,false,false,periodicTypeMapLimit,periodicTypeMapAmount,1,201);

        String  cardId = cardsBasic.getCardId();


        cardsBasic.persist().await().indefinitely();

        Map<BalanceTypes,Long> limitMap = Map.of(BalanceTypes.CURRENT_BALANCE,10000L, BalanceTypes.CASH_BALANCE,5000L);
        Map<BalanceTypes, AccountBalances>  balancesMap = Map.of(BalanceTypes.CURRENT_BALANCE,createAccountBalances(8000, 1000,500),
                BalanceTypes.CASH_BALANCE, createAccountBalances(4000,0,0));

        AccountInfo accountInfo = createAccountInfo(accountId,BlockType.APPROVE,balancesMap,limitMap);

        accountInfo.persist().await().indefinitely();



        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());



        IdentifierValidator.Builder builder =
                createIdentifierValidator(containerId,accountId,cardId,instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,"840","124",
                        balanceTypeList,limitTypeList);

        IdentifierValidator identifierValidator = builder
                .setAddress("123 testAddress 456")
                        .setPostalCode("672019")
                                .build();

        connectionServer.distributorProcessor.processMessage(identifierValidator);

        Thread.sleep(500);

        Optional<ValidationResponseSummary> validationResponseSummaryOptional = validationResponseSummaryList.stream()
                .filter(validationResponseSummary -> validationResponseSummary.getMessageId().equals(identifierValidator.getMessageId()))
                .findFirst();


        ValidationResponseSummary validationResponseSummary = validationResponseSummaryOptional.get();

        Optional<ValidationResponse> validationResponseAccountOptional = validationResponseSummary.getValidationResponseListList()
                .stream().filter(validationResponse1 -> validationResponse1.getServiceId().equals(ServiceID.ACCOUNT_VALIDATOR))
                .findFirst();


        Optional<ValidationResponse> validationResponseCardOptional = validationResponseSummary.getValidationResponseListList()
                .stream().filter(validationResponse1 -> validationResponse1.getServiceId().equals(ServiceID.CARD_VALIDATOR))
                .findFirst();

        Optional<ValidationResponse> validationResponseCustomerOptional = validationResponseSummary.getValidationResponseListList()
                .stream().filter(validationResponse1 -> validationResponse1.getServiceId().equals(ServiceID.CUSTOMER_VALIDATOR))
                .findFirst();

        Optional<ValidationResponse> validationResponseCurrencyOptional = validationResponseSummary.getValidationResponseListList()
                .stream().filter(validationResponse1 -> validationResponse1.getServiceId().equals(ServiceID.CURRENCY_CONVERSION))
                .findFirst();


        assertAll(
                ()-> assertTrue(validationResponseSummaryOptional.isPresent()),
                ()-> assertEquals(identifierValidator.getMessageId(),validationResponseSummary.getMessageId()),
                ()-> assertEquals(ServiceNames.IDENTIFIER_SERVICE,validationResponseSummary.getMicroServiceId()),
                ()-> assertEquals(identifierValidator.getAggregatorInstance(),validationResponseSummary.getAggregatorContainerId()),
                ()-> assertEquals(4, validationResponseSummary.getValidationResponseListCount()),
                ()-> assertEquals(1,validationResponseAccountOptional.get().getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponseAccountOptional.get().getValidationResponse(0)),

                ()-> assertEquals(1,validationResponseCardOptional.get().getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponseCardOptional.get().getValidationResponse(0)),
                ()-> assertTrue(validationResponseCustomerOptional.isPresent()),
                ()-> assertEquals(1,validationResponseCustomerOptional.get().getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponseCustomerOptional.get().getValidationResponse(0)),
                ()-> assertTrue(validationResponseCurrencyOptional.isPresent()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponseCurrencyOptional.get().getValidationResponse(0))



        );
    }


    private String setupCustomer(String name, String address, String postalCode) {
        CustomerDef customerDef = CustomerDef.builder()
                .customerId(UUID.randomUUID().toString())
                .addressType(AddressType.HOME)
                .customerType(CustomerType.OWNER)
                .customerName(name)
                .addressLine(address)
                .postalCode(postalCode)
                .state("Kerala")
                .countryCode("356")
                .primaryEmail("testemail@test.com")
                .primaryPhoneNumber("+91 9543345455")
                .build();

        customerDef.persist().await().indefinitely();
        return customerDef.getCustomerId();

    }

    private AccountBalances createAccountBalances(long balance, long memoDb, long memoCr){
        return AccountBalances.builder()
                .postedBalance(balance)
                .memoDb(memoDb)
                .memoCr(memoCr)
                .build()
                ;
    }

    private AccountInfo createAccountInfo(String accountId, BlockType blockType, Map<BalanceTypes, AccountBalances>  balancesMap, Map<BalanceTypes,Long> limitMap ){


        return AccountInfo.builder()
                .accountId(accountId)
                .org(1)
                .product(201)
                .accountType(AccountType.CREDIT)
                .blockType(blockType)
                .limitsMap(limitMap)
                .balancesMap(balancesMap)
                .build();

    }



    IdentifierValidator.Builder createIdentifierValidator(String containerId, String accountId,
                                                          String cardId,
                                                          String instrument,
                                                          String customerId,
                                                          String aggregatorInstance,
                                                          TransactionType transactionType,
                                                          InstallmentType installmentType,
                                                          CashBack cashBack,
                                                          TerminalType terminalType,
                                                          PurchaseTypes purchaseTypes,
                                                          EntryMode entryMode,
                                                          International international,
                                                          long txnAmount,
                                                          int criteria,
                                                          int org,
                                                          int product,
                                                          String txnCurrencyCode,
                                                          String billingCurrencyCode,
                                                          List<String> balanceTypesList,
//                                                          long billAmount,
                                                          List<String> limitTypeList) {

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
//                .setAddress("1234,test address 78")
//                .setPostalCode("623456")
//                .setCustomerName("Test")
                .setAvsType(AVSType.AVS_NOT_PRESENT.getAvsType())
                .setTransactionAmount(txnAmount)
//                .setBillingAmount(billAmount)
                .setTransactionCurrencyCode(txnCurrencyCode)
                .setBillingCurrencyCode(billingCurrencyCode)
                .setTransactionType(transactionType.getTransactionType())
                .setCountryCode("USA")
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
//                .setPostalCode("682019")
                .setAcquirerId("123456789")
                .setAcquirerCountry("124")
                .setMerchantNumber("1234567")
                .setTerminalType(terminalType.getTerminalType())
                .setRecurringTrans(RecurringTrans.NOT_RECURRING_TRANS.getRecurringTrans())
                .setPurchaseTypes(purchaseTypes.getPurchaseTypes())
                .setEntryMode(entryMode.getEntryMode())
                .setExpiryDate("20210330")
                .setInternational(international.getInternational())
                .addAllBalanceTypes(balanceTypesList)
                .addAllLimitTypes(limitTypeList)
                .setCriteria(criteria)
                .setOrg(org)
                .setProduct(product)
                ;
    }

    private ProductAuthCriteria createProductAuthCriteria(Integer org, Integer product,
                                                          Integer criteria,
                                                          IncludeExclude includeExclude, Map<PeriodicType, Map<LimitType, PeriodicCardAmount>> periodicTypeMapMap, Strategy strategy) {

        ProductAuthCriteriaKey productAuthCriteriaKey = ProductAuthCriteriaKey.builder()
                .org(org)
                .product(product)
                .criteria(criteria)
                .build();

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
                        .strategy(strategy)
                ;

        if(periodicTypeMapMap != null){
            builder.cardLimitMap(periodicTypeMapMap);
        }

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


    private Map<LimitType, PeriodicCardAmount> createProductTypeMap(Long amount, Integer count, double percent, List<LimitType> limitTypeList ) {

        Map<LimitType, PeriodicCardAmount> limitAmountMap = new HashMap<>();


        limitTypeList.forEach(limitType -> limitAmountMap.put(limitType,PeriodicCardAmount.builder()
                .transactionNumber(Double.valueOf(count*percent).intValue())
                .transactionAmount(Double.valueOf(amount*percent).longValue())
                .limitType(LimitType.CASH)
                .build()));

        return limitAmountMap;
    }


    private CardsBasic.CardsBasicBuilder createCardBasic(String cardId, CardStatus cardStatus, BlockType blockType, int org, int product) {

        return CardsBasic.builder()
                .cardId(cardId)
                .cardholderType(CardHolderType.PRIMARY)
                .blockType(blockType)
                .cardStatus(cardStatus)
                .org(org)
                .product(product)
                .waiverDaysActivation(10)
                ;


    }

    private CardsBasic createCardBasicMin(int org, int product, String cardId, CardStatus cardStatus, BlockType blockType) {

        CardsBasic cardsBasic = new CardsBasic();
        cardsBasic.setCardId(cardId);
        cardsBasic.setCardholderType(CardHolderType.PRIMARY.getCardHolderType());
        cardsBasic.setBlockType(blockType.getBlockType());
        cardsBasic.setCardStatus(cardStatus.getCardStatus());
        cardsBasic.setOrg(org);
        cardsBasic.setProduct(product);
        cardsBasic.setWaiverDaysActivation(10);

        return cardsBasic;

    }


    private CardsBasic setupDataForTest(CardStatus cardStatus, BlockType blockType,
                                        List<TerminalType> terminalTypeList, InternationalApplied terminalInternational, IncludeExclude includeExcludeTerminal,
                                        List<PurchaseTypes> purchaseTypesList, InternationalApplied purchaseInternational, IncludeExclude includeExcludePurchase,
                                        List<TransactionType> transactionTypeList, InternationalApplied transactionTypeInternationnal, IncludeExclude includeExcludeTransaction,
                                        List<LimitType> limitTypeList, InternationalApplied limitInternational, IncludeExclude includeExcludeLimit,
                                        List<EntryMode> entryModeList, InternationalApplied entryInternational, IncludeExclude includeExcludeEntry,
                                        Boolean blockCashBack, Boolean blockInstallments, Boolean blockInternational, Map<PeriodicType,Map<LimitType,PeriodicCardAmount>> cardLimitMap, Map<PeriodicType,Map<LimitType,PeriodicCardAmount>> accumValuesMap, int org, int product) {


        CardsBasic.CardsBasicBuilder builder = createCardBasic(UUID.randomUUID().toString().replace("-", ""), cardStatus, blockType, org,product);
        if (blockCashBack != null) {
            builder.blockCashBack(blockCashBack);
        }
        if (blockInstallments != null) {
            builder.blockInstallments(blockInstallments);
        }

        if (blockInternational != null) {
            builder.blockInternational(blockInternational);
        }
        if (terminalTypeList != null) {

            Map<TerminalType,InternationalApplied> terminalBlockMap = new HashMap<>();

            terminalTypeList.forEach(terminalType -> terminalBlockMap.put(terminalType, terminalInternational));
            builder.blockTerminalType(terminalBlockMap);
            builder.includeExcludeBlockTerminal(includeExcludeTerminal);
        } else {
            builder.blockTerminalType(new HashMap<>());
            builder.includeExcludeBlockTerminal(IncludeExclude.NOT_APPLICABLE);
        }

        if (transactionTypeList != null) {
            Map<TransactionType,InternationalApplied> transactionBlockMap = new HashMap<>();
            transactionTypeList.forEach(transactionType -> transactionBlockMap.put(transactionType, transactionTypeInternationnal));
            builder.blockTransactionType(transactionBlockMap);
            builder.includeExcludeBlockTransactionType(includeExcludeTransaction);
        } else {
            builder.blockTransactionType(new HashMap<>());
            builder.includeExcludeBlockTransactionType(IncludeExclude.NOT_APPLICABLE);
        }

        if (purchaseTypesList != null) {

            Map<PurchaseTypes,InternationalApplied> purchaseBlockMap = new HashMap<>();
            purchaseTypesList.forEach(purchaseTypes -> purchaseBlockMap.put(purchaseTypes, purchaseInternational));
            builder.blockPurchaseTypes(purchaseBlockMap);
            builder.includeExcludeBlockPurchaseType(includeExcludePurchase);
        } else {
            builder.blockPurchaseTypes(new HashMap<>());
            builder.includeExcludeBlockPurchaseType(IncludeExclude.NOT_APPLICABLE);
        }

        if (entryModeList != null) {
            Map<EntryMode,InternationalApplied> entryModeBlockMap = new HashMap<>();
            entryModeList.forEach(entryMode -> entryModeBlockMap.put(entryMode, entryInternational));
            builder.blockEntryMode(entryModeBlockMap);
            builder.includeExcludeBlockEntryMode(includeExcludeEntry);
        } else {
            builder.blockEntryMode(new HashMap<>());
            builder.includeExcludeBlockEntryMode(IncludeExclude.NOT_APPLICABLE);
        }

        if (limitTypeList != null) {
            Map<LimitType,InternationalApplied> limitBlockMap = new HashMap<>();
            limitTypeList.forEach(limitType -> limitBlockMap.put(limitType, limitInternational));
            builder.blockingLimitType(limitBlockMap);
            builder.includeExcludeBlockLimitType(includeExcludeLimit);
        } else {
            builder.includeExcludeBlockLimitType(IncludeExclude.NOT_APPLICABLE);
            builder.blockingLimitType(new HashMap<>());
        }


        if(cardLimitMap != null){
            builder.periodicTypePeriodicCardLimitMap(cardLimitMap);
        }

        if(accumValuesMap != null){
            builder.periodicCardAccumulatedValueMap(accumValuesMap);
        }

        return builder.build();

    }


    private void createAggregatorStream() {

        aggregatorService.aggregatorStream(Multi.createFrom().emitter(multiEmitter -> {

            multiEmitter.emit(createRegistrationAggregator());
            aggregatorResponseMultiEmitter = multiEmitter;

        })).subscribe().with(validationResponseSummary -> {

            if(validationResponseSummary.hasRegistration()){

                registrationAggregator = validationResponseSummary.getRegistration();

            } else {
                validationResponseSummaryList.add(validationResponseSummary);
                aggregatorResponseMultiEmitter.emit(AggregatorResponse.newBuilder()
                        .setMessageId(validationResponseSummary.getMessageId())
                        .setCompleted(true).build());

            }

        })

        ;
    }

    private AggregatorResponse createRegistrationAggregator(){

        return AggregatorResponse.newBuilder()
                .setMessageId(UUID.randomUUID().toString().replace("-",""))
                .setRegistration(RegistrationAggregator.newBuilder()
                        .setServiceName(ServiceNames.AGGREGATOR_SERVICE)
                        .setServiceInstance(aggregatorInstance)
                        .build())
                .build()
                ;
    }


    private CurrencyConversionTable buildCurrencyTable(String sourceCurrCode, String destCurrCode, long rate, LocalDate conversionDate, NetworkType networkType){


        return CurrencyConversionTable.builder()
                .network(networkType)
                .currencyCode(sourceCurrCode)
                .sourceCurrencyNode(2)
                .destCurrencyCode(destCurrCode)
                .destinationCurrencyNode(2)
                .conversionDate(conversionDate)
                .midRate(rate)
                .sellRate(rate)
                .buyRate(rate)
                .build();

    }



}

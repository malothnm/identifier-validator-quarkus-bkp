package in.nmaloth.identifierValidator.services.cards;

import in.nmaloth.identifierValidator.model.ProductCriteria;
import in.nmaloth.identifierValidator.model.entity.card.CardsBasic;
import in.nmaloth.identifierValidator.model.entity.card.PeriodicCardAmount;
import in.nmaloth.identifierValidator.model.entity.product.MCCRange;
import in.nmaloth.identifierValidator.model.entity.product.ProductAuthCriteria;
import in.nmaloth.identifierValidator.model.entity.product.ProductAuthCriteriaKey;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.identifierValidator.services.card.CardsService;
import in.nmaloth.identifierValidator.services.parameters.ParameterService;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.account.BalanceTypes;
import in.nmaloth.payments.constants.card.CardHolderType;
import in.nmaloth.payments.constants.card.CardStatus;
import in.nmaloth.payments.constants.card.LimitType;
import in.nmaloth.payments.constants.card.PeriodicType;
import in.nmaloth.payments.constants.ids.ServiceID;
import in.nmaloth.payments.constants.instrument.CVM;
import in.nmaloth.payments.constants.products.*;
import in.nmaloth.testResource.GRPCWireResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@QuarkusTest
@QuarkusTestResource(GRPCWireResource.class)
public class CardsService1Test {

    private Logger logger = LoggerFactory.getLogger(CardsService1Test.class);


    @Inject
    CardsService cardsService;

    @Inject
    ParameterService parameterService;


    @BeforeEach
    void setup(){

       Uni<List<ProductAuthCriteria>> productAuthCriteriaListUni =  ProductAuthCriteria.listAll();
       productAuthCriteriaListUni.await().indefinitely()
               .forEach(productAuthCriteria -> productAuthCriteria.delete().await().indefinitely());




    }


    @Test
    void basicTestNoControls(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();


        CardsBasic cardsBasic = createCardBasicMin(1,201,UUID.randomUUID().toString().replace("-",""),CardStatus.ACTIVE,BlockType.APPROVE);
        cardsBasic.persist().await().indefinitely();

        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.CASH,
                        InstallmentType.NO_INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.RESTAURANT,
                        EntryMode.ICC,
                        International.DOMESTIC,
                        1000L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,2000L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponse.getValidationResponse(0))
        );





    }



    @Test
    void basicTestInvalidCardStatus(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();


        CardsBasic cardsBasic = createCardBasicMin(1,201,UUID.randomUUID().toString().replace("-",""),CardStatus.FRAUD,BlockType.APPROVE);
        cardsBasic.persist().await().indefinitely();

        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.CASH,
                        InstallmentType.NO_INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.RESTAURANT,
                        EntryMode.ICC,
                        International.DOMESTIC,
                        1000L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

       ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,2000L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.STATUS.getServiceResponse(), validationResponse.getValidationResponse(0))

                );

    }

    @Test
    void basicTestInvalidBlockCode(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();


        CardsBasic cardsBasic = createCardBasicMin(1,201,UUID.randomUUID().toString().replace("-",""),CardStatus.ACTIVE,BlockType.BLOCK_DECLINE);
        cardsBasic.persist().await().indefinitely();

        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.CASH,
                        InstallmentType.NO_INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.RESTAURANT,
                        EntryMode.ICC,
                        International.DOMESTIC,
                        1000L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,2000L).await().indefinitely();


        assertAll(

                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.BLK.getServiceResponse(), validationResponse.getValidationResponse(0))

        );

    }


    @Test
    void testCardValuesNoLimitsApprove(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        List<TerminalType> terminalTypeListCard = new ArrayList<>();
        terminalTypeListCard.add(TerminalType.ATM);
        terminalTypeListCard.add(TerminalType.HOME_TERMINALS);
        List<PurchaseTypes> purchaseTypesListCard = new ArrayList<>();
        List<TransactionType> transactionTypeListCard = new ArrayList<>();
        List<LimitType> limitTypeListCard = new ArrayList<>();
        List<EntryMode> entryModeListCard = new ArrayList<>();

        List<LimitType> limitTypeList1Card = new ArrayList<>();
        limitTypeList1Card.add(LimitType.RETAIL);
        limitTypeList1Card.add(LimitType.NO_SPECIFIC);


        CardsBasic cardsBasic = setupDataForTest(CardStatus.ACTIVE,BlockType.APPROVE,
                terminalTypeListCard,InternationalApplied.DOMESTIC,IncludeExclude.NOT_APPLICABLE,
                purchaseTypesListCard,null,IncludeExclude.NOT_APPLICABLE,
                transactionTypeListCard, null,IncludeExclude.NOT_APPLICABLE,
                limitTypeListCard,null,IncludeExclude.NOT_APPLICABLE,
                entryModeListCard,null,IncludeExclude.NOT_APPLICABLE,
                false,false,false,new HashMap<>(),new HashMap<>(),1,201);

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.CASH,
                        InstallmentType.NO_INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.RESTAURANT,
                        EntryMode.ICC,
                        International.DOMESTIC,
                        1000L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,2000L).await().indefinitely();

        assertAll(

                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(), validationResponse.getValidationResponse(0))


        );

    }


    @Test
    void testCardValuesNoLimitsDeclineTransactionType(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        List<TerminalType> terminalTypeListCard = new ArrayList<>();
        terminalTypeListCard.add(TerminalType.ATM);
        terminalTypeListCard.add(TerminalType.HOME_TERMINALS);
        List<PurchaseTypes> purchaseTypesListCard = new ArrayList<>();
        List<TransactionType> transactionTypeListCard = List.of(TransactionType.ACCOUNT_FUND_TRANSACTION, TransactionType.OCT);
        List<LimitType> limitTypeListCard = new ArrayList<>();
        List<EntryMode> entryModeListCard = new ArrayList<>();

        List<LimitType> limitTypeList1Card = new ArrayList<>();
        limitTypeList1Card.add(LimitType.RETAIL);
        limitTypeList1Card.add(LimitType.NO_SPECIFIC);


        CardsBasic cardsBasic = setupDataForTest(CardStatus.ACTIVE,BlockType.APPROVE,
                terminalTypeListCard,InternationalApplied.DOMESTIC,IncludeExclude.NOT_APPLICABLE,
                purchaseTypesListCard,null,IncludeExclude.NOT_APPLICABLE,
                transactionTypeListCard, InternationalApplied.INTERNATIONAL,IncludeExclude.EXCLUDE,
                limitTypeListCard,null,IncludeExclude.NOT_APPLICABLE,
                entryModeListCard,null,IncludeExclude.NOT_APPLICABLE,
                false,false,false,new HashMap<>(),new HashMap<>(),1,201);

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.NO_INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.RESTAURANT,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        1000L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,2000L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.TRANSACTION_TYPE.getServiceResponse(),validationResponse.getValidationResponse(0))


        );

    }



    @Test
    void testCardValuesNoLimitsDeclineTerminalType(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        List<TerminalType> terminalTypeListCard = new ArrayList<>();
        terminalTypeListCard.add(TerminalType.ATM);
        terminalTypeListCard.add(TerminalType.HOME_TERMINALS);
        List<PurchaseTypes> purchaseTypesListCard = new ArrayList<>();
        List<TransactionType> transactionTypeListCard = List.of(TransactionType.ACCOUNT_FUND_TRANSACTION, TransactionType.OCT);
        List<LimitType> limitTypeListCard = new ArrayList<>();
        List<EntryMode> entryModeListCard = new ArrayList<>();

        List<LimitType> limitTypeList1Card = new ArrayList<>();
        limitTypeList1Card.add(LimitType.RETAIL);
        limitTypeList1Card.add(LimitType.NO_SPECIFIC);


        CardsBasic cardsBasic = setupDataForTest(CardStatus.ACTIVE,BlockType.APPROVE,
                terminalTypeListCard,InternationalApplied.BOTH,IncludeExclude.EXCLUDE,
                purchaseTypesListCard,null,IncludeExclude.NOT_APPLICABLE,
                transactionTypeListCard, InternationalApplied.INTERNATIONAL,IncludeExclude.NOT_APPLICABLE,
                limitTypeListCard,null,IncludeExclude.NOT_APPLICABLE,
                entryModeListCard,null,IncludeExclude.NOT_APPLICABLE,
                false,false,false,new HashMap<>(),new HashMap<>(),1,201);

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.NO_INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.RESTAURANT,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        1000L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,2000L).await().indefinitely();

        assertAll(

                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.TERMINAL_TYPE.getServiceResponse(),validationResponse.getValidationResponse(0))

        );

    }


    @Test
    void testCardValuesNoLimitsDeclinePurchaseType(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        List<TerminalType> terminalTypeListCard = new ArrayList<>();
        terminalTypeListCard.add(TerminalType.ATM);
        terminalTypeListCard.add(TerminalType.HOME_TERMINALS);
        List<PurchaseTypes> purchaseTypesListCard = List.of(PurchaseTypes.AIRLINE,PurchaseTypes.LODGING);
        List<TransactionType> transactionTypeListCard = List.of(TransactionType.ACCOUNT_FUND_TRANSACTION, TransactionType.OCT);
        List<LimitType> limitTypeListCard = new ArrayList<>();
        List<EntryMode> entryModeListCard = new ArrayList<>();

        List<LimitType> limitTypeList1Card = new ArrayList<>();
        limitTypeList1Card.add(LimitType.RETAIL);
        limitTypeList1Card.add(LimitType.NO_SPECIFIC);


        CardsBasic cardsBasic = setupDataForTest(CardStatus.ACTIVE,BlockType.APPROVE,
                terminalTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                purchaseTypesListCard,InternationalApplied.BOTH,IncludeExclude.EXCLUDE,
                transactionTypeListCard, InternationalApplied.INTERNATIONAL,IncludeExclude.NOT_APPLICABLE,
                limitTypeListCard,null,IncludeExclude.NOT_APPLICABLE,
                entryModeListCard,null,IncludeExclude.NOT_APPLICABLE,
                false,false,false,new HashMap<>(),new HashMap<>(),1,201);

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.NO_INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        1000L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,2000L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.PURCHASE_TYPE.getServiceResponse(),validationResponse.getValidationResponse(0))



        );

    }



    @Test
    void testCardValuesNoLimitsDeclineEntryMode(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        List<TerminalType> terminalTypeListCard = new ArrayList<>();
        terminalTypeListCard.add(TerminalType.ATM);
        terminalTypeListCard.add(TerminalType.HOME_TERMINALS);
        List<PurchaseTypes> purchaseTypesListCard = List.of(PurchaseTypes.AIRLINE,PurchaseTypes.LODGING);
        List<TransactionType> transactionTypeListCard = List.of(TransactionType.ACCOUNT_FUND_TRANSACTION, TransactionType.OCT);
        List<LimitType> limitTypeListCard = new ArrayList<>();
        List<EntryMode> entryModeListCard = List.of(EntryMode.ICC,EntryMode.MAG);

        List<LimitType> limitTypeList1Card = new ArrayList<>();
        limitTypeList1Card.add(LimitType.RETAIL);
        limitTypeList1Card.add(LimitType.NO_SPECIFIC);


        CardsBasic cardsBasic = setupDataForTest(CardStatus.ACTIVE,BlockType.APPROVE,
                terminalTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                purchaseTypesListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                transactionTypeListCard, InternationalApplied.INTERNATIONAL,IncludeExclude.NOT_APPLICABLE,
                limitTypeListCard,null,IncludeExclude.NOT_APPLICABLE,
                entryModeListCard,InternationalApplied.BOTH,IncludeExclude.EXCLUDE,
                false,false,false,new HashMap<>(),new HashMap<>(),1,201);

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.NO_INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        1000L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,2000L).await().indefinitely();

        assertAll(

                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.ENTRY_MODE.getServiceResponse(),validationResponse.getValidationResponse(0))


        );

    }


    @Test
    void testCardValuesNoLimitsDeclineLimitType(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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


        CardsBasic cardsBasic = setupDataForTest(CardStatus.ACTIVE,BlockType.APPROVE,
                terminalTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                purchaseTypesListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                transactionTypeListCard, InternationalApplied.INTERNATIONAL,IncludeExclude.NOT_APPLICABLE,
                limitTypeListCard,InternationalApplied.BOTH,IncludeExclude.EXCLUDE,
                entryModeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                false,false,false,new HashMap<>(),new HashMap<>(),1,201);

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.NO_INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        1000L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,2000L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.CARD_LIMIT.getServiceResponse(),validationResponse.getValidationResponse(0))

        );

    }


    @Test
    void testCardValuesNoLimitsDeclineCashBack(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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


        CardsBasic cardsBasic = setupDataForTest(CardStatus.ACTIVE,BlockType.APPROVE,
                terminalTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                purchaseTypesListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                transactionTypeListCard, InternationalApplied.INTERNATIONAL,IncludeExclude.NOT_APPLICABLE,
                limitTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                entryModeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                true,false,false,new HashMap<>(),new HashMap<>(),1,201);


        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.NO_INSTALLMENT_TYPE,
                        CashBack.CASH_BACK_PRESENT,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        1000L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

       ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,2000L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.CASH_BACK.getServiceResponse(),validationResponse.getValidationResponse(0))


        );

    }


    @Test
    void testCardValuesNoLimitsDeclineInstallment(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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


        CardsBasic cardsBasic = setupDataForTest(CardStatus.ACTIVE,BlockType.APPROVE,
                terminalTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                purchaseTypesListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                transactionTypeListCard, InternationalApplied.INTERNATIONAL,IncludeExclude.NOT_APPLICABLE,
                limitTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                entryModeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                false,true,false,new HashMap<>(),new HashMap<>(),1,201);

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        1000L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,2000L).await().indefinitely();

        assertAll(

                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.INSTALLMENT.getServiceResponse(),validationResponse.getValidationResponse(0))

        );

    }

    @Test
    void testCardValuesNoLimitsDeclineInternational(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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


        CardsBasic cardsBasic = setupDataForTest(CardStatus.ACTIVE,BlockType.APPROVE,
                terminalTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                purchaseTypesListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                transactionTypeListCard, InternationalApplied.INTERNATIONAL,IncludeExclude.NOT_APPLICABLE,
                limitTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                entryModeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                false,false,true,new HashMap<>(),new HashMap<>(),1,201);

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        1000L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,2000L).await().indefinitely();

        assertAll(

                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.INTERNATIONAL.getServiceResponse(),validationResponse.getValidationResponse(0))


        );

    }


    @Test
    void testCardValuesNoLimitsApproveLimits(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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
        Map<LimitType, PeriodicCardAmount> cardAmountMapDaily = createProductTypeMap(0L, 0, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapMonthly= createProductTypeMap(0L, 0, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapYearly= createProductTypeMap(0L, 0, 1, List.of(LimitType.RETAIL,LimitType.ATM));

        periodicTypeMapAmount.put(PeriodicType.SINGLE,cardAmountMapSingle);
        periodicTypeMapAmount.put(PeriodicType.SINGLE,cardAmountMapDaily);
        periodicTypeMapAmount.put(PeriodicType.MONTHLY,cardAmountMapMonthly);
        periodicTypeMapAmount.put(PeriodicType.YEARLY,cardAmountMapYearly);

        CardsBasic cardsBasic = setupDataForTest(CardStatus.ACTIVE,BlockType.APPROVE,
                terminalTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                purchaseTypesListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                transactionTypeListCard, InternationalApplied.INTERNATIONAL,IncludeExclude.NOT_APPLICABLE,
                limitTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                entryModeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                false,false,false,periodicTypeMapLimit,periodicTypeMapAmount,1,201);

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,100L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponse.getValidationResponse(0))        );

    }


    @Test
    void testCardValuesNoLimitsApproveLimitsSingle(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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
        Map<LimitType, PeriodicCardAmount> cardAmountMapDaily = createProductTypeMap(0L, 0, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapMonthly= createProductTypeMap(0L, 0, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapYearly= createProductTypeMap(0L, 0, 1, List.of(LimitType.RETAIL,LimitType.ATM));

        periodicTypeMapAmount.put(PeriodicType.SINGLE,cardAmountMapSingle);
        periodicTypeMapAmount.put(PeriodicType.SINGLE,cardAmountMapDaily);
        periodicTypeMapAmount.put(PeriodicType.MONTHLY,cardAmountMapMonthly);
        periodicTypeMapAmount.put(PeriodicType.YEARLY,cardAmountMapYearly);

        CardsBasic cardsBasic = setupDataForTest(CardStatus.ACTIVE,BlockType.APPROVE,
                terminalTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                purchaseTypesListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                transactionTypeListCard, InternationalApplied.INTERNATIONAL,IncludeExclude.NOT_APPLICABLE,
                limitTypeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                entryModeListCard,InternationalApplied.BOTH,IncludeExclude.NOT_APPLICABLE,
                false,false,false,periodicTypeMapLimit,periodicTypeMapAmount,1,201);

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,600L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.CARD_LIMIT.getServiceResponse(),validationResponse.getValidationResponse(0))
        );

    }

    @Test
    void testCardValuesNoLimitsDeclineLimitsDaily(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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
        Map<LimitType, PeriodicCardAmount> cardAmountMapDaily = createProductTypeMap(800L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapMonthly= createProductTypeMap(1000L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapYearly= createProductTypeMap(1000L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));

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

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,300L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.CARD_LIMIT.getServiceResponse(),validationResponse.getValidationResponse(0))
        );

    }



    @Test
    void testCardValuesNoLimitsDeclineLimitsDailyReset(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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
        Map<LimitType, PeriodicCardAmount> cardAmountMapDaily = createProductTypeMap(800L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapMonthly= createProductTypeMap(1000L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapYearly= createProductTypeMap(1000L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));

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

        cardsBasic.setLimitsLastUpdated(LocalDateTime.now().minusDays(1));
        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,300L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponse.getValidationResponse(0))
        );

    }


    @Test
    void testCardValuesNoLimitsApproveLimitsCountDaily(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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
        Map<LimitType, PeriodicCardAmount> cardAmountMapDaily = createProductTypeMap(300L, 10, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapMonthly= createProductTypeMap(1000L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapYearly= createProductTypeMap(1000L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));

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

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse= cardsService.validateCards(identifierValidator,productCriteria,300L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.CARD_LIMIT.getServiceResponse(),validationResponse.getValidationResponse(0))

        );

    }


    @Test
    void testCardValuesNoLimitsApproveLimitsCountDailyReset(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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
        Map<LimitType, PeriodicCardAmount> cardAmountMapDaily = createProductTypeMap(300L, 10, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapMonthly= createProductTypeMap(1000L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapYearly= createProductTypeMap(1000L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));

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

        cardsBasic.setLimitsLastUpdated(LocalDateTime.now().minusDays(1));
        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,300L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponse.getValidationResponse(0))        );

    }


    @Test
    void testCardValuesNoLimitsApproveLimitsMonthly(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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
        Map<LimitType, PeriodicCardAmount> cardAmountMapMonthly= createProductTypeMap(9800L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapYearly= createProductTypeMap(1000L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));

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

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse= cardsService.validateCards(identifierValidator,productCriteria,300L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.CARD_LIMIT.getServiceResponse(),validationResponse.getValidationResponse(0))

        );

    }



    @Test
    void testCardValuesNoLimitsApproveLimitsMonthlyCount(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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
        Map<LimitType, PeriodicCardAmount> cardAmountMapMonthly= createProductTypeMap(9000L, 100, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapYearly= createProductTypeMap(1000L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));

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

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,300L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.CARD_LIMIT.getServiceResponse(),validationResponse.getValidationResponse(0))
        );

    }


    @Test
    void testCardValuesNoLimitsApproveLimitsMonthlyReset(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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
        Map<LimitType, PeriodicCardAmount> cardAmountMapMonthly= createProductTypeMap(9800L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapYearly= createProductTypeMap(1000L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));

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

        cardsBasic.setLimitsLastUpdated(LocalDateTime.now().minusMonths(1));
        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,300L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponse.getValidationResponse(0))
        );

    }



    @Test
    void testCardValuesNoLimitsApproveLimitsMonthlyCountReset(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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
        Map<LimitType, PeriodicCardAmount> cardAmountMapMonthly= createProductTypeMap(9000L, 100, 1, List.of(LimitType.RETAIL,LimitType.ATM));
        Map<LimitType, PeriodicCardAmount> cardAmountMapYearly= createProductTypeMap(1000L, 3, 1, List.of(LimitType.RETAIL,LimitType.ATM));

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

        cardsBasic.setLimitsLastUpdated(LocalDateTime.now().minusMonths(1));
        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,300L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponse.getValidationResponse(0))        );

    }


    @Test
    void testCardValuesNoLimitsApproveLimitsYearly(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,300L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.CARD_LIMIT.getServiceResponse(),validationResponse.getValidationResponse(0))

        );

    }


    @Test
    void testCardValuesNoLimitsApproveLimitsYearlyCount(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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
        Map<LimitType, PeriodicCardAmount> cardAmountMapYearly= createProductTypeMap(99500L, 1000, 1, List.of(LimitType.RETAIL,LimitType.ATM));

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

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

       ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,300L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.CARD_LIMIT.getServiceResponse(),validationResponse.getValidationResponse(0))

        );

    }

    @Test
    void testCardValuesNoLimitsApproveLimitsYearlyReset(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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

        cardsBasic.setLimitsLastUpdated(LocalDateTime.now().minusYears(1));

        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,300L).await().indefinitely();


        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponse.getValidationResponse(0))        );

    }


    @Test
    void testCardValuesNoLimitsApproveLimitsYearlyCountReset(){

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1,201,100,IncludeExclude.NOT_APPLICABLE,null,Strategy.CHAMPION);
        productAuthCriteria.persist().await().indefinitely();


        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

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
        Map<LimitType, PeriodicCardAmount> cardAmountMapYearly= createProductTypeMap(99500L, 1000, 1, List.of(LimitType.RETAIL,LimitType.ATM));

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

        cardsBasic.setLimitsLastUpdated(LocalDateTime.now().minusYears(1));
        cardsBasic.persist().await().indefinitely();


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());


        IdentifierValidator identifierValidator =
                createIdentifierValidator(containerId,accountId,cardsBasic.getCardId(),instrument,customerId,aggregatorInstance,
                        TransactionType.ACCOUNT_FUND_TRANSACTION,
                        InstallmentType.INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.AIRLINE,
                        EntryMode.ICC,
                        International.INTERNATIONAL,
                        100L,
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse validationResponse = cardsService.validateCards(identifierValidator,productCriteria,300L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.CARD_VALIDATOR,validationResponse.getServiceId()),
                ()-> assertEquals(1, validationResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),validationResponse.getValidationResponse(0))        );

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
                .setAddress("1234,test address 78")
                .setPostalCode("623456")
                .setCustomerName("Test")
                .setAvsType(AVSType.AVS_NOT_PRESENT.getAvsType())
                .setTransactionAmount(txnAmount)
//                .setBillingAmount(billAmount)
                .setTransactionCurrencyCode("840")
                .setBillingCurrencyCode("124")
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
                .setPostalCode("682019")
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


}

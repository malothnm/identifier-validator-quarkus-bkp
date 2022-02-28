package in.nmaloth.identifierValidator.services.cards;

import in.nmaloth.identifierValidator.model.entity.card.CacheTempAccum;
import in.nmaloth.identifierValidator.model.entity.temp.CardTempBalance;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.identifierValidator.services.card.CardTempService;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.account.BalanceTypes;
import in.nmaloth.payments.constants.card.LimitType;
import in.nmaloth.testResource.GRPCWireResource;
import io.quarkus.infinispan.client.Remote;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.infinispan.client.hotrod.RemoteCache;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(GRPCWireResource.class)
class CardTempServiceImplTest {


    @Inject
    CardTempService cardTempService;

    @Inject
    @Remote("cardTemp")
    RemoteCache<String, CardTempBalance> cardTempBalanceRemoteCache;


    @Test
    void getAllCardTempBalance() {

        String cardNumber = UUID.randomUUID().toString().replace("-","");

        CardTempBalance cardTempBalance1 = CardTempBalance.builder()
                .cardNumber(cardNumber)
                .id(UUID.randomUUID().toString().replace("-",""))
                .amount(1000L)
                .limitTypes(LimitType.RETAIL)
                .limitTypes(LimitType.ATM)
                .build();

        CardTempBalance cardTempBalance2 = CardTempBalance.builder()
                .cardNumber(cardNumber)
                .id(UUID.randomUUID().toString().replace("-",""))
                .amount(3000L)
                .limitTypes(LimitType.RETAIL)
                .limitTypes(LimitType.ATM)
                .build();

        cardTempBalanceRemoteCache.put(cardTempBalance1.getId(),cardTempBalance1,10, TimeUnit.SECONDS);
        cardTempBalanceRemoteCache.put(cardTempBalance2.getId(),cardTempBalance2,10, TimeUnit.SECONDS);

        List<CardTempBalance> cardTempBalanceList = cardTempService.getAllCardTempBalance(cardNumber).await().indefinitely();

        assertAll(
                ()-> assertEquals(2,cardTempBalanceList.size())
        );
    }

    @Test
    void getAllCardTempBalance_repeat() {

        String cardNumber = UUID.randomUUID().toString().replace("-","");

        CardTempBalance cardTempBalance1 = CardTempBalance.builder()
                .cardNumber(cardNumber)
                .id(UUID.randomUUID().toString().replace("-",""))
                .amount(1000L)
                .limitTypes(LimitType.RETAIL)
                .limitTypes(LimitType.ATM)
                .build();

        CardTempBalance cardTempBalance2 = CardTempBalance.builder()
                .cardNumber(cardNumber)
                .id(UUID.randomUUID().toString().replace("-",""))
                .amount(3000L)
                .limitTypes(LimitType.RETAIL)
                .limitTypes(LimitType.ATM)
                .build();

        cardTempBalanceRemoteCache.put(cardTempBalance1.getId(),cardTempBalance1,10, TimeUnit.SECONDS);
        cardTempBalanceRemoteCache.put(cardTempBalance1.getId(),cardTempBalance2,10, TimeUnit.SECONDS);

        List<CardTempBalance> cardTempBalanceList = cardTempService.getAllCardTempBalance(cardNumber).await().indefinitely();

        assertAll(
                ()-> assertEquals(1,cardTempBalanceList.size())
        );
    }

    @Test
    void testGetAllCardTempBalance() {


        String containerId = UUID.randomUUID().toString().replace("-","");
        String accountId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
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
                createIdentifierValidator(containerId,accountId,cardId,instrument,customerId,aggregatorInstance,
                        TransactionType.CASH,
                        InstallmentType.NO_INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.RESTAURANT,
                        EntryMode.ICC,
                        International.DOMESTIC,
                        1000L,
                        balanceTypeList,limitTypeList).build();


        Map<String, CacheTempAccum>  cacheTempAccumMap =
                cardTempService.getAllCardTempBalance(identifierValidator,2000L).await().indefinitely();

        CacheTempAccum cacheTempAccumRetail = cacheTempAccumMap.get(LimitType.RETAIL.getLimitType());
        CacheTempAccum cacheTempAccumOTC = cacheTempAccumMap.get(LimitType.OTC.getLimitType());

        assertAll(
                ()-> assertEquals(1, cacheTempAccumRetail.getAccumCount()),
                ()-> assertEquals(2000, cacheTempAccumRetail.getAccumAmount()),
                ()-> assertEquals(1, cacheTempAccumOTC.getAccumCount()),
                ()-> assertEquals(2000, cacheTempAccumOTC.getAccumAmount())

        );


    }



    @Test
    void testGetAllCardTempBalance_mutiple() {

        String cardId = UUID.randomUUID().toString().replace("-","");


        CardTempBalance cardTempBalance = CardTempBalance.builder()
                .cardNumber(cardId)
                .id(UUID.randomUUID().toString().replace("-",""))
                .amount(1000L)
                .limitTypes(LimitType.RETAIL)
                .limitTypes(LimitType.ATM)
                .build();

        cardTempService.updateCardTempBalance(cardTempBalance).await().indefinitely();



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
                createIdentifierValidator(containerId,accountId,cardId,instrument,customerId,aggregatorInstance,
                        TransactionType.CASH,
                        InstallmentType.NO_INSTALLMENT_TYPE,
                        CashBack.NO_CASH_BACK,
                        TerminalType.ATM,
                        PurchaseTypes.RESTAURANT,
                        EntryMode.ICC,
                        International.DOMESTIC,
                        1000L,
                        balanceTypeList,limitTypeList).build();


        Map<String, CacheTempAccum>  cacheTempAccumMap =
                cardTempService.getAllCardTempBalance(identifierValidator,2000L).await().indefinitely();

        CacheTempAccum cacheTempAccumRetail = cacheTempAccumMap.get(LimitType.RETAIL.getLimitType());
        CacheTempAccum cacheTempAccumOTC = cacheTempAccumMap.get(LimitType.OTC.getLimitType());
        CacheTempAccum cacheTempAccumATM = cacheTempAccumMap.get(LimitType.ATM.getLimitType());

        assertAll(
                ()-> assertEquals(2, cacheTempAccumRetail.getAccumCount()),
                ()-> assertEquals(3000, cacheTempAccumRetail.getAccumAmount()),
                ()-> assertEquals(1, cacheTempAccumOTC.getAccumCount()),
                ()-> assertEquals(2000, cacheTempAccumOTC.getAccumAmount()),
                ()-> assertEquals(1, cacheTempAccumATM.getAccumCount()),
                ()-> assertEquals(1000, cacheTempAccumATM.getAccumAmount())

        );


    }



    @Test
    void createNewCardTempBalance() {

        CardTempBalance cardTempBalance = CardTempBalance.builder()
                .cardNumber(UUID.randomUUID().toString().replace("-",""))
                .id(UUID.randomUUID().toString().replace("-",""))
                .amount(1000L)
                .limitTypes(LimitType.RETAIL)
                .limitTypes(LimitType.ATM)
                .build();

        cardTempService.updateCardTempBalance(cardTempBalance).await().indefinitely();

        CardTempBalance cardTempBalance1 = cardTempBalanceRemoteCache.get(cardTempBalance.getId());

        assertAll(
                ()-> assertNotNull(cardTempBalance1)
        );

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
                ;
    }
}
package in.nmaloth.identifierValidator.services.account;

import in.nmaloth.identifierValidator.model.entity.temp.AccountTempBalance;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
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
class AccountTempServiceImplTest {

    @Inject
    AccountTempService accountTempService;

    @Inject
    @Remote("accountTemp")
    RemoteCache<String, AccountTempBalance> accountTempBalanceRemoteCache;


    @Test
    void getAllAccountTempBalance() {

        String accountNumber = UUID.randomUUID().toString().replace("-","");

        AccountTempBalance accountTempBalance1 = AccountTempBalance.builder()
                .cardNumber(accountNumber)
                .id(UUID.randomUUID().toString().replace("-",""))
                .amount(1000L)
                .balanceTypes(BalanceTypes.CURRENT_BALANCE)
                .balanceTypes(BalanceTypes.CASH_BALANCE)
                .build();

        AccountTempBalance accountTempBalance2 = AccountTempBalance.builder()
                .cardNumber(accountNumber)
                .id(UUID.randomUUID().toString().replace("-",""))
                .amount(3000L)
                .balanceTypes(BalanceTypes.CURRENT_BALANCE)
                .balanceTypes(BalanceTypes.INSTALLMENT_BALANCE)
                .build();

        accountTempBalanceRemoteCache.put(accountTempBalance1.getId(),accountTempBalance1,10, TimeUnit.SECONDS);
        accountTempBalanceRemoteCache.put(accountTempBalance2.getId(),accountTempBalance1,10, TimeUnit.SECONDS);

        List<AccountTempBalance> cardTempBalanceList = accountTempService.getAllAccountTempBalance(accountNumber).await().indefinitely();

        assertAll(
                ()-> assertEquals(2,cardTempBalanceList.size())
        );
    }

    @Test
    void getAllAccountTempBalance_repeat() {

        String cardNumber = UUID.randomUUID().toString().replace("-","");

        AccountTempBalance accountTempBalance1 = AccountTempBalance.builder()
                .cardNumber(cardNumber)
                .id(UUID.randomUUID().toString().replace("-",""))
                .amount(1000L)
                .balanceTypes(BalanceTypes.CURRENT_BALANCE)
                .balanceTypes(BalanceTypes.CASH_BALANCE)
                .build();

        AccountTempBalance accountTempBalance2 = AccountTempBalance.builder()
                .cardNumber(cardNumber)
                .id(UUID.randomUUID().toString().replace("-",""))
                .amount(3000L)
                .balanceTypes(BalanceTypes.CURRENT_BALANCE)
                .balanceTypes(BalanceTypes.INSTALLMENT_BALANCE)
                .build();

        accountTempBalanceRemoteCache.put(accountTempBalance1.getId(),accountTempBalance1,10, TimeUnit.SECONDS);
        accountTempBalanceRemoteCache.put(accountTempBalance1.getId(),accountTempBalance2,10, TimeUnit.SECONDS);

        List<AccountTempBalance> accountTempBalanceList = accountTempService.getAllAccountTempBalance(cardNumber).await().indefinitely();

        assertAll(
                ()-> assertEquals(1,accountTempBalanceList.size())
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


        Map<String, Long> cacheBalanceMap =
                accountTempService.getAllAccountTempBalance(identifierValidator,2000L).await().indefinitely();

        Long currentBalance = cacheBalanceMap.get(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        Long cashBalance = cacheBalanceMap.get(BalanceTypes.CASH_BALANCE.getBalanceTypes());

        assertAll(
                ()-> assertEquals(2000, currentBalance),
                ()-> assertEquals(2000, cashBalance)

        );


    }



    @Test
    void testGetAllCardTempBalance_mutiple() {

        String accountId = UUID.randomUUID().toString().replace("-","");


        AccountTempBalance accountTempBalance = AccountTempBalance.builder()
                .cardNumber(accountId)
                .id(UUID.randomUUID().toString().replace("-",""))
                .amount(1000L)
                .balanceTypes(BalanceTypes.CURRENT_BALANCE)
                .balanceTypes(BalanceTypes.CASH_BALANCE)
                .build();

        accountTempService.updateAccountTempBalance(accountTempBalance).await().indefinitely();



        String containerId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
        String customerId = UUID.randomUUID().toString().replace("-","");
        String  instrument = UUID.randomUUID().toString().replace("-","");
        String  aggregatorInstance = UUID.randomUUID().toString().replace("-","");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.INSTALLMENT_BALANCE.getBalanceTypes());

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


        Map<String, Long>  cacheTBalanceMap =
                accountTempService.getAllAccountTempBalance(identifierValidator,2000L).await().indefinitely();

        Long currentBalance = cacheTBalanceMap.get(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        Long cashBalance = cacheTBalanceMap.get(BalanceTypes.CASH_BALANCE.getBalanceTypes());
        Long installmentBalance = cacheTBalanceMap.get(BalanceTypes.INSTALLMENT_BALANCE.getBalanceTypes());

        assertAll(
                ()-> assertEquals(3000, currentBalance),
                ()-> assertEquals(1000, cashBalance),
                ()-> assertEquals(2000, installmentBalance)

        );


    }



    @Test
    void createNewCardTempBalance() {

        AccountTempBalance accountTempBalance = AccountTempBalance.builder()
                .cardNumber(UUID.randomUUID().toString().replace("-",""))
                .id(UUID.randomUUID().toString().replace("-",""))
                .amount(1000L)
                .balanceTypes(BalanceTypes.CURRENT_BALANCE)
                .balanceTypes(BalanceTypes.CASH_BALANCE)
                .build();

        accountTempService.updateAccountTempBalance(accountTempBalance).await().indefinitely();

        AccountTempBalance accountTempBalance1 = accountTempBalanceRemoteCache.get(accountTempBalance.getId());

        assertAll(
                ()-> assertNotNull(accountTempBalance1)
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
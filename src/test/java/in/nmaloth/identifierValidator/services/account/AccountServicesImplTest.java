package in.nmaloth.identifierValidator.services.account;

import in.nmaloth.identifierValidator.model.AccountResponse;
import in.nmaloth.identifierValidator.model.entity.account.AccountBalances;
import in.nmaloth.identifierValidator.model.entity.account.AccountInfo;
import in.nmaloth.identifierValidator.model.entity.temp.AccountTempBalance;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.account.AccountType;
import in.nmaloth.payments.constants.account.BalanceTypes;
import in.nmaloth.payments.constants.card.LimitType;
import in.nmaloth.payments.constants.ids.FieldID;
import in.nmaloth.payments.constants.ids.ServiceID;
import in.nmaloth.testResource.GRPCWireResource;
import io.quarkus.infinispan.client.Remote;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.infinispan.client.hotrod.RemoteCache;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(GRPCWireResource.class)
class AccountServicesImplTest {


    @Inject
    AccountServices accountServices;

    @Inject
    @Remote("accountTemp")
    RemoteCache<String, AccountTempBalance> accountTempBalanceRemoteCache;



    @Test
    void validateAccountBlock() {

        assertAll(
                ()-> assertTrue(accountServices.validateAccountBlock(BlockType.APPROVE).isEmpty() ),
                ()-> assertTrue(accountServices.validateAccountBlock(BlockType.VIP_ALWAYS_APPROVE).isEmpty()),
                ()-> assertEquals(ServiceResponse.TEMP_BLK,accountServices.validateAccountBlock(BlockType.BLOCK_TEMP).get()),
                ()-> assertEquals(ServiceResponse.SUSPECT_FRAUD,accountServices.validateAccountBlock(BlockType.BLOCK_SUSPECTED_FRAUD).get()),
                ()-> assertEquals(ServiceResponse.PICK_UP,accountServices.validateAccountBlock(BlockType.BLOCK_PICKUP).get()),
                ()-> assertEquals(ServiceResponse.FRAUD,accountServices.validateAccountBlock(BlockType.BLOCK_FRAUD).get()),
                ()-> assertEquals(ServiceResponse.BLK,accountServices.validateAccountBlock(BlockType.BLOCK_DECLINE).get())

        );
    }

    @Test
    void validateLimit() {

        Map<String,Long> limitMap = Map.of(BalanceTypes.CURRENT_BALANCE.getBalanceTypes(),1000L, BalanceTypes.CASH_BALANCE.getBalanceTypes(),500L);
        Map<String, AccountBalances>  balancesMap = Map.of(BalanceTypes.CURRENT_BALANCE.getBalanceTypes(),createAccountBalances(800, 100,50),
        BalanceTypes.CASH_BALANCE.getBalanceTypes(), createAccountBalances(400,0,0));

        Map<String,Long> txnBalanceMap = Map.of(BalanceTypes.CURRENT_BALANCE.getBalanceTypes(),100L, BalanceTypes.CASH_BALANCE.getBalanceTypes(),100L);

        AccountResponse  accountResponse = accountServices.validateLimit( limitMap,  balancesMap, txnBalanceMap,new ArrayList<>(),AccountType.CREDIT.getAccountType());

        Map<String,Long> limitMap1 = Map.of(BalanceTypes.CURRENT_BALANCE.getBalanceTypes(),1000L, BalanceTypes.CASH_BALANCE.getBalanceTypes(),500L);
        Map<String, AccountBalances>  balancesMap1 = Map.of(BalanceTypes.CURRENT_BALANCE.getBalanceTypes(),createAccountBalances(800, 100,50),
                BalanceTypes.CASH_BALANCE.getBalanceTypes(), createAccountBalances(300,0,0));

        Map<String,Long> txnBalanceMap1 = Map.of(BalanceTypes.CURRENT_BALANCE.getBalanceTypes(),160L, BalanceTypes.CASH_BALANCE.getBalanceTypes(),160L);

        AccountResponse  accountResponse1 = accountServices.validateLimit( limitMap1,  balancesMap1, txnBalanceMap1,new ArrayList<>(), AccountType.CREDIT.getAccountType());

        Map<String,Long> limitMap2 = Map.of(BalanceTypes.CURRENT_BALANCE.getBalanceTypes(),1000L, BalanceTypes.CASH_BALANCE.getBalanceTypes(),500L);
        Map<String, AccountBalances>  balancesMap2 = Map.of(BalanceTypes.CURRENT_BALANCE.getBalanceTypes(),createAccountBalances(800, 100,50),
                BalanceTypes.CASH_BALANCE.getBalanceTypes(), createAccountBalances(400,0,0));

        Map<String,Long> txnBalanceMap2 = Map.of(BalanceTypes.CURRENT_BALANCE.getBalanceTypes(),120L, BalanceTypes.CASH_BALANCE.getBalanceTypes(),120L);

        AccountResponse accountResponse2 = accountServices.validateLimit( limitMap1,  balancesMap1, txnBalanceMap1,new ArrayList<>(),AccountType.CREDIT.getAccountType());

        assertAll(
                ()-> assertEquals(0, accountResponse.getResponseList().size()),
                ()-> assertEquals(ServiceResponse.ACCT_LIMIT.getServiceResponse(),accountResponse1.getResponseList().get(0)),
                ()-> assertEquals(ServiceResponse.ACCT_LIMIT.getServiceResponse(),accountResponse2.getResponseList().get(0))


        );

    }



    @Test
    void validateAccount_NoDecline() {


        String containerId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
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

        Map<BalanceTypes,Long> limitMap = Map.of(BalanceTypes.CURRENT_BALANCE,10000L, BalanceTypes.CASH_BALANCE,5000L);
        Map<BalanceTypes, AccountBalances>  balancesMap = Map.of(BalanceTypes.CURRENT_BALANCE,createAccountBalances(8000, 1000,500),
                BalanceTypes.CASH_BALANCE, createAccountBalances(4000,0,0));

        AccountInfo accountInfo = createAccountInfo(accountId,BlockType.APPROVE,balancesMap,limitMap);

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
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        AccountResponse accountResponse = accountServices.validateAccount(identifierValidator,accountInfo,1000L).await().indefinitely();

        assertAll(
                ()-> assertEquals(0,accountResponse.getResponseList().size()),
                ()-> assertEquals(10000, accountResponse.getCreditLimit()),
                ()-> assertEquals(9500,accountResponse.getBalance()),
                ()-> assertEquals(500, accountResponse.getOtb())
        );
    }


    @Test
    void validateAccount_DeclineBlock() {


        String containerId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
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

        Map<BalanceTypes,Long> limitMap = Map.of(BalanceTypes.CURRENT_BALANCE,10000L, BalanceTypes.CASH_BALANCE,5000L);
        Map<BalanceTypes, AccountBalances>  balancesMap = Map.of(BalanceTypes.CURRENT_BALANCE,createAccountBalances(8000, 1000,500),
                BalanceTypes.CASH_BALANCE, createAccountBalances(4000,0,0));

        AccountInfo accountInfo = createAccountInfo(accountId,BlockType.BLOCK_DECLINE,balancesMap,limitMap);

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
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        AccountResponse accountResponse= accountServices.validateAccount(identifierValidator,accountInfo,1000L).await().indefinitely();

        assertAll(
                ()-> assertEquals(1,accountResponse.getResponseList().size()),
                ()-> assertEquals(ServiceResponse.BLK.getServiceResponse(),accountResponse.getResponseList().get(0)),
                ()-> assertEquals(10000, accountResponse.getCreditLimit()),
                ()-> assertEquals(9500,accountResponse.getBalance()),
                ()-> assertEquals(500, accountResponse.getOtb())
        );
    }

    @Test
    void validateAccount_DeclineLimit() {


        String containerId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
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

        Map<BalanceTypes,Long> limitMap = Map.of(BalanceTypes.CURRENT_BALANCE,10000L, BalanceTypes.CASH_BALANCE,5000L);
        Map<BalanceTypes, AccountBalances>  balancesMap = Map.of(BalanceTypes.CURRENT_BALANCE,createAccountBalances(8000, 1000,500),
                BalanceTypes.CASH_BALANCE, createAccountBalances(4000,0,0));

        AccountInfo accountInfo = createAccountInfo(accountId,BlockType.APPROVE,balancesMap,limitMap);

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
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        AccountResponse accountResponse = accountServices.validateAccount(identifierValidator,accountInfo,1600L).await().indefinitely();

        assertAll(
                ()-> assertEquals(1,accountResponse.getResponseList().size()),
                ()-> assertEquals(ServiceResponse.ACCT_LIMIT.getServiceResponse(),accountResponse.getResponseList().get(0)) ,
                ()-> assertEquals(10000, accountResponse.getCreditLimit()),
                ()-> assertEquals(10100,accountResponse.getBalance()),
                ()-> assertEquals(-100, accountResponse.getOtb())
        );


    }

    @Test
    void validateAccount_DeclineLimit_tempCache() {


        String containerId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
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

        AccountTempBalance accountTempBalance = AccountTempBalance.builder()
                .id(UUID.randomUUID().toString().replace("-",""))
                .cardNumber(accountId)
                .amount(1000L)
                .balanceTypes(BalanceTypes.CURRENT_BALANCE)
                .build();

        accountTempBalanceRemoteCache.put(accountTempBalance.getId(),accountTempBalance,10, TimeUnit.SECONDS);


        Map<BalanceTypes,Long> limitMap = Map.of(BalanceTypes.CURRENT_BALANCE,10000L, BalanceTypes.CASH_BALANCE,5000L);
        Map<BalanceTypes, AccountBalances>  balancesMap = Map.of(BalanceTypes.CURRENT_BALANCE,createAccountBalances(8000, 1000,500),
                BalanceTypes.CASH_BALANCE, createAccountBalances(4000,0,0));

        AccountInfo accountInfo = createAccountInfo(accountId,BlockType.APPROVE,balancesMap,limitMap);

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
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        AccountResponse accountResponse = accountServices.validateAccount(identifierValidator,accountInfo,600L).await().indefinitely();

        assertAll(
                ()-> assertEquals(1,accountResponse.getResponseList().size()),
                ()-> assertEquals(ServiceResponse.ACCT_LIMIT.getServiceResponse(),accountResponse.getResponseList().get(0)) ,
                ()-> assertEquals(10000, accountResponse.getCreditLimit()),
                ()-> assertEquals(10100,accountResponse.getBalance()),
                ()-> assertEquals(-100, accountResponse.getOtb())
        );
    }


    @Test
    void validateAccount() {


        String containerId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
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
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse accountResponse = accountServices.validateAccount(identifierValidator, 1000L).await().indefinitely();


        assertAll(
                ()-> assertEquals(ServiceID.ACCOUNT_VALIDATOR,accountResponse.getServiceId()),
                ()-> assertEquals(1,accountResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.NO_ENTRY.getServiceResponse(),accountResponse.getValidationResponse(0)) ,
                ()-> assertEquals(1, accountResponse.getServiceResponseFieldsMap().size())

        );

    }



    @Test
    void validateAccount_NoDecline_Account() {


        String containerId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
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

        Map<BalanceTypes,Long> limitMap = Map.of(BalanceTypes.CURRENT_BALANCE,10000L, BalanceTypes.CASH_BALANCE,5000L);
        Map<BalanceTypes, AccountBalances>  balancesMap = Map.of(BalanceTypes.CURRENT_BALANCE,createAccountBalances(8000, 1000,500),
                BalanceTypes.CASH_BALANCE, createAccountBalances(4000,0,0));

        AccountInfo accountInfo = createAccountInfo(accountId,BlockType.APPROVE,balancesMap,limitMap);
        accountInfo.persist().await().indefinitely();

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
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse accountResponse = accountServices.validateAccount(identifierValidator,1000L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.ACCOUNT_VALIDATOR,accountResponse.getServiceId()),
                ()-> assertEquals(1,accountResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.OK.getServiceResponse(),accountResponse.getValidationResponse(0)),
                ()-> assertEquals("10000", accountResponse.getServiceResponseFieldsMap().get(FieldID.CREDIT_LIMIT.getFieldId())),
                ()-> assertEquals("9500",accountResponse.getServiceResponseFieldsMap().get(FieldID.ACCOUNT_BALANCE.getFieldId())),
                ()-> assertEquals("500", accountResponse.getServiceResponseFieldsMap().get(FieldID.OTB.getFieldId())),
                ()-> assertEquals(AccountType.CREDIT.getAccountType(), accountResponse.getServiceResponseFieldsMap().get(FieldID.ACCOUNT_TYPE.getFieldId()))

                );
    }


    @Test
    void validateAccount_DeclineBlock_Account() {


        String containerId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
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

        Map<BalanceTypes,Long> limitMap = Map.of(BalanceTypes.CURRENT_BALANCE,10000L, BalanceTypes.CASH_BALANCE,5000L);
        Map<BalanceTypes, AccountBalances>  balancesMap = Map.of(BalanceTypes.CURRENT_BALANCE,createAccountBalances(8000, 1000,500),
                BalanceTypes.CASH_BALANCE, createAccountBalances(4000,0,0));

        AccountInfo accountInfo = createAccountInfo(accountId,BlockType.BLOCK_DECLINE,balancesMap,limitMap);

        accountInfo.persist().await().indefinitely();

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
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse accountResponse = accountServices.validateAccount(identifierValidator,1000L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.ACCOUNT_VALIDATOR,accountResponse.getServiceId()),
                ()-> assertEquals(1,accountResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.BLK.getServiceResponse(),accountResponse.getValidationResponse(0)),
                ()-> assertEquals("10000", accountResponse.getServiceResponseFieldsMap().get(FieldID.CREDIT_LIMIT.getFieldId())),
                ()-> assertEquals("9500",accountResponse.getServiceResponseFieldsMap().get(FieldID.ACCOUNT_BALANCE.getFieldId())),
                ()-> assertEquals("500", accountResponse.getServiceResponseFieldsMap().get(FieldID.OTB.getFieldId())),
                ()-> assertEquals(AccountType.CREDIT.getAccountType(), accountResponse.getServiceResponseFieldsMap().get(FieldID.ACCOUNT_TYPE.getFieldId()))

        );
    }

    @Test
    void validateAccount_DeclineLimit_account() {


        String containerId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
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

        Map<BalanceTypes,Long> limitMap = Map.of(BalanceTypes.CURRENT_BALANCE,10000L, BalanceTypes.CASH_BALANCE,5000L);
        Map<BalanceTypes, AccountBalances>  balancesMap = Map.of(BalanceTypes.CURRENT_BALANCE,createAccountBalances(8000, 1000,500),
                BalanceTypes.CASH_BALANCE, createAccountBalances(4000,0,0));

        AccountInfo accountInfo = createAccountInfo(accountId,BlockType.APPROVE,balancesMap,limitMap);
        accountInfo.persist().await().indefinitely();

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
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

       ValidationResponse accountResponse = accountServices.validateAccount(identifierValidator,1600L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.ACCOUNT_VALIDATOR,accountResponse.getServiceId()),
                ()-> assertEquals(1,accountResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.ACCT_LIMIT.getServiceResponse(),accountResponse.getValidationResponse(0)) ,
                ()-> assertEquals("10000", accountResponse.getServiceResponseFieldsMap().get(FieldID.CREDIT_LIMIT.getFieldId())),
                ()-> assertEquals("10100",accountResponse.getServiceResponseFieldsMap().get(FieldID.ACCOUNT_BALANCE.getFieldId())),
                ()-> assertEquals("-100", accountResponse.getServiceResponseFieldsMap().get(FieldID.OTB.getFieldId())),
                ()-> assertEquals(AccountType.CREDIT.getAccountType(), accountResponse.getServiceResponseFieldsMap().get(FieldID.ACCOUNT_TYPE.getFieldId()))

        );
    }

    @Test
    void validateAccount_DeclineLimit_tempCache_account() {


        String containerId = UUID.randomUUID().toString().replace("-","");
        String cardId = UUID.randomUUID().toString().replace("-","");
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

        AccountTempBalance accountTempBalance = AccountTempBalance.builder()
                .id(UUID.randomUUID().toString().replace("-",""))
                .cardNumber(accountId)
                .amount(1000L)
                .balanceTypes(BalanceTypes.CURRENT_BALANCE)
                .build();

        accountTempBalanceRemoteCache.put(accountTempBalance.getId(),accountTempBalance,10, TimeUnit.SECONDS);


        Map<BalanceTypes,Long> limitMap = Map.of(BalanceTypes.CURRENT_BALANCE,10000L, BalanceTypes.CASH_BALANCE,5000L);
        Map<BalanceTypes, AccountBalances>  balancesMap = Map.of(BalanceTypes.CURRENT_BALANCE,createAccountBalances(8000, 1000,500),
                BalanceTypes.CASH_BALANCE, createAccountBalances(4000,0,0));

        AccountInfo accountInfo = createAccountInfo(accountId,BlockType.APPROVE,balancesMap,limitMap);
        accountInfo.persist().await().indefinitely();

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
                        100,1,201,
                        balanceTypeList,limitTypeList).build();

        ValidationResponse accountResponse = accountServices.validateAccount(identifierValidator,600L).await().indefinitely();

        assertAll(
                ()-> assertEquals(ServiceID.ACCOUNT_VALIDATOR,accountResponse.getServiceId()),
                ()-> assertEquals(1,accountResponse.getValidationResponseCount()),
                ()-> assertEquals(ServiceResponse.ACCT_LIMIT.getServiceResponse(),accountResponse.getValidationResponse(0)) ,
                ()-> assertEquals("10000", accountResponse.getServiceResponseFieldsMap().get(FieldID.CREDIT_LIMIT.getFieldId())),
                ()-> assertEquals("10100",accountResponse.getServiceResponseFieldsMap().get(FieldID.ACCOUNT_BALANCE.getFieldId())),
                ()-> assertEquals("-100", accountResponse.getServiceResponseFieldsMap().get(FieldID.OTB.getFieldId())),
                ()-> assertEquals(AccountType.CREDIT.getAccountType(), accountResponse.getServiceResponseFieldsMap().get(FieldID.ACCOUNT_TYPE.getFieldId()))
        );
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
}
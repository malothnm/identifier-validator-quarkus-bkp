package in.nmaloth;

import in.nmaloth.identifierValidator.model.entity.account.AccountBalances;
import in.nmaloth.identifierValidator.model.entity.account.AccountInfo;
import in.nmaloth.identifierValidator.model.entity.card.CardsBasic;
import in.nmaloth.identifierValidator.model.entity.card.PeriodicCardAmount;
import in.nmaloth.identifierValidator.model.entity.customer.CustomerDef;
import in.nmaloth.identifierValidator.model.entity.product.BlockingValue;
import in.nmaloth.identifierValidator.model.entity.product.MCCRange;
import in.nmaloth.identifierValidator.model.entity.product.ProductAuthCriteria;
import in.nmaloth.identifierValidator.model.entity.product.ProductAuthCriteriaKey;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.account.AccountType;
import in.nmaloth.payments.constants.account.BalanceTypes;
import in.nmaloth.payments.constants.card.CardHolderType;
import in.nmaloth.payments.constants.card.CardStatus;
import in.nmaloth.payments.constants.card.LimitType;
import in.nmaloth.payments.constants.card.PeriodicType;
import in.nmaloth.payments.constants.customer.AddressType;
import in.nmaloth.payments.constants.customer.CustomerType;
import in.nmaloth.payments.constants.instrument.CVM;
import in.nmaloth.payments.constants.products.*;
import in.nmaloth.testResource.GRPCWireResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.tuples.Tuple2;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@QuarkusTest
@QuarkusTestResource(GRPCWireResource.class)
public class MongoTest {




    @Test
    public void setupAccount(){

        AccountInfo accountInfo = createAccountInfo(true,new ArrayList<>(),BlockType.APPROVE);
        accountInfo.persist().await().indefinitely();

        Optional<AccountInfo> accountInfoOptional = AccountInfo.findByAccountId(accountInfo.getAccountId()).await().indefinitely();

        assertTrue(accountInfoOptional.isPresent());

    }


    @Test
    public void setupCustomer(){

        CustomerDef customerDef = setupCustomer("test name"," test address 1", "1111111","test state","356");

        customerDef.persist().await().indefinitely();

        Optional<CustomerDef> customerDefOptional = CustomerDef.findByCustomerId(customerDef.getCustomerId())
                .await().indefinitely();

        assertTrue(customerDefOptional.isPresent());


    }


    @Test
    void setupProduct(){

        Map<PeriodicType,Map<LimitType,PeriodicCardAmount>> cardLimitMap = new HashMap<>();
        cardLimitMap.put(PeriodicType.SINGLE,createProductTypeMap(1000L, 10));
        cardLimitMap.put(PeriodicType.DAILY,createProductTypeMap(10000L, 100));
        cardLimitMap.put(PeriodicType.MONTHLY,createProductTypeMap(20000L,200));
        cardLimitMap.put(PeriodicType.YEARLY,createProductTypeMap(100000L,1000));
        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE,cardLimitMap,Strategy.CHAMPION);

        productAuthCriteria.persist().await().indefinitely();

        Optional<ProductAuthCriteria> productAuthCriteriaOptional = ProductAuthCriteria.findByProductId(1,201,100).await().indefinitely();

        assertTrue(productAuthCriteriaOptional.isPresent());

    }


    @Test
    void setupCards(){

        Map<PeriodicType,Map<LimitType,PeriodicCardAmount>> cardLimitMap = new HashMap<>();
        cardLimitMap.put(PeriodicType.SINGLE,createProductTypeMap(1000L, 10));
        cardLimitMap.put(PeriodicType.DAILY,createProductTypeMap(10000L, 100));
        cardLimitMap.put(PeriodicType.MONTHLY,createProductTypeMap(20000L,200));
        cardLimitMap.put(PeriodicType.YEARLY,createProductTypeMap(100000L,1000));

        Map<PeriodicType,Map<LimitType,PeriodicCardAmount>> accumMap = new HashMap<>();
        cardLimitMap.put(PeriodicType.SINGLE,createProductTypeMap(0L, 0));
        cardLimitMap.put(PeriodicType.DAILY,createProductTypeMap(0L, 0));
        cardLimitMap.put(PeriodicType.MONTHLY,createProductTypeMap(0L,0));
        cardLimitMap.put(PeriodicType.YEARLY,createProductTypeMap(0L,0));

        List<TerminalType> terminalTypeList = new ArrayList<>();
        terminalTypeList.add(TerminalType.ATM);
        terminalTypeList.add(TerminalType.HOME_TERMINALS);
        List<PurchaseTypes> purchaseTypesList = new ArrayList<>();
        List<TransactionType> transactionTypeList = new ArrayList<>();
        List<LimitType> limitTypeList = new ArrayList<>();
        List<EntryMode> entryModeList = new ArrayList<>();

        List<LimitType> limitTypeList1 = new ArrayList<>();
        limitTypeList1.add(LimitType.RETAIL);
        limitTypeList1.add(LimitType.NO_SPECIFIC);


        CardsBasic cardsBasic = setupDataForTest(CardStatus.ACTIVE,BlockType.APPROVE,
                terminalTypeList,InternationalApplied.DOMESTIC,IncludeExclude.EXCLUDE,
                purchaseTypesList,null,IncludeExclude.NOT_APPLICABLE,
                transactionTypeList, null,IncludeExclude.NOT_APPLICABLE,
                limitTypeList,null,IncludeExclude.NOT_APPLICABLE,
                entryModeList,null,IncludeExclude.NOT_APPLICABLE,
                false,false, false,cardLimitMap,accumMap);

        cardsBasic.persist().await().indefinitely();

        Optional<CardsBasic> cardsBasicOptional =  CardsBasic.findByCardId(cardsBasic.getCardId()).await().indefinitely();
        assertTrue(cardsBasicOptional.isPresent());



    }


    private CustomerDef setupCustomer(String name, String address, String postalCode, String state, String countryCode){
       return CustomerDef.builder()
                .customerId(UUID.randomUUID().toString())
                .addressType(AddressType.HOME)
                .customerType(CustomerType.OWNER)
                .customerName(name)
                .addressLine(address)
                .postalCode(postalCode)
                .state(state)
                .countryCode(countryCode)
                .build();



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


        ProductAuthCriteria productAuthCriteria  = new ProductAuthCriteria();
        productAuthCriteria.setOrg(org);
        productAuthCriteria.setProduct(product);
        productAuthCriteria.setCriteria(criteria);
        productAuthCriteria.setStrategy(strategy.getStrategy());

        if(periodicTypeMapMap != null){
            productAuthCriteria.setCardLimitMap(convertPeriodicCardAmountMap(periodicTypeMapMap));
        }

        if (includeExclude.equals(IncludeExclude.EXCLUDE)) {


            productAuthCriteria.setBlockingCountries(IncludeExclude.EXCLUDE.getIncludeExclude());
            productAuthCriteria.setCountryCodesBlocked(countriesList);
            productAuthCriteria.setBlockingCurrency(IncludeExclude.EXCLUDE.getIncludeExclude());
            productAuthCriteria.setCurrencyCodesBlocked(currenciesList);
            productAuthCriteria.setBlockingStates(IncludeExclude.EXCLUDE.getIncludeExclude());
            productAuthCriteria.setStateCodesBlocked(new ArrayList<>());
            productAuthCriteria.setBlockingMCC(IncludeExclude.EXCLUDE.getIncludeExclude());
            productAuthCriteria.setMccBlocked(mccRangeList);
            productAuthCriteria.setBlockingLimitTypes(IncludeExclude.EXCLUDE.getIncludeExclude());
            productAuthCriteria.setLimitTypesBlocked(createBlockingValueLimits(limitTypeList));
            productAuthCriteria.setBlockingPurchaseTypes(IncludeExclude.EXCLUDE.getIncludeExclude());
            productAuthCriteria.setPurchaseTypesBlocked(createBlockingValuePurchase(purchaseTypesList));
            productAuthCriteria.setBlockingBalanceTypes(IncludeExclude.EXCLUDE.getIncludeExclude());
            productAuthCriteria.setBalanceTypesBlocked(createBlockingValueBalance(blockingBalanceTypeList));
            productAuthCriteria.setBlockingTransactionTypes(IncludeExclude.EXCLUDE.getIncludeExclude());
            productAuthCriteria.setTransactionTypesBlocked(createBlockingValueTransaction(blockingTransactionTypeList));
            productAuthCriteria.setBlockTerminalTypes(IncludeExclude.EXCLUDE.getIncludeExclude());
            productAuthCriteria.setTerminalTypesBlocked(createBlockingValueTerminal(terminalTypeList));

        } else if (includeExclude.equals(IncludeExclude.INCLUDE)) {

            productAuthCriteria.setBlockingCountries(IncludeExclude.INCLUDE.getIncludeExclude());
            productAuthCriteria.setCountryCodesBlocked(countriesList);
            productAuthCriteria.setBlockingCurrency(IncludeExclude.INCLUDE.getIncludeExclude());
            productAuthCriteria.setCurrencyCodesBlocked(currenciesList);
            productAuthCriteria.setBlockingStates(IncludeExclude.INCLUDE.getIncludeExclude());
            productAuthCriteria.setStateCodesBlocked(new ArrayList<>());
            productAuthCriteria.setBlockingMCC(IncludeExclude.INCLUDE.getIncludeExclude());
            productAuthCriteria.setMccBlocked(mccRangeList);
            productAuthCriteria.setBlockingLimitTypes(IncludeExclude.INCLUDE.getIncludeExclude());
            productAuthCriteria.setLimitTypesBlocked(createBlockingValueLimits(limitTypeList));
            productAuthCriteria.setBlockingPurchaseTypes(IncludeExclude.INCLUDE.getIncludeExclude());
            productAuthCriteria.setPurchaseTypesBlocked(createBlockingValuePurchase(purchaseTypesList));
            productAuthCriteria.setBlockingBalanceTypes(IncludeExclude.INCLUDE.getIncludeExclude());
            productAuthCriteria.setBalanceTypesBlocked(createBlockingValueBalance(blockingBalanceTypeList));
            productAuthCriteria.setBlockingTransactionTypes(IncludeExclude.INCLUDE.getIncludeExclude());
            productAuthCriteria.setTransactionTypesBlocked(createBlockingValueTransaction(blockingTransactionTypeList));
            productAuthCriteria.setBlockTerminalTypes(IncludeExclude.INCLUDE.getIncludeExclude());
            productAuthCriteria.setTerminalTypesBlocked(createBlockingValueTerminal(terminalTypeList));


        } else {

            productAuthCriteria.setBlockingCountries(IncludeExclude.NOT_APPLICABLE.getIncludeExclude());
            productAuthCriteria.setBlockingCurrency(IncludeExclude.NOT_APPLICABLE.getIncludeExclude());
            productAuthCriteria.setBlockingStates(IncludeExclude.NOT_APPLICABLE.getIncludeExclude());
            productAuthCriteria.setBlockingMCC(IncludeExclude.NOT_APPLICABLE.getIncludeExclude());
            productAuthCriteria.setBlockingLimitTypes(IncludeExclude.NOT_APPLICABLE.getIncludeExclude());
            productAuthCriteria.setBlockingPurchaseTypes(IncludeExclude.NOT_APPLICABLE.getIncludeExclude());
            productAuthCriteria.setBlockingBalanceTypes(IncludeExclude.NOT_APPLICABLE.getIncludeExclude());
            productAuthCriteria.setBlockingTransactionTypes(IncludeExclude.NOT_APPLICABLE.getIncludeExclude());
            productAuthCriteria.setBlockTerminalTypes(IncludeExclude.NOT_APPLICABLE.getIncludeExclude())
            ;

        }

        return productAuthCriteria;


    }


    private BlockingValue createBlockingValue(String international, String value){

        BlockingValue blockingValue = new BlockingValue();
        blockingValue.setInternationalApplied(international);
        blockingValue.setValue(value);
        return blockingValue;
    }


    private Map<String,Map<String,PeriodicCardAmount>> convertPeriodicCardAmountMap(Map<PeriodicType,Map<LimitType,PeriodicCardAmount>> periodicTypeMapMap){

        return periodicTypeMapMap.entrySet().stream()
                .map(entry -> Tuple2.of(entry.getKey().getPeriodicType(),convertPeriodicLimitMap(entry.getValue())))
                .collect(Collectors.toMap(tuple2-> tuple2.getItem1(),tuple2-> tuple2.getItem2()));

    }

    private Map<String, PeriodicCardAmount> convertPeriodicLimitMap(Map<LimitType,PeriodicCardAmount> limitTypePeriodicCardAmountMap){

        return limitTypePeriodicCardAmountMap.entrySet()
                .stream().map(entry -> Tuple2.of(entry.getKey().getLimitType(),entry.getValue()))
                .collect(Collectors.toMap(tuple2-> tuple2.getItem1(),tuple2-> tuple2.getItem2()));
    }


    private List<BlockingValue> createBlockingValueLimits(List<BlockingLimitType> blockingLimitTypeList){

        return blockingLimitTypeList.stream()
                .map(blockingLimitType -> {
                    BlockingValue blockingValue = new BlockingValue();
                    blockingValue.setValue(blockingLimitType.getLimitType());
                    blockingValue.setInternationalApplied(blockingLimitType.getInternationalApplied());
                    return blockingValue;
                })
                .collect(Collectors.toList())
                ;
    }

    private List<BlockingValue> createBlockingValuePurchase(List<BlockingPurchaseType> blockingPurchaseTypeList){

        return blockingPurchaseTypeList.stream()
                .map(blockingPurchaseType -> {
                    BlockingValue blockingValue = new BlockingValue();
                    blockingValue.setValue(blockingPurchaseType.getPurchaseTypes());
                    blockingValue.setInternationalApplied(blockingPurchaseType.getInternationalApplied());
                    return blockingValue;
                })
                .collect(Collectors.toList())
                ;
    }


    private List<BlockingValue> createBlockingValueBalance(List<BlockingBalanceType> blockingBalanceTypes){

        return blockingBalanceTypes.stream()
                .map(blockingBalanceType -> {
                    BlockingValue blockingValue = new BlockingValue();
                    blockingValue.setValue(blockingBalanceType.getBalanceTypes());
                    blockingValue.setInternationalApplied(blockingBalanceType.getInternationalApplied());
                    return blockingValue;
                })
                .collect(Collectors.toList())
                ;
    }


    private List<BlockingValue> createBlockingValueTerminal(List<BlockingTerminalType> blockingTerminalTypes){

        return blockingTerminalTypes.stream()
                .map(blockingTerminalType -> {
                    BlockingValue blockingValue = new BlockingValue();
                    blockingValue.setValue(blockingTerminalType.getTerminalType());
                    blockingValue.setInternationalApplied(blockingTerminalType.getInternationalApplied());
                    return blockingValue;
                })
                .collect(Collectors.toList())
                ;
    }

    private List<BlockingValue> createBlockingValueTransaction(List<BlockingTransactionType> blockingTransactionTypes){

        return blockingTransactionTypes.stream()
                .map(blockingTransactionType -> {
                    BlockingValue blockingValue = new BlockingValue();
                    blockingValue.setValue(blockingTransactionType.getTransactionType());
                    blockingValue.setInternationalApplied(blockingTransactionType.getInternationalApplied());
                    return blockingValue;
                })
                .collect(Collectors.toList())
                ;
    }


    private Map<LimitType, PeriodicCardAmount> createProductTypeMap(Long amount, Integer count) {
        Map<LimitType, PeriodicCardAmount> limitAmountMap = new HashMap<>();

        PeriodicCardAmount periodicCardAmountNoSpecific = PeriodicCardAmount.builder()
                .transactionAmount(amount)
                .transactionNumber(count)
                .limitType(LimitType.NO_SPECIFIC)
                .build();


        PeriodicCardAmount periodicCardAmountRetail = PeriodicCardAmount.builder()
                .transactionAmount(( Double.valueOf(amount *0.5).longValue()))
                .transactionNumber(Double.valueOf(count *0.5).intValue())
                .limitType(LimitType.RETAIL)
                .build();

        PeriodicCardAmount periodicCardAmountCash = PeriodicCardAmount.builder()
                .transactionNumber(Double.valueOf(count*0.2).intValue())
                .transactionAmount(Double.valueOf(amount*.02).longValue())
                .limitType(LimitType.CASH)
                .build();
        PeriodicCardAmount periodicCardAmountOTC = PeriodicCardAmount.builder()
                .transactionAmount(Double.valueOf(amount*.1).longValue())
                .transactionNumber(Double.valueOf(count* 0.1).intValue())
                .limitType(LimitType.OTC)
                .build();

        limitAmountMap.put(LimitType.identify(periodicCardAmountNoSpecific.getLimitType()), periodicCardAmountNoSpecific);
        limitAmountMap.put(LimitType.identify(periodicCardAmountRetail.getLimitType()), periodicCardAmountRetail);
        limitAmountMap.put(LimitType.identify(periodicCardAmountCash.getLimitType()), periodicCardAmountCash);
        limitAmountMap.put(LimitType.identify(periodicCardAmountOTC.getLimitType()), periodicCardAmountOTC);

        return limitAmountMap;
    }



    private AccountInfo createAccountInfo(boolean allFields, List<Integer> fields, BlockType blockType){

        Map<BalanceTypes,Long> balanceLimitMap = new HashMap<>();

        balanceLimitMap.put(BalanceTypes.CURRENT_BALANCE,10000L);
        balanceLimitMap.put(BalanceTypes.CASH_BALANCE,5000L);
        balanceLimitMap.put(BalanceTypes.INSTALLMENT_BALANCE,3000L);


        Map<BalanceTypes, AccountBalances> accountBalancesMap = new HashMap<>();

        AccountBalances accountBalances1 = AccountBalances.builder()
                .postedBalance(1000)
                .memoDb(100)
                .memoCr(10)
                .build()
                ;

        AccountBalances accountBalances2 = AccountBalances.builder()
                .postedBalance(2000)
                .memoDb(200)
                .memoCr(20)
                .build()
                ;
        AccountBalances accountBalances3 = AccountBalances.builder()
                .postedBalance(3000)
                .memoDb(300)
                .memoCr(30)
                .build()
                ;
        AccountBalances accountBalances4 = AccountBalances.builder()
                .postedBalance(4000)
                .memoDb(400)
                .memoCr(40)
                .build()
                ;

        accountBalancesMap.put(BalanceTypes.CURRENT_BALANCE,accountBalances4);
        accountBalancesMap.put(BalanceTypes.CASH_BALANCE,accountBalances3);
        accountBalancesMap.put(BalanceTypes.INSTALLMENT_BALANCE,accountBalances1);


        AccountInfo.AccountInfoBuilder builder = AccountInfo.builder()
                .accountId(UUID.randomUUID().toString().replace("-", ""))
                .org(1)
                .product(201)
                .blockType(blockType)
                .accountType(AccountType.CREDIT)
                ;

        if(allFields){

            return builder.limitsMap(balanceLimitMap)
                    .balancesMap(accountBalancesMap)
                    .build();
        }

        fields.forEach(integer ->  populateRestofFields(integer,builder,balanceLimitMap,accountBalancesMap));

        return builder.build();

    }

    private void populateRestofFields(Integer integer, AccountInfo.AccountInfoBuilder builder,
                                      Map<BalanceTypes, Long> balanceLimitMap,
                                      Map<BalanceTypes, AccountBalances> accountBalancesMap) {

        switch (integer){
            case 0: {
                builder.limitsMap(balanceLimitMap);
                return;
            }
            case 1: {
                builder.balancesMap(accountBalancesMap);
                return;
            }
        }
    }


    private CardsBasic.CardsBasicBuilder createCardBasic(String cardId, CardStatus cardStatus, BlockType blockType) {

            return CardsBasic.builder()
                    .cardId(cardId)
                    .cardholderType(CardHolderType.PRIMARY)
                    .blockType(blockType)
                    .cardStatus(cardStatus)
                    .org(001)
                    .product(201)
                    .waiverDaysActivation(10)
                    ;


    }

    private CardsBasic setupDataForTest(CardStatus cardStatus, BlockType blockType, List<TerminalType> terminalTypeList, InternationalApplied terminalInternational, IncludeExclude includeExcludeTerminal,
                                    List<PurchaseTypes> purchaseTypesList, InternationalApplied purchaseInternational, IncludeExclude includeExcludePurchase,
                                    List<TransactionType> transactionTypeList, InternationalApplied transactionTypeInternationnal, IncludeExclude includeExcludeTransaction,
                                    List<LimitType> limitTypeList, InternationalApplied limitInternational, IncludeExclude includeExcludeLimit,
                                    List<EntryMode> entryModeList,InternationalApplied entryInternational, IncludeExclude includeExcludeEntry,
                                    Boolean blockCashBack, Boolean blockInstallments, Boolean blockInternational, Map<PeriodicType,Map<LimitType,PeriodicCardAmount>> cardLimitMap, Map<PeriodicType,Map<LimitType,PeriodicCardAmount>> accumValuesMap) {


        CardsBasic.CardsBasicBuilder builder = createCardBasic(UUID.randomUUID().toString().replace("-", ""), cardStatus, blockType);
        if (blockCashBack != null) {
            builder.blockCashBack(blockCashBack);
        }
        if (blockInstallments != null) {
            builder.blockInstallments(blockInstallments);
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

        if(blockInternational != null){
            builder.blockInternational(blockInternational);
        }

        return builder.build();

    }


}

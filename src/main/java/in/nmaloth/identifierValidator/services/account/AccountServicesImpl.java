package in.nmaloth.identifierValidator.services.account;

import in.nmaloth.identifierValidator.model.AccountResponse;
import in.nmaloth.identifierValidator.model.entity.account.AccountBalances;
import in.nmaloth.identifierValidator.model.entity.account.AccountInfo;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.payments.constants.BlockType;
import in.nmaloth.payments.constants.ServiceResponse;
import in.nmaloth.payments.constants.account.BalanceTypes;
import in.nmaloth.payments.constants.ids.FieldID;
import in.nmaloth.payments.constants.ids.ServiceID;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class AccountServicesImpl implements AccountServices {


    @Inject
    AccountTempService accountTempService;


    @Override
    public Optional<ServiceResponse> validateAccountBlock(BlockType blockType) {

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
    public AccountResponse validateLimit(Map<String, Long> limitMap, Map<String, AccountBalances> balancesMap,
                                         Map<String, Long> txnBalanceMap,List<String> responseList,String accountType) {


        AccountResponse accountResponse  = new AccountResponse();
        accountResponse.setResponseList(responseList);
        accountResponse.setAccountType(accountType);

        for (Map.Entry<String, Long> entry : txnBalanceMap.entrySet()) {

            Long limit = limitMap.get(entry.getKey());
            if (limit != null) {

                AccountBalances accountBalances = balancesMap.get(entry.getKey());
                if (accountBalances == null) {
                    accountBalances = AccountBalances.builder().postedBalance(0).memoCr(0).memoDb(0).build();
                }
                long accumAmount = accountBalances.getPostedBalance() + accountBalances.getMemoDb() - accountBalances.getMemoCr() + entry.getValue();
                if (limit < accumAmount) {
                    responseList.add(ServiceResponse.ACCT_LIMIT.getServiceResponse());
                    break;
                }

            }

        }

        AccountBalances accountBalances = balancesMap.get(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        if (accountBalances == null) {
            accountBalances = AccountBalances.builder().postedBalance(0).memoCr(0).memoDb(0).build();
        }
        Long txnAmmount = txnBalanceMap.get(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());

        if(txnAmmount == null){
            txnAmmount  = 0L;
        }

        long accumAmount = accountBalances.getPostedBalance() + accountBalances.getMemoDb() - accountBalances.getMemoCr() + txnAmmount;

        accountResponse.setBalance(accumAmount);

        Long limit = limitMap.get(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        if(limit != null){
            accountResponse.setCreditLimit(limit);
            accountResponse.setOtb(limit - accumAmount);
        }
        return accountResponse;

    }

    @Override
    public Uni<AccountResponse> validateAccount(IdentifierValidator identifierValidator, AccountInfo accountInfo, long convertAmount) {

        List<String> responseList = new ArrayList<>();


        validateAccountBlock(BlockType.identify(accountInfo.getBlockType()))
                .ifPresent(serviceResponse -> responseList.add(serviceResponse.getServiceResponse()));


        return accountTempService.getAllAccountTempBalance(identifierValidator, convertAmount)
                .onItem().transform(balanceTempMap -> validateLimit(accountInfo.getLimitsMap(), accountInfo.getBalancesMap(), balanceTempMap,responseList,accountInfo.getAccountType()))
//                .onItem().transform(serviceResponseOptional -> getAccountResponse(accountInfo, convertAmount, responseList, serviceResponseOptional));
        ;

    }
//
//    private AccountResponse getAccountResponse(AccountInfo accountInfo, long convertAmount, List<String> responseList, Optional<ServiceResponse> serviceResponseOptional) {
//
//        AccountResponse accountResponse = new AccountResponse();
//        accountResponse.setResponseList(responseList);
//        serviceResponseOptional.ifPresent(serviceResponse -> responseList.add(serviceResponse.getServiceResponse()));
//
//        AccountBalances accountBalances = accountInfo.getBalancesMap().get(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
//        if(accountBalances == null){
//            accountBalances = AccountBalances.builder().postedBalance(0L).memoDb(0L).memoCr(0L).build();
//        }
//        long balance = accountBalances.getPostedBalance() + accountBalances.getMemoDb() - accountBalances.getMemoCr() + convertAmount;
//        accountResponse.setBalance(balance);
//
//        Long creditLimit = accountInfo.getLimitsMap().get(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
//        if(creditLimit != null){
//            accountResponse.setCreditLimit(creditLimit);
//            accountResponse.setOtb(creditLimit - balance);
//        }
//
//        return accountResponse;
//    }

    @Override
    public Uni<ValidationResponse> validateAccount(IdentifierValidator identifierValidator, long convertAmount) {
        return AccountInfo.findByAccountId(identifierValidator.getAccountNumber())
                .onItem().transformToUni(accountInfoOptional -> {

                            if (accountInfoOptional.isEmpty()) {

                                AccountResponse accountResponse = new AccountResponse();
                                accountResponse.setResponseList(List.of(ServiceResponse.NO_ENTRY.getServiceResponse()));
                                return Uni.createFrom().item(accountResponse);
                            }
                            return validateAccount(identifierValidator, accountInfoOptional.get(), convertAmount);
                        }
                )
                .onItem().transform(accountResponse -> convertToValidationResponse(accountResponse, identifierValidator.getInternational()));


    }

    private ValidationResponse convertToValidationResponse(AccountResponse accountResponse, String international) {

        Map<String, String> serviceFieldMap = new HashMap<>();
        if(accountResponse.getBalance() != null){
            serviceFieldMap.put(FieldID.ACCOUNT_BALANCE.getFieldId(), accountResponse.getBalance().toString());

        }
        if(accountResponse.getOtb() != null){
            serviceFieldMap.put(FieldID.OTB.getFieldId(), accountResponse.getOtb().toString());
        }
        if(accountResponse.getCreditLimit() != null){
            serviceFieldMap.put(FieldID.CREDIT_LIMIT.getFieldId(), accountResponse.getCreditLimit().toString());
        }

        if(accountResponse.getAccountType() != null){
            serviceFieldMap.put(FieldID.ACCOUNT_TYPE.getFieldId(), accountResponse.getAccountType());
        }

        if(international != null){
            serviceFieldMap.put(FieldID.INTERNATIONAL.getFieldId(), international);

        }
        if(accountResponse.getResponseList().size() == 0){
            accountResponse.getResponseList().add(ServiceResponse.OK.getServiceResponse());
        }

        return ValidationResponse.newBuilder().setServiceId(ServiceID.ACCOUNT_VALIDATOR)
                .addAllValidationResponse(accountResponse.getResponseList())
                .putAllServiceResponseFields(serviceFieldMap)
                .build();
    }
}

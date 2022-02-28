package in.nmaloth.identifierValidator.services.account;

import in.nmaloth.identifierValidator.model.entity.temp.AccountTempBalance;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.payments.constants.TransactionType;
import io.quarkus.infinispan.client.Remote;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class AccountTempServiceImpl implements AccountTempService {

    @Inject
    @Remote("accountTemp")
    RemoteCache<String, AccountTempBalance> accountTempBalanceRemoteCache;

    QueryFactory queryFactory;

    @ConfigProperty(name = "cache.retention.limit")
    Optional<Integer> cacheRetention;


    public void startup(@Observes StartupEvent startupEvent){

        this.queryFactory = Search.getQueryFactory(accountTempBalanceRemoteCache);

    }

    @Override
    public Uni<List<AccountTempBalance>> getAllAccountTempBalance(String accountNumber) {


        Query<AccountTempBalance> accountTempBalanceQuery = queryFactory.create("from in.nmaloth.identifierValidator.model.entity.temp.AccountTempBalance where accountNumber = :accountNumber ");

        accountTempBalanceQuery.setParameter("accountNumber", accountNumber);

        CompletableFuture<List<AccountTempBalance>> completableFuture = CompletableFuture.supplyAsync(() ->  accountTempBalanceQuery.execute().list());

        return Uni.createFrom().future(completableFuture)
                ;

    }

    @Override
    public Uni<Map<String,Long>> getAllAccountTempBalance(IdentifierValidator identifierValidator, long convertedAmount) {


        return getAllAccountTempBalance(identifierValidator.getAccountNumber())
                .onItem().transform(accountTempBalances -> {
                    if(accountTempBalances == null){
                        accountTempBalances = new ArrayList<>();
                    }
                    accountTempBalances.add(createNewAccountTempBalance(identifierValidator,convertedAmount));
                    return accountTempBalances;
                })
                .onItem().transform(accountTempBalances -> findAccumulatedAmount(accountTempBalances))
                ;

    }

    @Override
    public Uni<AccountTempBalance> updateAccountTempBalance(AccountTempBalance accountTempBalance){

        return Uni.createFrom().future(accountTempBalanceRemoteCache.putAsync(accountTempBalance.getId(),accountTempBalance, cacheRetention.orElse(5), TimeUnit.SECONDS));
    }



    @Override
    public AccountTempBalance createNewAccountTempBalance(IdentifierValidator identifierValidator, long convertedAmount) {

        long transactionAmount = 0L;
        if (checkForDebit(identifierValidator)) {
            transactionAmount = convertedAmount;
        } else {
            transactionAmount = convertedAmount * -1;
        }

        AccountTempBalance.AccountTempBalanceBuilder builder = AccountTempBalance.builder()
                .id(identifierValidator.getMessageId())
                .cardNumber(identifierValidator.getCardNumber())
                .amount(transactionAmount);

        for( int i = 0; i < identifierValidator.getBalanceTypesCount(); i ++){
            builder.balanceTypes(identifierValidator.getBalanceTypes(i));
        }
        return builder.build();

    }


    private boolean checkForDebit(IdentifierValidator identifierValidator){

        switch (TransactionType.identify(identifierValidator.getTransactionType())){
            case MERCHANDISE_RETURN:
            case CREDIT_VOUCHER:
            case OCT:
            case PAYMENT: {
                return false;
            }
        }
        return true;
    }


    private Map<String, Long> findAccumulatedAmount(List<AccountTempBalance> accountTempBalanceList) {

        Map<String, Long> cacheBalanceAmount = new HashMap<>();

        for (AccountTempBalance accountTempBalance : accountTempBalanceList) {

            accountTempBalance.getBalanceTypes()
                    .forEach(balanceType -> updateLimitValues(cacheBalanceAmount, balanceType, accountTempBalance.getAmount()));

        }
        return cacheBalanceAmount;
    }


    private void updateLimitValues(Map<String, Long> cacheBalanceAccumMap,
                                   String balanceType, Long amount) {

        Long amountCache = cacheBalanceAccumMap.get(balanceType);
        if (amountCache == null) {
            cacheBalanceAccumMap.put(balanceType,amount);
        } else {
            cacheBalanceAccumMap.put(balanceType,amountCache + amount);
        }
    }

}

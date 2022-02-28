package in.nmaloth.identifierValidator.services.card;

import in.nmaloth.identifierValidator.model.entity.card.CacheTempAccum;
import in.nmaloth.identifierValidator.model.entity.temp.CardTempBalance;
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
public class CardTempServiceImpl implements CardTempService{

    @Inject
    @Remote("cardTemp")
    RemoteCache<String, CardTempBalance> cardTempBalanceRemoteCache;

    QueryFactory queryFactory;

    @ConfigProperty(name = "cache.retention.limit")
    Optional<Integer> cardCacheRetention;


    public void startup(@Observes StartupEvent startupEvent){

        this.queryFactory = Search.getQueryFactory(cardTempBalanceRemoteCache);

    }

    @Override
    public Uni<List<CardTempBalance>> getAllCardTempBalance(String cardNumber) {


        Query<CardTempBalance> cardTempBalanceQuery = queryFactory.create("from in.nmaloth.identifierValidator.model.entity.temp.CardTempBalance where cardNumber = :cardNumber ");

        cardTempBalanceQuery.setParameter("cardNumber", cardNumber);

        CompletableFuture<List<CardTempBalance>> completableFuture = CompletableFuture.supplyAsync(() ->  cardTempBalanceQuery.execute().list());

        return Uni.createFrom().future(completableFuture)
                ;

    }

    @Override
    public Uni<Map<String,CacheTempAccum>> getAllCardTempBalance(IdentifierValidator identifierValidator,long convertedAmount) {


        return getAllCardTempBalance(identifierValidator.getCardNumber())
                .onItem().transform(cardTempBalances -> {
                    if(cardTempBalances == null){
                        cardTempBalances = new ArrayList<>();
                    }
                    cardTempBalances.add(createNewCardTempBalance(identifierValidator,convertedAmount));
                    return cardTempBalances;
                })
                .onItem().transform(cardTempBalances -> findAccumulatedAmount(cardTempBalances))
                ;

    }

    @Override
    public Uni<CardTempBalance> updateCardTempBalance(CardTempBalance cardTempBalance){

        return Uni.createFrom().future(cardTempBalanceRemoteCache.putAsync(cardTempBalance.getId(),cardTempBalance, cardCacheRetention.orElse(5), TimeUnit.SECONDS));
    }



    @Override
    public CardTempBalance createNewCardTempBalance( IdentifierValidator identifierValidator,long convertedAmount) {

        long transactionAmount = 0L;
        if (checkForDebit(identifierValidator)) {
            transactionAmount = convertedAmount;
        } else {
            transactionAmount = convertedAmount * -1;
        }

        CardTempBalance.CardTempBalanceBuilder builder = CardTempBalance.builder()
                .id(identifierValidator.getMessageId())
                .cardNumber(identifierValidator.getCardNumber())
                .amount(transactionAmount);

        for( int i = 0; i < identifierValidator.getLimitTypesCount(); i ++){
            builder.limitTypes(identifierValidator.getLimitTypes(i));
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


    private Map<String, CacheTempAccum> findAccumulatedAmount(List<CardTempBalance> cardTempBalanceList) {

        Map<String, CacheTempAccum> cacheCardTempAccumMap = new HashMap<>();

        for (CardTempBalance cardTempBalance : cardTempBalanceList) {

            cardTempBalance.getLimitTypes()
                    .forEach(limitType -> updateLimitValues(cacheCardTempAccumMap, limitType, cardTempBalance.getAmount()));

        }
        return cacheCardTempAccumMap;
    }


    private void updateLimitValues(Map<String, CacheTempAccum> cacheCardTempAccumMap,
                                   String limitType, Long amount) {

        CacheTempAccum cacheCardTempAccum = cacheCardTempAccumMap.get(limitType);
        if (cacheCardTempAccum == null) {
            cacheCardTempAccum = CacheTempAccum.builder()
                    .accumAmount(amount)
                    .accumCount(1)
                    .accumType(limitType)
                    .build();
            cacheCardTempAccumMap.put(limitType,cacheCardTempAccum);
        } else {
            cacheCardTempAccum.setAccumAmount(cacheCardTempAccum.getAccumAmount() + amount);
            cacheCardTempAccum.setAccumCount(cacheCardTempAccum.getAccumCount() + 1);
        }
    }

}

package in.nmaloth.identifierValidator.services;

import in.nmaloth.identifierValidator.exception.CurrencyCodeNotFoundException;
import in.nmaloth.identifierValidator.model.CurrencyConversionKeyDaily;
import in.nmaloth.identifierValidator.model.CurrencyConversionResponse;
import in.nmaloth.identifierValidator.model.RateToUse;
import in.nmaloth.identifierValidator.model.entity.global.CurrencyConversionTable;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class CurrencyConversionServiceDaily implements CurrencyConversionService {

    private Map<CurrencyConversionKeyDaily, CurrencyConversionTable> currencyConversionTableMap = new HashMap<>();


    @ConfigProperty(name = "currency-codes.settlement.list")
    List<String>  settlementCurrencyList;


    @ConfigProperty(name = "currency-codes.settlement.default")
    String defaultCurrencyCode;


    @ConfigProperty(name = "network")
    String network;

    @ConfigProperty(name = "conversion.future.days")
    Optional<Integer> daysToConvert;

    @ConfigProperty(name = "percent.node")
    Optional<Integer> percentNode;


    @ConfigProperty(name = "currency.convert.mechanism")
    private Optional<String> currencyConversionMechanism;


    public void startupLoad(@Observes StartupEvent startupEvent) {

        filterLoadedCurrency()
                .forEach(localDate -> CurrencyConversionTable.findAllCurrencyConversionTables(network, localDate)
                        .await().indefinitely()
                        .stream()
                        .forEach(currencyConversionTable -> updateCurrencyConversionMap(currencyConversionTable))
                );


    }


    private void updateCurrencyConversionMap(CurrencyConversionTable currencyConversionTable) {

        currencyConversionTableMap.put(CurrencyConversionKeyDaily.builder()
                .currencyCode(currencyConversionTable.getCurrencyCode())
                .destinationCurrencyCode(currencyConversionTable.getDestCurrencyCode())
                .conversionDate(currencyConversionTable.getConversionDate())
                .build(), currencyConversionTable);


    }

    private List<LocalDate> filterLoadedCurrency() {

        List<LocalDate> localDateList = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();
        localDateList.add(currentDate);


        for (int i = 0; i < daysToConvert.orElse(2); i++) {
            localDateList.add(currentDate.plusDays(i + 1));
        }
        return localDateList;
    }


    @Scheduled(cron = "45 5,11,17,23 * * * ?")
    void cronJob2345(ScheduledExecution execution) {

        removePastElements();

        filterLoadedCurrency()
                .forEach(localDate -> CurrencyConversionTable.findAllCurrencyConversionTables(network, localDate)
                        .await().indefinitely()
                        .stream().forEach(currencyConversionTable -> updateCurrencyConversionMap(currencyConversionTable))
                );


    }


//    @Scheduled(cron = "45 17 * * *")
//    void cronJob1745(ScheduledExecution execution) {
//
//        removePastElements();
//
//        filterLoadedCurrency()
//                .forEach(localDate -> ccTableRepository.findAllCurrencyConversionTables(network, localDate)
//                        .subscribe().with(currencyConversionTable -> updateCurrencyConversionMap(currencyConversionTable))
//                );
//
//
//    }
//

//    @Scheduled(cron = "45 11 * * *")
//    void cronJob1145(ScheduledExecution execution) {
//
//        removePastElements();
//
//        filterLoadedCurrency()
//                .forEach(localDate -> ccTableRepository.findAllCurrencyConversionTables(network, localDate)
//                        .subscribe().with(currencyConversionTable -> updateCurrencyConversionMap(currencyConversionTable))
//                );
//
//
//    }
//
//
//    @Scheduled(cron = "45 5 * * *")
//    void cronJob0545(ScheduledExecution execution) {
//
//        removePastElements();
//
//        filterLoadedCurrency()
//                .forEach(localDate -> ccTableRepository.findAllCurrencyConversionTables(network, localDate)
//                        .subscribe().with(currencyConversionTable -> updateCurrencyConversionMap(currencyConversionTable))
//                );
//
//
//    }

    private void removePastElements() {
        LocalDate currentDate = LocalDate.now();

        List<CurrencyConversionKeyDaily> currencyConversionKeyDailyRemovalList = currencyConversionTableMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getConversionDate().isBefore(currentDate))
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());

        currencyConversionKeyDailyRemovalList.forEach(currencyConversionKeyDaily -> currencyConversionTableMap.remove(currencyConversionKeyDaily));


    }


    @Override
    public long convertTransactionAmount(String txnCurrencyCode, String billingCurrencyCode, long txnAmount, String settlementCurrencyCode) throws CurrencyCodeNotFoundException {

        CurrencyConversionResponse currencyConversionResponse = new CurrencyConversionResponse();
        if(txnCurrencyCode.equals(settlementCurrencyCode)){

            if(settlementCurrencyCode.equals(billingCurrencyCode)){
                return txnAmount;
            }
        }


        if(!txnCurrencyCode.equals(settlementCurrencyCode)){

            txnAmount = convertAmounts(txnCurrencyCode,settlementCurrencyCode,RateToUse.BUY,txnAmount);

        }

        if(settlementCurrencyCode.equals(billingCurrencyCode)){
            return txnAmount;
        } else {
            return convertAmounts(settlementCurrencyCode,billingCurrencyCode,RateToUse.BUY,txnAmount);

        }


    }

    private long convertAmounts(String srcCurrCode, String destCurrCode, RateToUse rateToUse, long amount) throws CurrencyCodeNotFoundException {

        CurrencyConversionTable currencyTable = fetchCurrencyTable(srcCurrCode, destCurrCode);

        long rate = 0L;
        switch (rateToUse){
            case BUY:{
                rate = currencyTable.getBuyRate();
                break;
            }
            case SELL:{
                rate = currencyTable.getSellRate();
                break;
            }
            default:{
                rate = currencyTable.getMidRate();
                break;
            }
        }

        long percentNodeValue = node(percentNode.orElse(6));
        double nodeFactor =1;
        if(currencyTable.getSourceCurrencyNode() != currencyTable.getDestinationCurrencyNode()){
            nodeFactor = node(currencyTable.getDestinationCurrencyNode())/node(currencyTable.getSourceCurrencyNode());
        }
        return Math.round(amount * rate * nodeFactor/ percentNodeValue);

    }

    private long node(int node){


        long value = 1;
        for (int i = 0; i < node; i ++){
            value = value * 10;
        }
        return value;
    }

    private CurrencyConversionTable fetchCurrencyTable(String originalCurrency, String destCurrency) throws CurrencyCodeNotFoundException {

        CurrencyConversionKeyDaily currencyKey = CurrencyConversionKeyDaily.builder()
                .currencyCode(originalCurrency)
                .destinationCurrencyCode(destCurrency)
                .conversionDate(LocalDate.now())
                .build();

        CurrencyConversionTable currencyTable = currencyConversionTableMap.get(currencyKey);
        if(currencyTable == null){
            throw  new CurrencyCodeNotFoundException(getMessage(originalCurrency,destCurrency));
        }

        return currencyTable;
    }

    private String getMessage(String sourceCurrency, String destCurrency){

        return  new StringBuilder().append("Source Currency :")
                .append(sourceCurrency)
                .append( " : Destination Currency ")
                .append(destCurrency)
                .append(" : Not Found")
                .toString();
    }


    @Override
    public boolean currencyConversionRequired() {

        if(currencyConversionMechanism.orElse("no").equalsIgnoreCase("no")){
            return false;
        }
        return true;
    }

    @Override
    public void loadConversionTable(CurrencyConversionKeyDaily currencyConversionKeyDaily, CurrencyConversionTable currencyConversionTable) {

        currencyConversionTableMap.put(currencyConversionKeyDaily,currencyConversionTable);
    }

    @Override
    public void loadCurrencyTables() {

        removePastElements();

        filterLoadedCurrency()
                .forEach(localDate -> CurrencyConversionTable.findAllCurrencyConversionTables(network, localDate)
                        .subscribe().with(currencyConversionTableList -> currencyConversionTableList.forEach(table -> updateCurrencyConversionMap(table))
                ));


    }

    @Override
    public CurrencyConversionTable getConversionTable(String srcCurrencyCode, String destinationCurrencyCode, LocalDate conversionDate) {
        return currencyConversionTableMap.get(CurrencyConversionKeyDaily.builder()
                .currencyCode(srcCurrencyCode)
                .destinationCurrencyCode(destinationCurrencyCode)
                .conversionDate(conversionDate)
                .build());
    }

    @Override
    public String getSettlementCurrencyCode(String txnCurrencyCode) {

        if(settlementCurrencyList.contains(txnCurrencyCode)){
            return txnCurrencyCode;
        }
        return defaultCurrencyCode;
    }


}

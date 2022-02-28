package in.nmaloth.identifierValidator.services;

import in.nmaloth.identifierValidator.exception.CurrencyCodeNotFoundException;
import in.nmaloth.identifierValidator.model.CurrencyConversionKeyDaily;
import in.nmaloth.identifierValidator.model.entity.global.CurrencyConversionTable;
import in.nmaloth.payments.constants.network.NetworkType;
import in.nmaloth.testResource.GRPCWireResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.junit.experimental.theories.Theories;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(GRPCWireResource.class)
class CurrencyConversionServiceDailyTest {





    @Inject
    CurrencyConversionService currencyConversionService;


    @Test
    void testLoadConversionAmount() throws InterruptedException {


        CurrencyConversionTable currencyTable = buildCurrencyTable("124","840",500000L,LocalDate.now(),NetworkType.VISA_VIP);

        currencyTable.persist().await().indefinitely();

        currencyConversionService.loadCurrencyTables();

        Thread.sleep(300);
        CurrencyConversionTable currencyConversionTable = currencyConversionService.getConversionTable("124","840", LocalDate.now());

        assertNotNull(currencyConversionTable);



    }




    @Test
    void convertTransactionAmount() throws InterruptedException, CurrencyCodeNotFoundException {

        CurrencyConversionTable currencyTable = buildCurrencyTable("124","840",500000L,LocalDate.now(),NetworkType.VISA_VIP);

        currencyTable.persist().await().indefinitely();

        currencyConversionService.loadCurrencyTables();

        Thread.sleep(500);


        Long billingAmount = currencyConversionService.convertTransactionAmount("124","840", 1000, "840");


        assertEquals(500, billingAmount);
        ;
    }


    @Test
    void convertTransactionAmount_1() throws InterruptedException, CurrencyCodeNotFoundException {

        CurrencyConversionTable currencyTable = buildCurrencyTable("124","840",500000L,LocalDate.now(),NetworkType.VISA_VIP);

        currencyTable.persist().await().indefinitely();

        currencyConversionService.loadCurrencyTables();

        Thread.sleep(500);


        Long billingAmount = currencyConversionService.convertTransactionAmount("840","840", 1000, "840");


        assertEquals(1000, billingAmount);
        ;
    }


    @Test
    void convertTransactionAmount_2() throws InterruptedException, CurrencyCodeNotFoundException {

        List<CurrencyConversionTable> currencyConversionTableList = new ArrayList<>();
        currencyConversionTableList.add(buildCurrencyTable("124","840",500000L,LocalDate.now(),NetworkType.VISA_VIP));
        currencyConversionTableList.add(buildCurrencyTable("840","124",2000000L,LocalDate.now(),NetworkType.VISA_VIP));

        currencyConversionTableList.add(buildCurrencyTable("484","840",100000L,LocalDate.now(),NetworkType.VISA_VIP));
        currencyConversionTableList.add(buildCurrencyTable("840","484",10000000L,LocalDate.now(),NetworkType.VISA_VIP));


        CurrencyConversionTable.persist(currencyConversionTableList).await().indefinitely();

        currencyConversionService.loadCurrencyTables();

        Thread.sleep(500);

        Long billingAmount = currencyConversionService.convertTransactionAmount("124","484", 1000, "840");


        assertEquals(5000, billingAmount);
        ;
    }


    @Test
    void currencyConversionRequired() {

        assertFalse(currencyConversionService.currencyConversionRequired());

    }

    @Test
    void currencyConversionSettlementCurrency() {

        String settlementCurrency1 = currencyConversionService.getSettlementCurrencyCode("484");
        String settlementCurrency2 = currencyConversionService.getSettlementCurrencyCode("124");
        String settlementCurrency3 = currencyConversionService.getSettlementCurrencyCode("840");

        assertAll(
                ()-> assertEquals("484",settlementCurrency1),
                ()-> assertEquals("840",settlementCurrency2),
                ()-> assertEquals("840",settlementCurrency3)


                );

    }

    @Test
    void loadCurrencyTables() throws InterruptedException {

        CurrencyConversionKeyDaily key1 = CurrencyConversionKeyDaily.builder()
                .destinationCurrencyCode("124")
                .currencyCode("484")
                .conversionDate(LocalDate.now().minusDays(1))
                .build();

;

        currencyConversionService.loadConversionTable(key1,CurrencyConversionTable.builder()
                .network(NetworkType.VISA_VIP)
                .conversionDate(LocalDate.now().minusDays(1))
                .destinationCurrencyNode(2)
                .destCurrencyCode("124")
                .sourceCurrencyNode(2)
                .currencyCode("484")
                .buyRate(100000)
                .sellRate(10000)
                .midRate(10000)
                .build());


        CurrencyConversionTable currencyConversionTable = CurrencyConversionTable.builder()
                .network(NetworkType.VISA_VIP)
                .conversionDate(LocalDate.now())
                .destinationCurrencyNode(2)
                .destCurrencyCode("124")
                .sourceCurrencyNode(2)
                .currencyCode("484")
                .buyRate(200000)
                .sellRate(20000)
                .midRate(20000)
                .build();

        currencyConversionTable.persist().await().indefinitely();

        currencyConversionService.loadCurrencyTables();

        Thread.sleep(500);

        CurrencyConversionTable currencyConversionTable1 = currencyConversionService.getConversionTable("484","124", LocalDate.now());

        assertEquals(200000, currencyConversionTable1.getBuyRate());

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
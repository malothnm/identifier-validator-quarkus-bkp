package in.nmaloth.identifierValidator.services;

import in.nmaloth.identifierValidator.exception.CurrencyCodeNotFoundException;
import in.nmaloth.identifierValidator.model.AggregateResponse;
import in.nmaloth.identifierValidator.model.CurrencyConversionKeyDaily;
import in.nmaloth.identifierValidator.model.CurrencyConversionResponse;
import in.nmaloth.identifierValidator.model.entity.global.CurrencyConversionTable;
import in.nmaloth.payments.constants.ServiceResponse;
import in.nmaloth.payments.constants.network.NetworkType;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;

public interface CurrencyConversionService {

    long convertTransactionAmount(String txnCurrencyCode, String billingCurrencyCode, long txnAmount, String settlementCurrencyCode) throws CurrencyCodeNotFoundException;
    boolean currencyConversionRequired();

    void loadConversionTable(CurrencyConversionKeyDaily currencyConversionKeyDaily, CurrencyConversionTable currencyConversionTable);
    void loadCurrencyTables();


    CurrencyConversionTable getConversionTable(String srcCurrencyCode, String destinationCurrencyCode, LocalDate conversionDate);

    String getSettlementCurrencyCode(String txnCurrencyCode);
}

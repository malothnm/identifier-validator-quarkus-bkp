package in.nmaloth.identifierValidator.services;

import in.nmaloth.identifierValidator.model.AggregateResponse;
import in.nmaloth.payments.constants.ServiceResponse;

public interface AggregationService {


    AggregateResponse createCurrencyConversionResponse(String messageId, ServiceResponse serviceResponse, Long billAmount, String billCurrency);

    void updateCache(String messageId, ServiceResponse serviceResponse,Long billAmount, String billCurrency,boolean currencyConversionNotRequired);

}

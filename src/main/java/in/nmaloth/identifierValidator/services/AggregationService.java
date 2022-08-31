package in.nmaloth.identifierValidator.services;

import in.nmaloth.identifierValidator.model.AggregateResponse;
import in.nmaloth.identifierValidator.model.AggregateResponseCombined;
import in.nmaloth.identifierValidator.model.proto.aggregator.AggregatorResponse;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponseSummary;
import in.nmaloth.payments.constants.ServiceResponse;

import java.util.List;

public interface AggregationService {

//
//    AggregateResponse createCurrencyConversionResponse(String messageId, ServiceResponse serviceResponse, Long billAmount, String billCurrency);
//
//    void updateCache(String messageId, ServiceResponse serviceResponse,Long billAmount, String billCurrency,boolean currencyConversionNotRequired);

    AggregateResponseCombined updateCache(AggregateResponse aggregateResponse);
    AggregateResponse createAggregateResponse(String messageId, String serviceName,
                                              List<ValidationResponse> validationResponseList,
                                              String messageTypeId, String aggregatorContainerId);
    AggregateResponse createAggregateResponse(String messageId, String serviceName,
                                              List<ValidationResponse> validationResponseList);

    void sendAggregateResponseToProcessor(AggregateResponse aggregateResponse);

    boolean sendMessageToAggregator(ValidationResponseSummary validationResponseSummary);

}

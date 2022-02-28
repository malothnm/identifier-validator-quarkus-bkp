package in.nmaloth.identifierValidator.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import in.nmaloth.identifierValidator.model.AggregateResponse;
import in.nmaloth.identifierValidator.model.AggregationKey;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;
import in.nmaloth.payments.constants.ServiceResponse;
import in.nmaloth.payments.constants.ids.FieldID;
import in.nmaloth.payments.constants.ids.ServiceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregationServiceImpl implements AggregationService {

    private final Logger logger = LoggerFactory.getLogger(AggregationServiceImpl.class);


    private Cache<AggregationKey, AggregateResponse> aggregateResponseCache = Caffeine.newBuilder()
            .removalListener((o, o2, removalCause) -> {
                AggregationKey aggregationKey = (AggregationKey) o;

                if(removalCause.wasEvicted()){
                    logger.info(aggregationKey.toString());

                }
            })
            .expireAfterWrite(Duration.ofMillis(2000))
            .build();




    @Override
    public AggregateResponse createCurrencyConversionResponse(String messageId, ServiceResponse serviceResponse, Long billAmount, String billCurrency) {
        AggregationKey aggregationKey = AggregationKey.builder()
                .serviceId(ServiceID.CURRENCY_CONVERSION)
                .messageId(messageId)
                .build()
                ;

        Map<String,String> fieldsMap = new HashMap<>();

        if(billAmount != null && billCurrency != null){
            fieldsMap.put(FieldID.BILLING_CURRENCY.getFieldId(), billCurrency);
            fieldsMap.put(FieldID.BILL_AMOUNT.getFieldId(),billAmount.toString());
        }

        List<String> serviceResponseList = new ArrayList<>();
        serviceResponseList.add(serviceResponse.getServiceResponse());

        ValidationResponse.Builder builder = ValidationResponse.newBuilder()
                .setServiceId(ServiceID.CURRENCY_CONVERSION)
                .addAllValidationResponse(serviceResponseList);

        if(fieldsMap.size() > 0){
            builder.putAllServiceResponseFields(fieldsMap);
        }


        return AggregateResponse.builder()
                .aggregationKey(aggregationKey)
                .validationCompleted(true)
                .validationResponse(builder.build())
                .build()
                ;
    }

    @Override
    public void updateCache(String messageId, ServiceResponse serviceResponse, Long billAmount, String billCurrency, boolean currencyConversionNotRequired) {

        if(currencyConversionNotRequired){

        }else {
            AggregateResponse aggregateResponse = createCurrencyConversionResponse(messageId,serviceResponse,billAmount,billCurrency);
            aggregateResponseCache.put(aggregateResponse.getAggregationKey(),aggregateResponse);
        }


    }

}

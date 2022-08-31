package in.nmaloth.identifierValidator.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import in.nmaloth.identifierValidator.config.EventBusNames;
import in.nmaloth.identifierValidator.config.EventProcessors;
import in.nmaloth.identifierValidator.config.ServiceNames;
import in.nmaloth.identifierValidator.listeners.MessageListener;
import in.nmaloth.identifierValidator.listeners.MessageListenerImpl;
import in.nmaloth.identifierValidator.model.AggregateResponse;
import in.nmaloth.identifierValidator.model.AggregateResponseCombined;
import in.nmaloth.identifierValidator.model.AggregationKey;
import in.nmaloth.identifierValidator.model.proto.aggregator.AggregatorResponse;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponseSummary;
import in.nmaloth.identifierValidator.processors.EventIncomingProcessor;
import in.nmaloth.identifierValidator.processors.EventIncomingProcessorImpl;
import in.nmaloth.payments.constants.ServiceResponse;
import in.nmaloth.payments.constants.ids.FieldID;
import in.nmaloth.payments.constants.ids.ServiceID;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.MultiEmitter;
import io.vertx.mutiny.core.Vertx;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


@ApplicationScoped
public class AggregationServiceImpl implements AggregationService {

    private final Logger logger = LoggerFactory.getLogger(AggregationServiceImpl.class);

    private final List<String> serviceList = List.of(ServiceID.PARAMETER_VALIDATOR,
            ServiceID.ACCOUNT_VALIDATOR, ServiceID.CARD_VALIDATOR, ServiceID.CUSTOMER_VALIDATOR);

    private Cache<String, AggregateResponseCombined> aggregateResponseCache = Caffeine.newBuilder()
            .removalListener((o, o2, removalCause) -> {
                String aggregationKey = (String) o;

                if (removalCause.wasEvicted()) {
                    logger.info(aggregationKey.toString());

                }
            })
            .expireAfterWrite(Duration.ofMillis(2000))
            .build();

    @ConfigProperty(name = "aggregate.response.threads")
    Integer aggregationResponseThreads;

    @Inject
    EventProcessors eventProcessors;

    @Inject
    Vertx vertx;

    private EventIncomingProcessor[] aggregationResponseProcessors;


    public void startup(@Observes StartupEvent startupEvent) {

        this.aggregationResponseProcessors = new EventIncomingProcessor[aggregationResponseThreads];

        for (int i = 0; i < aggregationResponseThreads; i++) {

            aggregationResponseProcessors[i] = new EventIncomingProcessorImpl<AggregateResponse>();
            MessageListener<AggregateResponse> messageListener = new MessageListenerImpl<>();
            aggregationResponseProcessors[i].registerFluxListeners(messageListener);

            Multi<AggregateResponse> multiValidationResults = Multi.createFrom()
                    .emitter((Consumer<MultiEmitter<? super AggregateResponse>>) messageListener::setEmitter);

            multiValidationResults.runSubscriptionOn(Executors.newSingleThreadExecutor())
                    .onItem().transform(aggregateResponse -> updateCache(aggregateResponse))
                    .filter(aggregateResponseCombined -> checkIfCompleted(aggregateResponseCombined))
                    .onItem().transform(aggregateResponse -> createResponseSummary(aggregateResponse))
                    .onItem().invoke(validationResponseSummary -> vertx.eventBus().send(EventBusNames.BACKUP_CACHE,validationResponseSummary))
                    .filter(validationResponseSummary -> sendMessageToAggregator(validationResponseSummary))
                    .onFailure().recoverWithItem(() -> ValidationResponseSummary.newBuilder().setMicroServiceId(ServiceNames.ERROR).build())
                    .subscribe().with(validationResponseSummary -> {
                       logger.error(" ############# Error Handling to be be build {}", validationResponseSummary.toString());
                    });


        }
    }


    @Override
    public void sendAggregateResponseToProcessor(AggregateResponse aggregateResponse) {


        int hashInt = aggregateResponse.getMessageId().hashCode();
        if (hashInt < 0) {
            hashInt = hashInt * -1;
        }
        int index = hashInt % aggregationResponseThreads;

        aggregationResponseProcessors[index].processMessage(aggregateResponse);

    }


//
//
//    @Override
//    public AggregateResponse createCurrencyConversionResponse(String messageId, ServiceResponse serviceResponse, Long billAmount, String billCurrency) {
//        AggregationKey aggregationKey = AggregationKey.builder()
//                .serviceId(ServiceID.CURRENCY_CONVERSION)
//                .messageId(messageId)
//                .build()
//                ;
//
//        Map<String,String> fieldsMap = new HashMap<>();
//
//        if(billAmount != null && billCurrency != null){
//            fieldsMap.put(FieldID.BILLING_CURRENCY.getFieldId(), billCurrency);
//            fieldsMap.put(FieldID.BILL_AMOUNT.getFieldId(),billAmount.toString());
//        }
//
//        List<String> serviceResponseList = new ArrayList<>();
//        serviceResponseList.add(serviceResponse.getServiceResponse());
//
//        ValidationResponse.Builder builder = ValidationResponse.newBuilder()
//                .setServiceId(ServiceID.CURRENCY_CONVERSION)
//                .addAllValidationResponse(serviceResponseList);
//
//        if(fieldsMap.size() > 0){
//            builder.putAllServiceResponseFields(fieldsMap);
//        }
//
//
//        return AggregateResponse.builder()
//                .aggregationKey(aggregationKey)
//                .validationCompleted(true)
//                .validationResponse(builder.build())
//                .build()
//                ;
//    }
//
////    @Override
//    public void updateCache(String messageId, ServiceResponse serviceResponse, Long billAmount, String billCurrency, boolean currencyConversionNotRequired) {
//
//        if(currencyConversionNotRequired){
//
//        }else {
//            AggregateResponse aggregateResponse = createCurrencyConversionResponse(messageId,serviceResponse,billAmount,billCurrency);
//            aggregateResponseCache.put(aggregateResponse.getAggregationKey(),aggregateResponse);
//        }
//
//
//    }


    @Override
    public boolean sendMessageToAggregator(ValidationResponseSummary validationResponseSummary) {

        boolean result = eventProcessors.aggregatorProcessor.processMessage(validationResponseSummary,
                validationResponseSummary.getAggregatorContainerId());

        if (result) {
            removeAllCache(validationResponseSummary.getMessageId());
        }
        return result;
    }

    private void removeAllCache(String messageId) {

        for (String serviceId : serviceList) {
            AggregationKey aggregationKey = new AggregationKey();
            aggregationKey.setMessageId(messageId);
            aggregationKey.setServiceId(serviceId);
            aggregateResponseCache.invalidate(aggregationKey);
        }
    }

    @Override
    public AggregateResponseCombined updateCache(AggregateResponse aggregateResponse) {

        AggregateResponseCombined aggregateResponseCombined = aggregateResponseCache.getIfPresent(aggregateResponse.getMessageId());
        if(aggregateResponseCombined == null){
            aggregateResponseCombined = new AggregateResponseCombined();
            aggregateResponseCombined.setMessageId(aggregateResponse.getMessageId());
            aggregateResponseCombined.setValidationResponse(aggregateResponse.getValidationResponse());
            aggregateResponseCombined.setServiceCompleted(new ArrayList<>());
            aggregateResponseCombined.getServiceCompleted().add(aggregateResponse.getServiceId());
            if(aggregateResponse.getServiceId().equals(ServiceID.PARAMETER_VALIDATOR)){
                aggregateResponseCombined.setAggregatorContainerId(aggregateResponse.getAggregatorContainerId());
                aggregateResponseCombined.setMessageTypeId(aggregateResponse.getMessageTypeId());
            }
            aggregateResponseCache.put(aggregateResponse.getMessageId(), aggregateResponseCombined);
        } else {
            aggregateResponseCombined.getValidationResponse().addAll(aggregateResponse.getValidationResponse());
            aggregateResponseCombined.getServiceCompleted().add(aggregateResponse.getServiceId());

            if(aggregateResponse.getServiceId().equals(ServiceID.PARAMETER_VALIDATOR)){
                aggregateResponseCombined.setAggregatorContainerId(aggregateResponse.getAggregatorContainerId());
                aggregateResponseCombined.setMessageTypeId(aggregateResponse.getMessageTypeId());
            }
        }

        return aggregateResponseCombined;

    }

    @Override
    public AggregateResponse createAggregateResponse(String messageId, String serviceName, List<ValidationResponse> validationResponseList, String messageTypeId, String aggregatorContainerId) {

//        AggregationKey aggregationKey = new AggregationKey();
//        aggregationKey.setMessageId(messageId);
//        aggregationKey.setServiceId(serviceName);

        AggregateResponse aggregateResponse = new AggregateResponse();
        aggregateResponse.setValidationResponse(validationResponseList);
        aggregateResponse.setMessageTypeId(messageTypeId);
        aggregateResponse.setAggregatorContainerId(aggregatorContainerId);
        aggregateResponse.setMessageId(messageId);
        aggregateResponse.setServiceId(serviceName);
        return aggregateResponse;
    }

    @Override
    public AggregateResponse createAggregateResponse(String messageId, String serviceName, List<ValidationResponse> validationResponseList) {

//        AggregationKey aggregationKey = new AggregationKey();
//        aggregationKey.setMessageId(messageId);
//        aggregationKey.setServiceId(serviceName);

        AggregateResponse aggregateResponse = new AggregateResponse();
        aggregateResponse.setValidationResponse(validationResponseList);
        aggregateResponse.setMessageId(messageId);
        aggregateResponse.setServiceId(serviceName);
        return aggregateResponse;
    }

    private boolean checkIfCompleted(AggregateResponseCombined aggregateResponseCombined) {


        for (ValidationResponse validationResponse: aggregateResponseCombined.getValidationResponse()) {

            for (int i = 0; i < validationResponse.getValidationResponseCount(); i ++) {
                if(validationResponse.getValidationResponse(i).equals(ServiceResponse.INVALID_CURRENCY_CODE.getServiceResponse())){
                    return true;
                }
            }
        }

        for (String serviceId : serviceList) {
            if(!aggregateResponseCombined.getServiceCompleted().contains(serviceId)){
                return false;
            }
        }

        return true;

    }

    private ValidationResponseSummary createResponseSummary(AggregateResponseCombined aggregateResponseCombined) {


        return ValidationResponseSummary.newBuilder()
                .setMicroServiceId(ServiceNames.IDENTIFIER_SERVICE)
                .setAggregatorContainerId(aggregateResponseCombined.getAggregatorContainerId())
                .setMessageTypeId(aggregateResponseCombined.getMessageTypeId())
                .addAllValidationResponseList(aggregateResponseCombined.getValidationResponse())
                .setMessageId(aggregateResponseCombined.getMessageId())
                .build()
                ;

    }


}

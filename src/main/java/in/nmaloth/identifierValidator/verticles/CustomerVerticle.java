package in.nmaloth.identifierValidator.verticles;

import in.nmaloth.identifierValidator.config.EventBusNames;
import in.nmaloth.identifierValidator.config.EventProcessors;
import in.nmaloth.identifierValidator.model.AggregateResponse;
import in.nmaloth.identifierValidator.model.AggregationKey;
import in.nmaloth.identifierValidator.model.IdentifierAmount;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.identifierValidator.services.AggregationService;
import in.nmaloth.identifierValidator.services.customer.CustomerService;
import in.nmaloth.payments.constants.ServiceResponse;
import in.nmaloth.payments.constants.ids.ServiceID;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class CustomerVerticle extends AbstractVerticle {

    @Inject
    AggregationService aggregationService;

    @Inject
    EventProcessors eventProcessors;

    @Inject
    CustomerService customerService;

    @Override
    public Uni<Void> asyncStart() {
        return vertx.eventBus().<IdentifierValidator>consumer(EventBusNames.CUSTOMER)
                .handler(identifierValidatorMessage -> processCustomer(identifierValidatorMessage.body()))
                .completionHandler();    }

    private void processCustomer(IdentifierValidator identifierValidator) {

        customerService.validateCustomerService(identifierValidator)
                .onFailure().recoverWithItem(throwable -> {
                    throwable.printStackTrace();
                    return createValidationResponseError();
                })
                .onItem().transform(validationResponse -> {


                    AggregateResponse aggregateResponse = new AggregateResponse();

                    aggregateResponse.setMessageId(identifierValidator.getMessageId());
                    aggregateResponse.setServiceId(ServiceID.CUSTOMER_VALIDATOR);


                    if(validationResponse == null){
                        aggregateResponse.setValidationResponse(new ArrayList<>());
                    } else {
                        aggregateResponse.setValidationResponse(List.of(validationResponse));
                    }
                    return aggregateResponse;

                })
                .subscribe().with(aggregateResponse -> aggregationService.sendAggregateResponseToProcessor(aggregateResponse))

        ;

    }


    private ValidationResponse createValidationResponseError(){

        return ValidationResponse.newBuilder()
                .setServiceId(ServiceID.CUSTOMER_VALIDATOR)
                .addAllValidationResponse(List.of(ServiceResponse.SYSTEM_ERROR.getServiceResponse()))
                .build();

    }

}

package in.nmaloth.identifierValidator.verticles;

import in.nmaloth.identifierValidator.config.EventBusNames;
import in.nmaloth.identifierValidator.config.EventProcessors;
import in.nmaloth.identifierValidator.model.AggregateResponse;
import in.nmaloth.identifierValidator.model.AggregationKey;
import in.nmaloth.identifierValidator.model.IdentifierAmount;
import in.nmaloth.identifierValidator.model.proto.aggregator.AggregatorService;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.identifierValidator.services.AggregationService;
import in.nmaloth.identifierValidator.services.account.AccountServices;
import in.nmaloth.payments.constants.ServiceResponse;
import in.nmaloth.payments.constants.ids.ServiceID;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;

@Dependent
public class AccountVerticle extends AbstractVerticle {


    @Inject
    AccountServices accountServices;


    @Inject
    AggregationService aggregationService;

    @Inject
    EventProcessors eventProcessors;



    @Override
    public Uni<Void> asyncStart() {

        return vertx.eventBus().<IdentifierAmount>consumer(EventBusNames.ACCOUNT)
                .handler(identifierAmountMessage -> processAccount(identifierAmountMessage.body()))
                .completionHandler();    }

    private void processAccount(IdentifierAmount identifierAmount) {



        accountServices.validateAccount(identifierAmount.getIdentifierValidator(), identifierAmount.getConvertAmount())
                .onFailure().recoverWithItem((throwable) -> {
                    throwable.printStackTrace();
                    return createValidationResponseError();
                })

                .onItem().transform(validationResponse -> {

                    AggregateResponse aggregateResponse = new AggregateResponse();
                    aggregateResponse.setMessageId(identifierAmount.getIdentifierValidator().getMessageId());
                    aggregateResponse.setServiceId(ServiceID.ACCOUNT_VALIDATOR);

                    aggregateResponse.setValidationResponse(List.of(validationResponse));
                    return aggregateResponse;
                })
                .subscribe().with(aggregateResponse -> aggregationService.sendAggregateResponseToProcessor(aggregateResponse))

        ;



    }

    private ValidationResponse createValidationResponseError(){

        return ValidationResponse.newBuilder()
                .setServiceId(ServiceID.ACCOUNT_VALIDATOR)
                .addAllValidationResponse(List.of(ServiceResponse.SYSTEM_ERROR.getServiceResponse()))
                .build();

    }


}

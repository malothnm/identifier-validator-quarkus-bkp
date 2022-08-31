package in.nmaloth.identifierValidator.verticles;

import in.nmaloth.identifierValidator.config.EventBusNames;
import in.nmaloth.identifierValidator.config.EventProcessors;
import in.nmaloth.identifierValidator.model.AggregateResponse;
import in.nmaloth.identifierValidator.model.AggregationKey;
import in.nmaloth.identifierValidator.model.IdentifierAmount;
import in.nmaloth.identifierValidator.model.ProductCriteria;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;
import in.nmaloth.identifierValidator.services.AggregationService;
import in.nmaloth.identifierValidator.services.card.CardsService;
import in.nmaloth.identifierValidator.services.parameters.ParameterService;
import in.nmaloth.payments.constants.ServiceResponse;
import in.nmaloth.payments.constants.ids.ServiceID;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Dependent
public class CardVerticle extends AbstractVerticle {


    @Inject
    AggregationService aggregationService;

    @Inject
    EventProcessors eventProcessors;

    @Inject
    ParameterService parameterService;

    @Inject
    CardsService cardsService;

    @Override
    public Uni<Void> asyncStart() {
        return vertx.eventBus().<IdentifierAmount>consumer(EventBusNames.CARDS)
                .handler(identifierAmountMessage -> processCard(identifierAmountMessage.body()))
                .completionHandler();    }

    private void processCard(IdentifierAmount identifierAmount) {

        Optional<ProductCriteria> productCriteriaOptional = parameterService.findCriteriaRecord(identifierAmount.getIdentifierValidator().getOrg(),
                identifierAmount.getIdentifierValidator().getProduct(), identifierAmount.getIdentifierValidator().getCriteria());

        if (productCriteriaOptional.isPresent()) {

            cardsService.validateCards(identifierAmount.getIdentifierValidator(),
                    productCriteriaOptional.get(), identifierAmount.getConvertAmount())
                    .onFailure().recoverWithItem(throwable -> {
                        throwable.printStackTrace();
                        return createValidationResponseError();
                    })
                    .onItem().transform(validationResponse -> {


                        AggregateResponse aggregateResponse = new AggregateResponse();

                        aggregateResponse.setMessageId(identifierAmount.getIdentifierValidator().getMessageId());
                        aggregateResponse.setServiceId(ServiceID.CARD_VALIDATOR);
                        aggregateResponse.setValidationResponse(List.of(validationResponse));
                        return aggregateResponse;

                    })
                    .subscribe().with(aggregateResponse -> aggregationService.sendAggregateResponseToProcessor(aggregateResponse))

            ;

        } else {

            AggregateResponse aggregateResponse = new AggregateResponse();

            aggregateResponse.setMessageId(identifierAmount.getIdentifierValidator().getMessageId());
            aggregateResponse.setServiceId(ServiceID.CARD_VALIDATOR);

            aggregateResponse.setValidationResponse(
                    List.of(
                            ValidationResponse.newBuilder()
                                    .setServiceId(ServiceID.CARD_VALIDATOR)
                                    .addAllValidationResponse(List.of(ServiceResponse.NO_PRODUCT.getServiceResponse()))
                                    .build()
                    )
            );

            aggregationService.sendAggregateResponseToProcessor(aggregateResponse);


        }

    }

    private ValidationResponse createValidationResponseError(){

        return ValidationResponse.newBuilder()
                .setServiceId(ServiceID.CARD_VALIDATOR)
                .addAllValidationResponse(List.of(ServiceResponse.SYSTEM_ERROR.getServiceResponse()))
                .build();

    }
}

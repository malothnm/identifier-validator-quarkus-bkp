package in.nmaloth.identifierValidator.verticles;

import in.nmaloth.identifierValidator.config.EventBusNames;
import in.nmaloth.identifierValidator.config.EventProcessors;
import in.nmaloth.identifierValidator.config.ServiceNames;
import in.nmaloth.identifierValidator.exception.CurrencyCodeNotFoundException;
import in.nmaloth.identifierValidator.model.ProductCriteria;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponseSummary;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.identifierValidator.services.CurrencyConversionService;
import in.nmaloth.identifierValidator.services.account.AccountServices;
import in.nmaloth.identifierValidator.services.card.CardsService;
import in.nmaloth.identifierValidator.services.customer.CustomerService;
import in.nmaloth.identifierValidator.services.parameters.ParameterService;
import in.nmaloth.payments.constants.ServiceResponse;
import in.nmaloth.payments.constants.ids.FieldID;
import in.nmaloth.payments.constants.ids.ServiceID;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Dependent
public class MessageProcessorVerticle extends AbstractVerticle {

    @Inject
    CurrencyConversionService currencyConversionService;

    @Inject
    CustomerService customerService;

    @Inject
    AccountServices accountServices;

    @Inject
    ParameterService parameterService;

    @Inject
    CardsService cardsService;

    @Inject
    EventProcessors eventProcessors;


    @Override
    public Uni<Void> asyncStart() {

        return vertx.eventBus().<IdentifierValidator>consumer(EventBusNames.PROCESS_MESSAGE)
                .handler(identifierValidatorMessage -> processIdentifierMessage(identifierValidatorMessage.body()))
                .completionHandler();
    }

    private void processIdentifierMessage(IdentifierValidator identifierValidator) {

        ValidationResponseSummary.Builder builder = ValidationResponseSummary.newBuilder()
                .setMessageId(identifierValidator.getMessageId())
                .setMessageTypeId(identifierValidator.getMessageTypeId())
                .setAggregatorContainerId(identifierValidator.getAggregatorInstance())
                .setMicroServiceId(ServiceNames.IDENTIFIER_SERVICE);

        String settlementCurrencyCode = null;
        if (identifierValidator.hasSettlementCurrCode()) {
            settlementCurrencyCode = identifierValidator.getSettlementCurrCode();
        } else {
            settlementCurrencyCode = currencyConversionService.getSettlementCurrencyCode(identifierValidator.getTransactionCurrencyCode());
        }

        try {
            long convertAmount = currencyConversionService.convertTransactionAmount(identifierValidator.getTransactionCurrencyCode(), identifierValidator.getBillingCurrencyCode(), identifierValidator.getTransactionAmount(), settlementCurrencyCode);

            List<ValidationResponse> validationResponseList = new ArrayList<>();
            if (!(identifierValidator.getTransactionCurrencyCode().equals(settlementCurrencyCode) && identifierValidator.getBillingCurrencyCode().equals(settlementCurrencyCode))) {
                Map<String, String> serviceFieldMap = Map.of(
                        FieldID.BILL_AMOUNT.getFieldId(), Long.valueOf(convertAmount).toString(),
                        FieldID.BILLING_CURRENCY.getFieldId(), identifierValidator.getBillingCurrencyCode()
                );
                validationResponseList.add(ValidationResponse.newBuilder()
                        .setServiceId(ServiceID.CURRENCY_CONVERSION)
                        .addAllValidationResponse(List.of(ServiceResponse.OK.getServiceResponse()))
                        .putAllServiceResponseFields(serviceFieldMap)
                        .build());

            }


            Optional<ProductCriteria> productCriteriaOptional = parameterService.findCriteriaRecord(identifierValidator.getOrg(), identifierValidator.getProduct(), identifierValidator.getCriteria());

            if (productCriteriaOptional.isPresent()) {
                ProductCriteria productCriteria = productCriteriaOptional.get();
                List<String> serviceResponseList = parameterService.validateParameters(identifierValidator, productCriteria);

                if (serviceResponseList.size() == 0) {

                    ValidationResponse validationResponseParameter = ValidationResponse.newBuilder()
                            .addAllValidationResponse(List.of(ServiceResponse.OK.getServiceResponse())).build();

                    Uni<ValidationResponse> cardServiceResponseUni = cardsService.validateCards(identifierValidator, productCriteria, convertAmount);

                    Uni<ValidationResponse> accountServiceResponseUni = accountServices.validateAccount(identifierValidator, convertAmount);

                    Uni<ValidationResponse> customerServiceResponseUni = customerService.validateCustomerService(identifierValidator);

                    Uni.combine().all()
                            .unis(cardServiceResponseUni, accountServiceResponseUni, customerServiceResponseUni)
                            .asTuple()
                            .subscribe().with(tuple3 -> {

                                validationResponseList.add(tuple3.getItem1());
                                validationResponseList.add(tuple3.getItem2());
                                if(tuple3.getItem3() != null){
                                    validationResponseList.add(tuple3.getItem3());
                                }
                                builder.addAllValidationResponseList(validationResponseList);
                                ValidationResponseSummary validationResponseSummary = builder.build();
                                eventProcessors.aggregatorProcessor.processMessage(validationResponseSummary,identifierValidator.getAggregatorInstance());
                                vertx.eventBus().send(EventBusNames.BACKUP_CACHE, validationResponseSummary);

                            });


                } else {
                    // create response Message
                    ;
                    builder.addAllValidationResponseList(List.of(ValidationResponse.newBuilder()
                            .setServiceId(ServiceID.PARAMETER_VALIDATOR)
                            .addAllValidationResponse(serviceResponseList)
                            .build()));

                    ValidationResponseSummary validationResponseSummary = builder.build();
                    eventProcessors.aggregatorProcessor.processMessage(validationResponseSummary,identifierValidator.getAggregatorInstance());

                    vertx.eventBus().send(EventBusNames.BACKUP_CACHE, validationResponseSummary);

                }

            } else {

                builder.addAllValidationResponseList(List.of(ValidationResponse.newBuilder()
                        .setServiceId(ServiceID.PARAMETER_VALIDATOR)
                        .addAllValidationResponse(List.of(ServiceResponse.NO_PRODUCT.getServiceResponse()))
                        .build()));

                ValidationResponseSummary validationResponseSummary = builder.build();
                eventProcessors.aggregatorProcessor.processMessage(validationResponseSummary,identifierValidator.getAggregatorInstance());

                vertx.eventBus().send(EventBusNames.BACKUP_CACHE, validationResponseSummary);

            }

        } catch (CurrencyCodeNotFoundException e) {

            builder.addAllValidationResponseList(List.of(ValidationResponse.newBuilder()
                    .setServiceId(ServiceID.CURRENCY_CONVERSION)
                    .addAllValidationResponse(List.of(ServiceResponse.INVALID_CURRENCY_CODE.getServiceResponse()))
                    .build()));

            ValidationResponseSummary validationResponseSummary = builder.build();
            eventProcessors.aggregatorProcessor.processMessage(validationResponseSummary,identifierValidator.getAggregatorInstance());

            vertx.eventBus().send(EventBusNames.BACKUP_CACHE, validationResponseSummary);

        }


    }
}

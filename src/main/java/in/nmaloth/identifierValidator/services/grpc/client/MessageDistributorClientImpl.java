package in.nmaloth.identifierValidator.services.grpc.client;

import in.nmaloth.identifierValidator.config.EventBusNames;
import in.nmaloth.identifierValidator.config.EventProcessors;
import in.nmaloth.identifierValidator.config.RequestNames;
import in.nmaloth.identifierValidator.config.ServiceNames;
import in.nmaloth.identifierValidator.listeners.MessageListener;
import in.nmaloth.identifierValidator.listeners.MessageListenerImpl;
import in.nmaloth.identifierValidator.model.proto.identifier.*;
import in.nmaloth.identifierValidator.serviceEvents.model.ServiceAction;
import in.nmaloth.identifierValidator.serviceEvents.model.ServiceEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.MultiEmitter;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class MessageDistributorClientImpl implements MessageDistributorClient{


    private static final Logger logger = LoggerFactory.getLogger(MessageDistributorClientImpl.class);


    @Inject
    EventProcessors eventProcessors;

    @Inject
    Vertx vertx;


    @Override
    public void createDistributorStream(ServiceEvent serviceEvent) {


        MutinyIdentifierServiceGrpc.MutinyIdentifierServiceStub client = MutinyIdentifierServiceGrpc.newMutinyStub(serviceEvent.getChannel());

        EventBus eventBus = vertx.eventBus();

        MessageListener<IdentifierResponse> messageListener = new MessageListenerImpl<>();


        client.sendMessage(getOutgoingResponseMulti(messageListener))
                .onItem().invoke(identifierValidator -> {

                    if (identifierValidator.hasRegistration()) {

                        logger.info("###############????? : Server Registration Completed {}", identifierValidator.getRegistration().toString());
                        messageListener.setServiceName(identifierValidator.getRegistration().getServiceName());
                        messageListener.setServiceInstance(identifierValidator.getRegistration().getServiceInstance());

                        eventProcessors.distributorProcessor.processMessage(IdentifierResponse.newBuilder()
                                .setMessageId(UUID.randomUUID().toString().replace("-",""))
                                .setStatusUpdateIdentifier(StatusUpdateIdentifier.newBuilder()
                                        .setServiceInstance(eventProcessors.INSTANCE)
                                        .setServiceName(ServiceNames.IDENTIFIER_SERVICE)
                                        .setReadyStatus(true)
                                        .build()).build(),identifierValidator.getRegistration().getServiceInstance())
                        ;

                    } else if(identifierValidator.hasStatusUpdateIdentifier()){
                        eventProcessors.distributorProcessor.updateReadyStatus(identifierValidator.getStatusUpdateIdentifier().getServiceInstance(),
                                identifierValidator.getStatusUpdateIdentifier().getServiceName(), identifierValidator.getStatusUpdateIdentifier().getReadyStatus());
                    }
                })
                .onFailure().retry().atMost(1L)
                .onFailure().recoverWithMulti(() -> {
                    logger.info(" ################ Entered Termination");
                    return Multi.createFrom().emitter(multiEmitter -> multiEmitter.complete());
                })
                .onTermination().invoke(() -> {

                    logger.info(" ################ Entered Termination");
                    ServiceEvent serviceEvent1 = ServiceEvent.builder().serviceName(serviceEvent.getServiceName())
                            .requestName(RequestNames.DISTRIBUTOR)
                            .serviceAction(ServiceAction.REMOVE_CLIENT)
                            .instance(serviceEvent.getInstance())
                            .attempts(serviceEvent.getAttempts())
                            .build();

                    logger.info(" Terminated Connection for Service {} Instance {} ", serviceEvent.getServiceName(), serviceEvent.getInstance());
                    eventBus.send(EventBusNames.SERVICE_EVENTS, serviceEvent1);

                }).filter(identifierValidator -> !(identifierValidator.hasRegistration() || identifierValidator.hasStatusUpdateIdentifier()))

                .subscribe().with(identifierValidator -> {

                        eventBus.send(EventBusNames.PROCESS_MESSAGE, identifierValidator);
//
//                        eventProcessors.distributorProcessor.processMessage(IdentifierResponse.newBuilder()
//                                .setMessageId(identifierValidator.getMessageId())
//                                .setCompleted(true)
//                                .build(),messageListener.getServiceInstance()
//                        );


                });


    }

    @Override
    public Multi<IdentifierResponse> getOutgoingResponseMulti(MessageListener<IdentifierResponse> messageListener) {
        return Multi.createFrom()
                .<IdentifierResponse>emitter(multiEmitter -> updateEventProcessors(multiEmitter, messageListener))
                .onFailure().invoke(throwable -> throwable.printStackTrace())

                ;    }


    private void updateEventProcessors(MultiEmitter<? super IdentifierResponse> multiEmitter, MessageListener<IdentifierResponse> messageListener) {

        messageListener.setServiceName(ServiceNames.DISTRIBUTOR);
//        messageListener.setServiceInstance(eventProcessors.INSTANCE);
        messageListener.setEmitter(multiEmitter);

        multiEmitter.emit(IdentifierResponse.newBuilder()
                .setMessageId(UUID.randomUUID().toString().replace("-", ""))
                .setCompleted(true)
                .setRegistration(RegistrationIdentifier.newBuilder()
                        .setServiceName(ServiceNames.IDENTIFIER_SERVICE)
                        .setServiceInstance(eventProcessors.INSTANCE)
                        .build())
                .build());


        eventProcessors.distributorProcessor.registerFluxListeners(messageListener);


    }


}

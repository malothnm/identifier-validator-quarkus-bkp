package in.nmaloth.testResource;



import in.nmaloth.identifierValidator.config.ServiceNames;
import in.nmaloth.identifierValidator.listeners.MessageListener;
import in.nmaloth.identifierValidator.listeners.MessageListenerImpl;
import in.nmaloth.identifierValidator.model.proto.aggregator.*;
import in.nmaloth.identifierValidator.processors.EventOutgoingProcessor;
import in.nmaloth.identifierValidator.processors.EventOutgoingProcessorImpl;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.MultiEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AggregatorClient {

    private static final Logger logger = LoggerFactory.getLogger(AggregatorClient.class);


    public EventOutgoingProcessor<AggregatorResponse> eventOutgoingProcessor = new EventOutgoingProcessorImpl<>();

    public final String INSTANCE = UUID.randomUUID().toString().replace("-", "");

    public List<ValidationResponseSummary> validationSummaryMessageList = new ArrayList<>();


    public void createAggregatorResponseStream(String host, Integer port) {


        ManagedChannel managedChannel = createNewChannel(host, port);

        MutinyAggregatorServiceGrpc.MutinyAggregatorServiceStub client = MutinyAggregatorServiceGrpc.newMutinyStub(managedChannel);


        MessageListener<AggregatorResponse> messageListener = new MessageListenerImpl<>();


        client.aggregatorStream(getOutgoingResponseMulti(messageListener))
                .onFailure().retry().atMost(1L)
                .onFailure().recoverWithMulti(() -> {
                    logger.info(" ################ Entered Termination");
                    return Multi.createFrom().emitter(multiEmitter -> multiEmitter.complete());
                })
                .onTermination().invoke(() -> {

                    logger.info(" ################ Entered Termination");


                })

                .subscribe().with(validationResponseSummary -> {
                    if (validationResponseSummary.hasRegistration()) {

                        logger.info("###############????? : Server Registration Completed  for connector{}", validationResponseSummary.toString());
                        messageListener.setServiceName(validationResponseSummary.getRegistration().getServiceName());
                        messageListener.setServiceInstance(validationResponseSummary.getRegistration().getServiceInstance());

                        eventOutgoingProcessor.processMessage(AggregatorResponse.newBuilder()
                                .setMessageId(UUID.randomUUID().toString().replace("-", ""))
                                .setStatusUpdateAggregator(
                                        StatusUpdateAggregator.newBuilder()
                                                .setServiceName(ServiceNames.AGGREGATOR_SERVICE)
                                                .setServiceName(INSTANCE)
                                                .setReadyStatus(true))
                                .build(),validationResponseSummary.getRegistration().getServiceInstance()
                        );


                    } else {
                        logger.info("###############????? : Actual Message {}", validationResponseSummary.getMessageId());
                        validationSummaryMessageList.add(validationResponseSummary);
                    }
                });

    }


    private ManagedChannel createNewChannel(String host, Integer port) {

        ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forAddress(host, port);
        builder.usePlaintext();
        return builder.build();
    }


    public Multi<AggregatorResponse> getOutgoingResponseMulti(MessageListener<AggregatorResponse> messageListener) {


        return Multi.createFrom()
                .<AggregatorResponse>emitter(multiEmitter -> updateEventProcessors(multiEmitter, messageListener))
                .onFailure().invoke(throwable -> throwable.printStackTrace())

                ;

    }

    private void updateEventProcessors(MultiEmitter<? super AggregatorResponse> multiEmitter, MessageListener<AggregatorResponse> messageListener) {

//        messageListener.setServiceName(ServiceNames.AGGREGATOR_SERVICE);
//        messageListener.setServiceInstance(eventProcessors.INSTANCE);
        messageListener.setEmitter(multiEmitter);

        multiEmitter.emit(AggregatorResponse.newBuilder()
                .setMessageId(UUID.randomUUID().toString().replace("-", ""))
                .setRegistration(RegistrationAggregator.newBuilder()
                        .setServiceName(ServiceNames.AGGREGATOR_SERVICE)
                        .setServiceInstance(INSTANCE)
                        .build())
                .build());


        eventOutgoingProcessor.registerFluxListeners(messageListener);


    }

}

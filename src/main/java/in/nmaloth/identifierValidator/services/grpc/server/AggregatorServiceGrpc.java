package in.nmaloth.identifierValidator.services.grpc.server;

import in.nmaloth.identifierValidator.config.CacheConfig;
import in.nmaloth.identifierValidator.config.EventBusNames;
import in.nmaloth.identifierValidator.config.EventProcessors;
import in.nmaloth.identifierValidator.config.ServiceNames;
import in.nmaloth.identifierValidator.listeners.MessageListener;
import in.nmaloth.identifierValidator.listeners.MessageListenerImpl;
import in.nmaloth.identifierValidator.model.proto.aggregator.*;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

@GrpcService
public class AggregatorServiceGrpc implements AggregatorService {

    @Inject
    EventProcessors eventProcessors;


    private static final Logger logger = LoggerFactory.getLogger(AggregatorServiceGrpc.class);


    @Override
    public Multi<ValidationResponseSummary> aggregatorStream(Multi<AggregatorResponse> request) {
        MessageListener<ValidationResponseSummary> messageListener = new MessageListenerImpl<>();

        request.onItem().invoke(aggregatorResponse -> {

                    logger.info("########### {}", aggregatorResponse.getMessageId());
                    if (aggregatorResponse.hasRegistration()) {
                        messageListener.setServiceName(aggregatorResponse.getRegistration().getServiceName());
                        messageListener.setServiceInstance(aggregatorResponse.getRegistration().getServiceInstance());

                        eventProcessors.aggregatorProcessor.processMessage(ValidationResponseSummary.newBuilder()
                                .setMessageId(aggregatorResponse.getMessageId())
                                .setRegistration(RegistrationAggregator.newBuilder()
                                        .setServiceName(ServiceNames.IDENTIFIER_SERVICE)
                                        .setServiceInstance(eventProcessors.INSTANCE)
                                        .build()).build(), aggregatorResponse.getRegistration().getServiceInstance());


                        eventProcessors.aggregatorProcessor.processMessage(ValidationResponseSummary.newBuilder()
                                .setMessageId(UUID.randomUUID().toString().replace("-",""))
                                .setStatusUpdateAggregator(StatusUpdateAggregator.newBuilder()
                                        .setServiceName(ServiceNames.IDENTIFIER_SERVICE)
                                        .setServiceInstance(eventProcessors.INSTANCE)
                                        .setReadyStatus(true)
                                        .build()).build(), aggregatorResponse.getRegistration().getServiceInstance());

                    } else if (aggregatorResponse.hasStatusUpdateAggregator()){
                        eventProcessors.aggregatorProcessor.updateReadyStatus(aggregatorResponse.getStatusUpdateAggregator().getServiceInstance(),
                                aggregatorResponse.getStatusUpdateAggregator().getServiceName(),
                                aggregatorResponse.getStatusUpdateAggregator().getReadyStatus());
                    }
                })
                .onFailure().invoke(throwable -> {
                    throwable.printStackTrace();
                })
                .onCancellation().invoke(() -> logger.info(" Cancelled the Stream {} for instance {}", messageListener.getServiceName(), messageListener.getServiceInstance()))
                .onTermination().invoke(() -> {

                    messageListener.getEmitter().complete();
                    eventProcessors.aggregatorProcessor.removeRegisteredFluxListener(messageListener);

                })
                .filter(aggregatorResponse -> !(aggregatorResponse.hasRegistration()|| aggregatorResponse.hasStatusUpdateAggregator()))
                .subscribe().with(aggregatorResponse -> {


                    // update cache for response
                });

        return getAggregatorMulti(messageListener);
    }

    private Multi<ValidationResponseSummary> getAggregatorMulti(MessageListener<ValidationResponseSummary> messageListener) {
        return Multi.createFrom().<ValidationResponseSummary>emitter(multiEmitter -> {

                    messageListener.setEmitter(multiEmitter);
                    eventProcessors.aggregatorProcessor.registerFluxListeners(messageListener);

                }).onFailure().invoke(() -> {
                    logger.info("Removed the listener {} for instance ", messageListener.toString(), messageListener.getServiceInstance());

                }).onCancellation().invoke(() -> logger.info(" Cancelled Stream for {} and Instance {}", messageListener.getServiceName(), messageListener.getServiceInstance()))
                .onTermination().invoke(() -> {
                    logger.info("Terminated the listener {} for instance {}", messageListener.toString(), messageListener.getServiceInstance());
                    eventProcessors.aggregatorProcessor.removeRegisteredFluxListener(messageListener);
                })

                ;
    }
}

package in.nmaloth.testResource;

import in.nmaloth.identifierValidator.config.ServiceNames;
import in.nmaloth.identifierValidator.listeners.MessageListener;
import in.nmaloth.identifierValidator.model.proto.identifier.*;
import in.nmaloth.identifierValidator.processors.EventOutgoingProcessor;
import io.quarkus.deployment.ide.Ide;
import io.smallrye.mutiny.Multi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class DistributorServer extends MutinyIdentifierServiceGrpc.IdentifierServiceImplBase {



    private static final Logger logger = LoggerFactory.getLogger(DistributorServer.class);
    private final EventOutgoingProcessor<IdentifierValidator> eventOutgoingProcessor;

    public final List<IdentifierResponse> outgoingResponseList;

    private final MessageListener<IdentifierValidator> messageListener;


    private final String instance;

    public DistributorServer(EventOutgoingProcessor<IdentifierValidator> eventOutgoingProcessor,
                             List<IdentifierResponse> outgoingResponseList,
                             MessageListener<IdentifierValidator> messageListener, String instance) {
        this.eventOutgoingProcessor = eventOutgoingProcessor;
        this.outgoingResponseList = outgoingResponseList;
        this.messageListener = messageListener;
        this.instance = instance;
    }


    @Override
    public Multi<IdentifierValidator> sendMessage(Multi<IdentifierResponse> request) {


        request.onItem().invoke(identifierResponse -> {
            logger.info(" ######################### Entered here ");
            logger.info(identifierResponse.toString());
            if (identifierResponse.hasRegistration()) {
                messageListener.setServiceInstance(identifierResponse.getRegistration().getServiceInstance());
                messageListener.setServiceName(identifierResponse.getRegistration().getServiceName());


                IdentifierValidator identifierValidator = IdentifierValidator.newBuilder()
                        .setMessageId(UUID.randomUUID().toString())
                        .setRegistration(                RegistrationIdentifier.newBuilder()
                                .setServiceInstance(instance)
                                .setServiceName(ServiceNames.DISTRIBUTOR)
                                .build())
                        .build();
                eventOutgoingProcessor.processMessage(identifierValidator, identifierResponse.getRegistration().getServiceInstance());

                eventOutgoingProcessor.processMessage(IdentifierValidator.newBuilder()
                        .setMessageId(UUID.randomUUID().toString().replace("-",""))
                        .setStatusUpdateIdentifier(StatusUpdateIdentifier.newBuilder()
                                .setServiceInstance(instance)
                                .setServiceName(ServiceNames.DISTRIBUTOR)
                                .setReadyStatus(true).build()
                        ).build(),identifierResponse.getRegistration().getServiceInstance());
            } else  if(identifierResponse.hasStatusUpdateIdentifier()){

                eventOutgoingProcessor.updateReadyStatus(identifierResponse.getStatusUpdateIdentifier().getServiceInstance(),
                        identifierResponse.getStatusUpdateIdentifier().getServiceName(),identifierResponse.getStatusUpdateIdentifier().getReadyStatus());
            }else {
                outgoingResponseList.add(identifierResponse);
            }

        });



        return Multi.createFrom().<IdentifierValidator>emitter(multiEmitter -> {

                    messageListener.setEmitter(multiEmitter);
                    eventOutgoingProcessor.registerFluxListeners(messageListener);


                }).onFailure().invoke(() -> {
                    logger.info("Removed the listener {} for instance ", messageListener.toString(), messageListener.getServiceInstance());

                }).onCancellation().invoke(() -> logger.info(" Cancelled Stream for {} and Instance {}", messageListener.getServiceName(), messageListener.getServiceInstance()))
                .onTermination().invoke(() -> {
                    logger.info("Terminated the listener {} for instance {}", messageListener.toString(), messageListener.getServiceInstance());
                    eventOutgoingProcessor.removeRegisteredFluxListener(messageListener);
                })

                ;
    }


}

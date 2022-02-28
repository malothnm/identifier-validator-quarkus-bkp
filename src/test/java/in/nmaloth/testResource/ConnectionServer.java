package in.nmaloth.testResource;

import in.nmaloth.identifierValidator.listeners.MessageListener;
import in.nmaloth.identifierValidator.listeners.MessageListenerImpl;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierResponse;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.identifierValidator.processors.EventOutgoingProcessor;
import in.nmaloth.identifierValidator.processors.EventOutgoingProcessorImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConnectionServer {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionServer.class);
    public EventOutgoingProcessor<IdentifierValidator> distributorProcessor = new EventOutgoingProcessorImpl<>();
    public final String instance = UUID.randomUUID().toString().replace("-","");

    public final List<IdentifierResponse> distributorResponseList = new ArrayList<>();
    public final MessageListener<IdentifierValidator> messageListener = new MessageListenerImpl<>();



    public Server server;


    public void start(Integer port){


        server = ServerBuilder.forPort(port).addService(new IdentifierServiceGrpc(distributorProcessor, distributorResponseList, messageListener, instance)).build();
        try {
            server.start();
            logger.info(" Server started on port {}",port);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public void stop(){
        server.shutdown();
    }
}

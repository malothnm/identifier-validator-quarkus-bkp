package in.nmaloth.identifierValidator.services.grpc.client;

import in.nmaloth.identifierValidator.listeners.MessageListener;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierResponse;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.identifierValidator.serviceEvents.model.ServiceEvent;
import io.smallrye.mutiny.Multi;

public interface MessageDistributorClient {


    void createDistributorStream(ServiceEvent serviceEvent);

    Multi<IdentifierResponse> getOutgoingResponseMulti(MessageListener<IdentifierResponse> messageListener);
}

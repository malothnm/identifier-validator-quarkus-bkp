package in.nmaloth.identifierValidator.config;

import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierResponse;
import in.nmaloth.identifierValidator.processors.EventIncomingProcessor;
import in.nmaloth.identifierValidator.processors.EventIncomingProcessorImpl;
import in.nmaloth.identifierValidator.processors.EventOutgoingProcessor;
import in.nmaloth.identifierValidator.processors.EventOutgoingProcessorImpl;
import in.nmaloth.identifierValidator.serviceEvents.model.ServiceEvent;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponseSummary;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class EventProcessors {


    public final EventOutgoingProcessor<ValidationResponseSummary> aggregatorProcessor =  new EventOutgoingProcessorImpl<>();
    public final EventOutgoingProcessor<IdentifierResponse> distributorProcessor =  new EventOutgoingProcessorImpl<>();

    public final EventIncomingProcessor<ServiceEvent> serviceEventProcessor = new EventIncomingProcessorImpl<>();


    public final String INSTANCE = UUID.randomUUID().toString().replace("-","");




}

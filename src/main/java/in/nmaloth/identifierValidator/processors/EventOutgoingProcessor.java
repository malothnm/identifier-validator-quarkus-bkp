package in.nmaloth.identifierValidator.processors;

import in.nmaloth.identifierValidator.listeners.MessageListener;
import io.smallrye.mutiny.tuples.Tuple2;

import java.util.List;

public interface EventOutgoingProcessor<T> {

    void registerFluxListeners(MessageListener<T> messageListener, String serviceInstance, String serviceName);

    void registerFluxListeners(MessageListener<T> messageListener);

    void removeRegisteredFluxListener(String serviceInstance);
    void removeRegisteredFluxListener(MessageListener<T> messageListener);



    void processMessage(T message);
    boolean processMessage(T message,String instance);


    List<Tuple2<String,String>> getOutgoingFluxInfo();
}

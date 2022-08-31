package in.nmaloth.identifierValidator.processors;

import in.nmaloth.identifierValidator.listeners.MessageListener;
import io.smallrye.mutiny.tuples.Tuple2;

import java.util.List;
import java.util.Optional;

public interface EventOutgoingProcessor<T> {


    void registerFluxListeners(MessageListener<T> messageListener, String serviceInstance, String serviceName);

    void registerFluxListeners(MessageListener<T> messageListener);

    void removeRegisteredFluxListener(String serviceInstance);
    void removeRegisteredFluxListener(MessageListener<T> messageListener);



    void processMessage(T message);

    boolean processMessage(T message,String instance);

    Optional<MessageListener<T>> selectListener();

    List<MessageListener<T>> getAllMessageListeners();


    List<Tuple2<String,String>> getOutgoingFluxInfo();

    void updateReadyStatus(String serviceInstance, String serviceName,boolean status);

    boolean getReadyStatus();

    Optional<String> fetchNextInstance();
}

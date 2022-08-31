package in.nmaloth.identifierValidator.listeners;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.MultiEmitter;

public interface MessageListener <T>{

    void processMessage(T message);
    void processClose();
    void setEmitter(MultiEmitter<? super T> emitter);
    MultiEmitter<? super T> getEmitter();
    void setServiceInstance(String serviceInstance);
    void setServiceNameAndInstance(String serviceName, String instance);
    void setServiceNameInstanceAndIdentifier(String serviceName, String instance, String identifier);

    void setIdentifier(String identifier);
    boolean checkIdentifier(String identifier);
    String getServiceInstance();

    String getIdentifier();

    void setServiceName(String serviceName);
    String getServiceName();

    void setMulti(Multi<T> multi);

    void setReadyStatus(boolean readyStatus);
    boolean getReadyStatus();



}

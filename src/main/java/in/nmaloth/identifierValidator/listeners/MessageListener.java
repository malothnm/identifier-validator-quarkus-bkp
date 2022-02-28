package in.nmaloth.identifierValidator.listeners;

import io.smallrye.mutiny.subscription.MultiEmitter;

public interface MessageListener <T>{

    void processMessage(T message);
    void processClose();
    void setEmitter(MultiEmitter<? super T> emitter);
    MultiEmitter<? super T> getEmitter();
    void setServiceInstance(String serviceInstance);
    String getServiceInstance();

    void setServiceName(String serviceName);
    String getServiceName();



}

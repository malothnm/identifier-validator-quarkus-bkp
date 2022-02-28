package in.nmaloth.identifierValidator.listeners;


import io.smallrye.mutiny.subscription.MultiEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageListenerImpl<T> implements MessageListener<T> {

    private MultiEmitter<? super T> emitter;
    private String serviceInstance;
    private String serviceName;

    private static Logger log = LoggerFactory.getLogger(MessageListenerImpl.class);


    public MessageListenerImpl(MultiEmitter<? super T> emitter) {
        this.emitter = emitter;
    }

    public MessageListenerImpl(MultiEmitter<? super T> emitter,  String serviceName,String serviceInstance) {
        this.emitter = emitter;
        this.serviceInstance = serviceInstance;
        this.serviceName = serviceName;
    }



    public MessageListenerImpl() {
    }

    @Override
    public void processMessage(T message) {


        this.emitter.emit(message);
    }

    @Override
    public void processClose() {
        this.emitter.complete();
    }

    @Override
    public String getServiceInstance() {
        return serviceInstance;
    }

    @Override
    public void setServiceInstance(String serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public void setEmitter(MultiEmitter<? super T> emitter) {
        this.emitter = emitter;
    }

    @Override
    public MultiEmitter<? super T> getEmitter() {
        return emitter;
    }
}

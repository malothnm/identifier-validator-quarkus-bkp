package in.nmaloth.identifierValidator.processors;


import in.nmaloth.identifierValidator.listeners.MessageListener;

public interface EventIncomingProcessor<T> {

    void registerFluxListeners(MessageListener<T> messageListener);
    void processMessage(T message);


}

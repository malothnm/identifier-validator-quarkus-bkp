package in.nmaloth.identifierValidator.processors;


import in.nmaloth.identifierValidator.listeners.MessageListener;

public class EventIncomingProcessorImpl <T> implements EventIncomingProcessor<T>{

    private MessageListener<T> messageListener;

    @Override
    public void registerFluxListeners(MessageListener<T> messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public void processMessage(T message) {
        messageListener.processMessage(message);
    }
}

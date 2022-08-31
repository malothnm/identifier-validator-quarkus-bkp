package in.nmaloth.identifierValidator.processors;

import in.nmaloth.identifierValidator.listeners.MessageListener;
import io.smallrye.mutiny.tuples.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EventOutgoingProcessorImpl<T> implements EventOutgoingProcessor<T> {


    private static final Logger log = LoggerFactory.getLogger(EventOutgoingProcessorImpl.class);

    private List<MessageListener<T>> messageListenerList = new ArrayList<>();
    private AtomicInteger roundRobin = new AtomicInteger(0);

    private boolean readyForProcessing;


    @Override
    public void registerFluxListeners(MessageListener<T> messageListener, String serviceInstance, String serviceName) {

        messageListener.setServiceInstance(serviceInstance);
        messageListener.setServiceName(serviceName);
        messageListenerList.add(messageListener);

    }



    @Override
    public void updateReadyStatus(String serviceInstance, String serviceName,boolean status) {

        for ( MessageListener<T> messageListener: messageListenerList) {

            if(messageListener.getServiceInstance().equals(serviceInstance) && messageListener.getServiceName().equals(serviceName)){
                messageListener.setReadyStatus(status);
                break;
            }

        }

        checkForProcessingStatus();
    }

    @Override
    public boolean getReadyStatus() {
        return readyForProcessing;
    }

    @Override
    public Optional<String> fetchNextInstance() {

        if(!readyForProcessing){
            return Optional.empty();
        }

        Optional<MessageListener<T>> messageListenerOptional = Optional.empty();
        boolean notProcessedMessage = true;

        do {
            messageListenerOptional = selectListener();

            if(messageListenerOptional.isPresent()){
                try {
                    notProcessedMessage = true;


                    MessageListener<T> messageListener = messageListenerOptional.get();
                    if(messageListener.getEmitter().isCancelled()){
                        messageListenerList.remove(messageListener);
                        checkForProcessingStatus();
                    } else {

                        if(messageListener.getServiceInstance() != null && messageListener.getReadyStatus()){
                            return Optional.of(messageListenerOptional.get().getServiceInstance());
                        }
                    }
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        while ( messageListenerOptional.isPresent() && messageListenerOptional.get().getEmitter() != null &&
                notProcessedMessage);

        if(messageListenerOptional.isEmpty()){
            log.info(" No Active Flux ...");

        }
        return Optional.empty();
    }

    @Override
    public void registerFluxListeners(MessageListener<T> messageListener) {

        messageListenerList.add(messageListener);
    }

    @Override
    public void removeRegisteredFluxListener(String serviceInstance) {

        Optional<MessageListener<T>> messageListenerOptional = messageListenerList.stream()
                .filter(messageListener -> messageListener.getServiceInstance().equalsIgnoreCase(serviceInstance))
                .findFirst();

        if(messageListenerOptional.isPresent()){
            messageListenerList.remove(messageListenerOptional.get());
        }

        checkForProcessingStatus();


    }

    public void checkForProcessingStatus() {
        boolean readyToProcess = false;
        for (MessageListener<T> messageListener: messageListenerList) {

            if(messageListener.getReadyStatus()){
                readyToProcess = true;
                break;
            }
        }

        if(readyToProcess){
            this.readyForProcessing = true;
        } else {
            this.readyForProcessing = false;
        }
    }

    @Override
    public void removeRegisteredFluxListener(MessageListener<T> messageListener) {

        Optional<MessageListener<T>> messageListenerOptional = messageListenerList.stream()
                .filter(messageListener1 -> messageListener.equals(messageListener1))
                .findFirst();

        if(messageListenerOptional.isPresent()){
            messageListenerList.remove(messageListenerOptional.get());
        }

        log.info(" ############### Removing Listeners  {}",messageListener.toString());



    }

    @Override
    public void processMessage(T message) {

        if(!readyForProcessing){
            return;
        }

        Optional<MessageListener<T>> messageListenerOptional = Optional.empty();
        boolean notProcessedMessage = true;

        do {
            messageListenerOptional = selectListener();

            if(messageListenerOptional.isPresent()){
                try {
                    notProcessedMessage = true;


                    MessageListener<T> messageListener = messageListenerOptional.get();
                    if(messageListener.getEmitter().isCancelled()){
                        messageListenerList.remove(messageListener);
                        checkForProcessingStatus();
                    } else {

                        if(messageListener.getServiceInstance() != null && messageListener.getReadyStatus()){
                            messageListenerOptional.get().processMessage(message);
                            notProcessedMessage = false;
                        }
                    }
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        while ( messageListenerOptional.isPresent() && messageListenerOptional.get().getEmitter() != null &&
                notProcessedMessage);

        if(messageListenerOptional.isEmpty()){
            log.info(" No Active Flux ...");

        }

    }


    @Override
    public Optional<MessageListener<T>> selectListener(){
        int size = messageListenerList.size();
        if(size > 0 ){
            int roundRobin = this.roundRobin.incrementAndGet();
            int selectedListener = roundRobin%size;
            if(roundRobin > 99999){
                this.roundRobin = new AtomicInteger(0);
            }

            return Optional.ofNullable(messageListenerList.get(selectedListener));
        }
        return Optional.empty();
    }

    @Override
    public List<MessageListener<T>> getAllMessageListeners() {
        return messageListenerList;
    }


    @Override
    public boolean processMessage(T message, String instance) {

        if(instance == null){
            return false;
        }

        Optional<MessageListener<T>> messageListenerOptional = fetchOutgoingListenerInfo(instance);

        if(messageListenerOptional.isEmpty()){
            return false;
        }

        if(messageListenerOptional.get().getEmitter().isCancelled()){
            return false;
        }

        try {
            MessageListener<T> messageListener = messageListenerOptional.get();
            messageListener.processMessage(message);
            return true;

        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }

    }



    private Optional<MessageListener<T>> fetchOutgoingListenerInfo(String instance) {

        for (MessageListener<T>  messageListener:messageListenerList ) {

            if(messageListener.getServiceInstance().equals(instance)){
                return Optional.of(messageListener);
            }
        }
        return Optional.empty();
    }



    @Override
    public List<Tuple2<String, String>> getOutgoingFluxInfo() {

        return messageListenerList.stream()
                .map(tMessageListener -> Tuple2.of(tMessageListener.getServiceName(),tMessageListener.getServiceInstance()))
                .collect(Collectors.toList());

    }
}

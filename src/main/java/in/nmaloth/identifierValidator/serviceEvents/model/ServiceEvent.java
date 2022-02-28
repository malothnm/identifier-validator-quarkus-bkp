package in.nmaloth.identifierValidator.serviceEvents.model;

import io.grpc.ManagedChannel;

public class ServiceEvent {

    private ServiceAction serviceAction;

    private String serviceName;

    private String instance;

    private String requestName;

    private String host;

    private Integer port;

    private ManagedChannel channel;

    private Integer attempts;

    public ServiceEvent(ServiceAction serviceAction, String serviceName, String instance,
                        String requestName, String host, Integer port, ManagedChannel channel, Integer attempts) {
        this.serviceAction = serviceAction;
        this.serviceName = serviceName;
        this.instance = instance;
        this.requestName = requestName;
        this.host = host;
        this.port = port;
        this.channel = channel;
        this.attempts = attempts;
    }

    public ServiceEvent() {
    }


    @Override
    public String toString() {
        return "ServiceEvent{" +
                "serviceAction=" + serviceAction +
                ", serviceName='" + serviceName + '\'' +
                ", instance='" + instance + '\'' +
                ", requestName='" + requestName + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", channel=" + channel +
                ", attempts=" + attempts +
                '}';
    }

    public ServiceAction getServiceAction() {
        return serviceAction;
    }

    public void setServiceAction(ServiceAction serviceAction) {
        this.serviceAction = serviceAction;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public ManagedChannel getChannel() {
        return channel;
    }

    public static ServiceEventBuilder builder(){
        return new ServiceEventBuilder();
    }

    public void setChannel(ManagedChannel channel) {
        this.channel = channel;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public static class ServiceEventBuilder {

        private ServiceAction serviceAction;
        private String serviceName;
        private String instance;
        private String requestName;
        private String host;
        private Integer port;
        private ManagedChannel channel;
        private Integer attempts;

        public ServiceEventBuilder serviceAction(ServiceAction serviceActions){
            this.serviceAction = serviceActions;
            return this;
        }

        public ServiceEventBuilder serviceName(String serviceName){
            this.serviceName = serviceName;
            return this;
        }

        public ServiceEventBuilder instance(String instance){
            this.instance = instance;
            return this;
        }

        public ServiceEventBuilder requestName(String requestName){
            this.requestName = requestName;
            return this;
        }

        public ServiceEventBuilder host(String host){
            this.host = host;
            return this;
        }

        public ServiceEventBuilder port(Integer port ){
            this.port = port;
            return this;
        }
        public ServiceEventBuilder channel(ManagedChannel channel){
            this.channel = channel;
            return this;
        }

        public ServiceEventBuilder attempts(Integer attempts){
            this.attempts = attempts;
            return this;
        }

        public ServiceEvent build(){
            return new ServiceEvent(serviceAction,serviceName,instance,requestName,host,port, channel,attempts);
        }
    }
}

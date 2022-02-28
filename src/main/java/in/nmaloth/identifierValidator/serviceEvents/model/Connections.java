package in.nmaloth.identifierValidator.serviceEvents.model;

import io.grpc.ManagedChannel;

public class Connections {

    private String serviceName;
    private String instance;
    private String host;
    private Integer port;
    private ManagedChannel managedChannel;

    public Connections(String serviceName, String instance,String host, Integer port, ManagedChannel managedChannel) {
        this.serviceName = serviceName;
        this.instance = instance;
        this.host = host;
        this.port = port;
        this.managedChannel = managedChannel;
    }

    public Connections() {
    }

    @Override
    public String toString() {
        return "Connections{" +
                "serviceName='" + serviceName + '\'' +
                ", instance='" + instance + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
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

    public ManagedChannel getManagedChannel() {
        return managedChannel;
    }

    public void setManagedChannel(ManagedChannel managedChannel) {
        this.managedChannel = managedChannel;
    }

    public static ConnectionSBuilder builder() {
        return new ConnectionSBuilder();
    }

    public static class ConnectionSBuilder {


        private String serviceName;
        private String instance;
        private String host;
        private Integer port;
        private ManagedChannel managedChannel;

        public ConnectionSBuilder serviceName(String serviceName){
            this.serviceName = serviceName;
            return this;
        }

        public ConnectionSBuilder instance(String instance){
            this.instance = instance;
            return  this;
        }

        public ConnectionSBuilder host(String host){
            this.host = host;
            return this;
        }

        public ConnectionSBuilder port(Integer port ){
            this.port = port;
            return this;
        }
        public ConnectionSBuilder managedChannel(ManagedChannel managedChannel){
            this.managedChannel = managedChannel;
            return this;
        }

        public Connections build(){
            return new Connections(serviceName,instance,host,port,managedChannel);
        }

    }
}

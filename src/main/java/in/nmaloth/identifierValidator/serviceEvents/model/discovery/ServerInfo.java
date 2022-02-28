package in.nmaloth.identifierValidator.serviceEvents.model.discovery;

public class ServerInfo {

    private String serviceName;
    private String serviceInstance;
    private String host;
    private Integer port;

    public ServerInfo(String serviceName, String serviceInstance, String host, Integer port) {
        this.serviceName = serviceName;
        this.serviceInstance = serviceInstance;
        this.host = host;
        this.port = port;
    }

    public ServerInfo() {
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(String serviceInstance) {
        this.serviceInstance = serviceInstance;
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

    public static ServerInfoBuilder builder(){
        return new ServerInfoBuilder();
    }

    public static class ServerInfoBuilder {

        private String serviceName;
        private String serviceInstance;
        private String host;
        private Integer port;


        public ServerInfoBuilder serviceName(String serviceName){
            this.serviceName = serviceName;
            return this;
        }

        public ServerInfoBuilder serviceInstance(String serviceInstance){
            this.serviceInstance = serviceInstance;
            return this;
        }

        public ServerInfoBuilder host(String host){
            this.host = host;
            return this;
        }

        public ServerInfoBuilder port(Integer port){
            this.port = port;
            return this;
        }
        public ServerInfo build(){
            return new ServerInfo(serviceName,serviceInstance,host,port);
        }

    }
}

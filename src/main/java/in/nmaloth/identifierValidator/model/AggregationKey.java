package in.nmaloth.identifierValidator.model;


import java.util.Objects;

public class AggregationKey {

    private String messageId;
    private String serviceId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AggregationKey)) return false;
        AggregationKey that = (AggregationKey) o;
        return getMessageId().equals(that.getMessageId()) && getServiceId().equals(that.getServiceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessageId(), getServiceId());
    }

    public AggregationKey(String messageId, String serviceId) {
        this.messageId = messageId;
        this.serviceId = serviceId;
    }

    public AggregationKey() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String toString() {
        return "AggregationKey{" +
                "messageId='" + messageId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                '}';
    }

    public static AggregationKeyBuilder builder(){
        return new AggregationKeyBuilder();
    }

    public static class AggregationKeyBuilder {

        private String messageId;
        private String serviceId;

        public AggregationKeyBuilder messageId(String messageId){
            this.messageId = messageId;
            return this;
        }
        public AggregationKeyBuilder serviceId(String serviceId){
            this.serviceId = serviceId;
            return this;
        }
        public AggregationKey build(){
            return new AggregationKey(this.messageId,this.serviceId);
        }

    }
}

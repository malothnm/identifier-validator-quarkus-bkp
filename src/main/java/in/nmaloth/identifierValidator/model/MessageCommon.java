package in.nmaloth.identifierValidator.model;



public class MessageCommon {

    private String messageId;
    private String aggregatorInstance;
    private String messageTypeId;
    private boolean messageSendForAggregation;


    public MessageCommon(String messageId, String aggregatorInstance, String messageTypeId, boolean messageSendForAggregation) {
        this.messageId = messageId;
        this.aggregatorInstance = aggregatorInstance;
        this.messageTypeId = messageTypeId;
        this.messageSendForAggregation = messageSendForAggregation;
    }

    public MessageCommon() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getAggregatorInstance() {
        return aggregatorInstance;
    }

    public void setAggregatorInstance(String aggregatorInstance) {
        this.aggregatorInstance = aggregatorInstance;
    }

    public String getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(String messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public boolean isMessageSendForAggregation() {
        return messageSendForAggregation;
    }

    public void setMessageSendForAggregation(boolean messageSendForAggregation) {
        this.messageSendForAggregation = messageSendForAggregation;
    }

    public static MessageCommonBuilder builder(){

        return new MessageCommonBuilder();
    }

    public static class MessageCommonBuilder {

        private String messageId;
        private String aggregatorInstance;
        private String messageTypeId;
        private boolean messageSendForAggregation;

        public MessageCommonBuilder messageId(String messageId){
            this.messageId = messageId;
            return this;
        }

        public MessageCommonBuilder aggregatorInstance(String aggregatorInstance){
            this.aggregatorInstance = aggregatorInstance;
            return this;
        }

        public MessageCommonBuilder messageTypeId(String messageTypeId){
            this.messageTypeId = messageTypeId;
            return this;
        }


        public MessageCommonBuilder messageSendForAggregation(boolean messageSendForAggregation){
            this.messageSendForAggregation = messageSendForAggregation;
            return this;
        }

        public MessageCommon build(){
            return new MessageCommon(messageId,aggregatorInstance,messageTypeId,messageSendForAggregation);
        }

    }
}

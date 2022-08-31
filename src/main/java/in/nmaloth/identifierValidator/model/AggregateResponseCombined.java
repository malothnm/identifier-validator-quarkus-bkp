package in.nmaloth.identifierValidator.model;

import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;

import java.util.List;


public class AggregateResponseCombined {

    private String messageId;
    private List<ValidationResponse> validationResponse;
    private String messageTypeId;
    private String aggregatorContainerId;
    private List<String> serviceCompleted;
//    private boolean validationCompleted;


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }


    public List<ValidationResponse> getValidationResponse() {
        return validationResponse;
    }

    public void setValidationResponse(List<ValidationResponse> validationResponse) {
        this.validationResponse = validationResponse;
    }

    public String getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(String messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public String getAggregatorContainerId() {
        return aggregatorContainerId;
    }

    public void setAggregatorContainerId(String aggregatorContainerId) {
        this.aggregatorContainerId = aggregatorContainerId;
    }

    public List<String> getServiceCompleted() {
        return serviceCompleted;
    }

    public void setServiceCompleted(List<String> serviceCompleted) {
        this.serviceCompleted = serviceCompleted;
    }
}

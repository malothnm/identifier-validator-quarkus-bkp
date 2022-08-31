package in.nmaloth.identifierValidator.model;

import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;

import java.util.List;


public class AggregateResponse {

    private String messageId;
    private String serviceId;
    private List<ValidationResponse> validationResponse;
    private String messageTypeId;
    private String aggregatorContainerId;
//    private boolean validationCompleted;


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

}

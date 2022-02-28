package in.nmaloth.identifierValidator.model;

import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;


public class AggregateResponse {

    private AggregationKey aggregationKey;
    private ValidationResponse validationResponse;
    private boolean validationCompleted;

    public AggregateResponse(AggregationKey aggregationKey, ValidationResponse validationResponse, boolean validationCompleted) {
        this.aggregationKey = aggregationKey;
        this.validationResponse = validationResponse;
        this.validationCompleted = validationCompleted;
    }

    public AggregateResponse() {
    }

    public AggregationKey getAggregationKey() {
        return aggregationKey;
    }

    public void setAggregationKey(AggregationKey aggregationKey) {
        this.aggregationKey = aggregationKey;
    }

    public ValidationResponse getValidationResponse() {
        return validationResponse;
    }

    public void setValidationResponse(ValidationResponse validationResponse) {
        this.validationResponse = validationResponse;
    }

    public boolean isValidationCompleted() {
        return validationCompleted;
    }

    public void setValidationCompleted(boolean validationCompleted) {
        this.validationCompleted = validationCompleted;
    }

    public static AggregateResponseBuilder builder(){
        return new AggregateResponseBuilder();
    }

    public static class AggregateResponseBuilder {

        private AggregationKey aggregationKey;
        private ValidationResponse validationResponse;
        private boolean validationCompleted;


        public AggregateResponseBuilder aggregationKey(AggregationKey aggregationKey){
            this.aggregationKey = aggregationKey;
            return this;
        }

        public AggregateResponseBuilder validationResponse(ValidationResponse validationResponse){
            this.validationResponse = validationResponse;
            return this;
        }

        public AggregateResponseBuilder validationCompleted(boolean validationCompleted){
            this.validationCompleted = validationCompleted;
            return this;
        }

        public AggregateResponse build(){
            return new AggregateResponse(aggregationKey,validationResponse,validationCompleted);
        }
    }
}

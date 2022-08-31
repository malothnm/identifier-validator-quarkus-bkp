package in.nmaloth.identifierValidator.model;

import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;

public class IdentifierAmount {

    private IdentifierValidator identifierValidator;
    private long convertAmount;

    public IdentifierAmount(IdentifierValidator identifierValidator, long convertAmount) {
        this.identifierValidator = identifierValidator;
        this.convertAmount = convertAmount;
    }

    public IdentifierAmount() {
    }

    public IdentifierValidator getIdentifierValidator() {
        return identifierValidator;
    }

    public void setIdentifierValidator(IdentifierValidator identifierValidator) {
        this.identifierValidator = identifierValidator;
    }

    public long getConvertAmount() {
        return convertAmount;
    }

    public void setConvertAmount(long convertAmount) {
        this.convertAmount = convertAmount;
    }
}

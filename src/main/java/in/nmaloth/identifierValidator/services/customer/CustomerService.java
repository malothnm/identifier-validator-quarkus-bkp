package in.nmaloth.identifierValidator.services.customer;

import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import io.smallrye.mutiny.Uni;

public interface CustomerService {

    Uni<ValidationResponse> validateCustomerService(IdentifierValidator identifierValidator);

}

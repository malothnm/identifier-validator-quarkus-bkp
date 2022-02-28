package in.nmaloth.identifierValidator.services.customer;

import com.google.common.base.CharMatcher;
import in.nmaloth.identifierValidator.model.entity.customer.CustomerDef;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.payments.constants.ResponseResults.AddressVerificationResults;
import in.nmaloth.payments.constants.ServiceResponse;
import in.nmaloth.payments.constants.ids.FieldID;
import in.nmaloth.payments.constants.ids.ServiceID;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@ApplicationScoped
public class CustomerServiceImpl implements CustomerService {




    CharMatcher matcher = CharMatcher.forPredicate(Character::isLetterOrDigit);
    CharMatcher matcherDigits = CharMatcher.forPredicate(Character::isDigit);




    @Override
    public Uni<ValidationResponse> validateCustomerService(IdentifierValidator identifierValidator) {

        if(identifierValidator.hasCustomerName() || identifierValidator.hasAddress() || identifierValidator.hasPostalCode()) {

            return CustomerDef.findByCustomerId(identifierValidator.getCustomerNumber())
                    .map(customerDefOptional -> validateCustomer(customerDefOptional, identifierValidator))
                    ;

        } else {
            return Uni.createFrom().nullItem();
        }



    }


    private ValidationResponse validateCustomer(Optional<CustomerDef> customerDefOptional,
                                                IdentifierValidator identifierValidator) {

        List<String> serviceResponseList = new ArrayList<>();
        ValidationResponse.Builder builder = ValidationResponse.newBuilder()
                .setServiceId(ServiceID.CUSTOMER_VALIDATOR);


        if (customerDefOptional.isEmpty()) {
             serviceResponseList.add(ServiceResponse.NO_ENTRY.getServiceResponse());
             return builder.addAllValidationResponse(serviceResponseList).build();
        }

        CustomerDef customerDef = customerDefOptional.get();
        if (identifierValidator.hasCustomerName()) {
            boolean nameResult = validateName(identifierValidator.getCustomerName(), customerDef.getCustomerName());
            if (nameResult) {
            } else {
                serviceResponseList.add(ServiceResponse.INVALID_NAME.getServiceResponse());
//                return builder.addAllValidationResponse(serviceResponseList).build();

            }
        }
        AddressVerificationResults addressVerificationResults = validateAddress(identifierValidator, customerDef);

        if (addressVerificationResults.equals(AddressVerificationResults.ADDRESS_INFORMATION_NOT_VERIFIED) ||
                (addressVerificationResults.equals(AddressVerificationResults.STREET_ADDRESS_POSTAL_CODE_MATCH))) {
            if(serviceResponseList.size() == 0){
                serviceResponseList.add(ServiceResponse.OK.getServiceResponse());
            }
        } else {
            serviceResponseList.add(ServiceResponse.ADDRESS_VERIFICATION.getServiceResponse());
        }

        builder.addAllValidationResponse(serviceResponseList);
        builder.putAllServiceResponseFields(Map.of(FieldID.ADDRESS_VERIFICATION.getFieldId(), addressVerificationResults.getAddressVerificationResult()));


        return builder.build();

    }


    private AddressVerificationResults validateAddress(IdentifierValidator identifierValidator, CustomerDef customerDef) {

        if (identifierValidator.hasAddress()) {
            boolean addressMatch = validateAddress(identifierValidator.getAddress(), customerDef.getAddressLine());

            if (identifierValidator.hasPostalCode()) {
                boolean postalCodeMatch = validateZipCode(identifierValidator.getPostalCode(), customerDef.getPostalCode());

                if (addressMatch) {
                    if (postalCodeMatch) {
                        return AddressVerificationResults.STREET_ADDRESS_POSTAL_CODE_MATCH;
                    } else {
                        return AddressVerificationResults.ADDRESS_MATCH_ZIP_CODE_NO_MATCH;
                    }
                } else {
                    if (postalCodeMatch) {
                        return AddressVerificationResults.POSTAL_CODE_MATCH_ADDRESS_NO_MATCH;
                    }
                    return AddressVerificationResults.NO_MATCH;
                }
            } else {

                if (addressMatch) {
                    return AddressVerificationResults.STREET_ADDRESS_MATCH_POSTAL_CODE_NOT_VERIFIED;

                } else {
                    return AddressVerificationResults.NO_MATCH;
                }
            }

        } else {
            if (identifierValidator.hasPostalCode()) {

                if (validateZipCode(identifierValidator.getPostalCode(), customerDef.getPostalCode())) {

                    return AddressVerificationResults.POSTAL_CODE_MATCH_ADDRESS_NO_MATCH;
                } else {

                    return AddressVerificationResults.NO_MATCH;
                }


            } else {
                return AddressVerificationResults.ADDRESS_INFORMATION_NOT_VERIFIED;
            }
        }

    }

    private boolean validateName(String incomingName, String customerName) {

        return matcher.retainFrom(incomingName).equalsIgnoreCase(matcher.retainFrom(customerName));
    }

    private boolean validateAddress(String incomingAddress, String customerAddress) {

        String incomingFormattedAddress = matcher.retainFrom(incomingAddress);
        String customerFormatAddress = matcher.retainFrom(customerAddress);
        if (incomingFormattedAddress.equalsIgnoreCase(customerFormatAddress)) {
            return true;
        }
        return matcherDigits.retainFrom(incomingFormattedAddress).equals(matcherDigits.retainFrom(customerFormatAddress));
    }


    private boolean validateZipCode(String incomingZipCode, String customerZipCode) {

        return (matcher.retainFrom(incomingZipCode).equalsIgnoreCase(matcher.retainFrom(customerZipCode)));

    }


}

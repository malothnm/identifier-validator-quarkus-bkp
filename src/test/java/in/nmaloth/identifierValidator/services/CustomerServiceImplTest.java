package in.nmaloth.identifierValidator.services;

import in.nmaloth.identifierValidator.model.entity.customer.CustomerDef;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.identifierValidator.services.customer.CustomerService;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.ResponseResults.AddressVerificationResults;
import in.nmaloth.payments.constants.account.BalanceTypes;
import in.nmaloth.payments.constants.card.LimitType;
import in.nmaloth.payments.constants.customer.AddressType;
import in.nmaloth.payments.constants.customer.CustomerType;
import in.nmaloth.payments.constants.ids.FieldID;
import in.nmaloth.payments.constants.ids.ServiceID;
import in.nmaloth.testResource.GRPCWireResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(GRPCWireResource.class)
class CustomerServiceImplTest {


    @Inject
    private CustomerService customerService;

    @BeforeEach
    void setup() {

        CustomerDef.findAll()
                .list()
                .await().indefinitely()
                .forEach(customerDef -> customerDef.delete().await().indefinitely());

    }

    @Test
    void validateCustomerServiceValidNameAddressAndPostalCode() {

        String customerId = setupCustomer("test name", "123 testAddress 456", "672019");
        String containerId = UUID.randomUUID().toString().replace("-", "");
        String accountId = UUID.randomUUID().toString().replace("-", "");
        String cardId = UUID.randomUUID().toString().replace("-", "");
        String instrument = UUID.randomUUID().toString().replace("-", "");
        String aggregatorInstance = UUID.randomUUID().toString().replace("-", "");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());

        IdentifierValidator.Builder builder = createIdentifierValidator(containerId, accountId, cardId, instrument,
                customerId, aggregatorInstance, balanceTypeList, limitTypeList, "test name", "123 testAddress 456", "672019");

        IdentifierValidator validator = builder.build();
        ValidationResponse validationResponse = customerService.validateCustomerService(validator).await().indefinitely();

        assertAll(
                () -> assertEquals(ServiceResponse.OK.getServiceResponse(), validationResponse.getValidationResponse(0)),
                () -> assertEquals(ServiceID.CUSTOMER_VALIDATOR, validationResponse.getServiceId()),
                () -> assertEquals(1, validationResponse.getValidationResponseCount()),
                () -> assertEquals(AddressVerificationResults.STREET_ADDRESS_POSTAL_CODE_MATCH.getAddressVerificationResult(), validationResponse.getServiceResponseFieldsMap().get(FieldID.ADDRESS_VERIFICATION.getFieldId()))
        );
        ;
    }

    @Test
    void validateCustomerServiceAdjustedNameValidAddressAndPostalCode() {

        String customerId = setupCustomer("test   name", "123 testAddress 456", "672019");
        String containerId = UUID.randomUUID().toString().replace("-", "");
        String accountId = UUID.randomUUID().toString().replace("-", "");
        String cardId = UUID.randomUUID().toString().replace("-", "");
        String instrument = UUID.randomUUID().toString().replace("-", "");
        String aggregatorInstance = UUID.randomUUID().toString().replace("-", "");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());

        IdentifierValidator.Builder builder = createIdentifierValidator(containerId, accountId, cardId, instrument,
                customerId, aggregatorInstance, balanceTypeList, limitTypeList, "test name", "123 testAddress 456", "672019");

        IdentifierValidator validator = builder.build();
        ValidationResponse validationResponse = customerService.validateCustomerService(validator).await().indefinitely();


        assertAll(
                () -> assertEquals(ServiceResponse.OK.getServiceResponse(), validationResponse.getValidationResponse(0)),
                () -> assertEquals(ServiceID.CUSTOMER_VALIDATOR, validationResponse.getServiceId()),
                () -> assertEquals(1, validationResponse.getValidationResponseCount()),
                () -> assertEquals(AddressVerificationResults.STREET_ADDRESS_POSTAL_CODE_MATCH.getAddressVerificationResult(), validationResponse.getServiceResponseFieldsMap().get(FieldID.ADDRESS_VERIFICATION.getFieldId()))
        );

    }

    @Test
    void validateCustomerServiceInvalidNameValidAddressAndPostalCode() {

        String customerId = setupCustomer("test   name  1", "123 testAddress 456", "672019");
        String containerId = UUID.randomUUID().toString().replace("-", "");
        String accountId = UUID.randomUUID().toString().replace("-", "");
        String cardId = UUID.randomUUID().toString().replace("-", "");
        String instrument = UUID.randomUUID().toString().replace("-", "");
        String aggregatorInstance = UUID.randomUUID().toString().replace("-", "");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());

        IdentifierValidator.Builder builder = createIdentifierValidator(containerId, accountId, cardId, instrument,
                customerId, aggregatorInstance, balanceTypeList, limitTypeList, "test name", "123 testAddress 456", "672019");

        IdentifierValidator validator = builder.build();
        ValidationResponse validationResponse = customerService.validateCustomerService(validator).await().indefinitely();


        assertAll(
                () -> assertEquals(ServiceResponse.INVALID_NAME.getServiceResponse(), validationResponse.getValidationResponse(0)),
                () -> assertEquals(1, validationResponse.getValidationResponseCount()),
                () -> assertEquals(ServiceID.CUSTOMER_VALIDATOR, validationResponse.getServiceId()),
                () -> assertEquals(1, validationResponse.getServiceResponseFieldsCount()),
                () -> assertEquals(AddressVerificationResults.STREET_ADDRESS_POSTAL_CODE_MATCH.getAddressVerificationResult(),
                        validationResponse.getServiceResponseFieldsMap().get(FieldID.ADDRESS_VERIFICATION.getFieldId()))


        )

        ;
    }

    @Test
    void validateCustomerServiceNoNameInValidAddressAndPostalCode() {

        String customerId = setupCustomer("test   name  1", "123 testAddress 456", "672019");
        String containerId = UUID.randomUUID().toString().replace("-", "");
        String accountId = UUID.randomUUID().toString().replace("-", "");
        String cardId = UUID.randomUUID().toString().replace("-", "");
        String instrument = UUID.randomUUID().toString().replace("-", "");
        String aggregatorInstance = UUID.randomUUID().toString().replace("-", "");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());

        IdentifierValidator.Builder builder = createIdentifierValidator(containerId, accountId, cardId, instrument,
                customerId, aggregatorInstance, balanceTypeList, limitTypeList, null, "123 testAddress 4568", "672017");

        IdentifierValidator validator = builder.build();
        ValidationResponse validationResponse = customerService.validateCustomerService(validator).await().indefinitely();



        assertAll(

                () -> assertEquals(ServiceResponse.ADDRESS_VERIFICATION.getServiceResponse(), validationResponse.getValidationResponse(0)),
                () -> assertEquals(1, validationResponse.getValidationResponseCount()),
                () -> assertEquals(ServiceID.CUSTOMER_VALIDATOR, validationResponse.getServiceId()),
                () -> assertEquals(1, validationResponse.getServiceResponseFieldsCount()),
                () -> assertEquals(AddressVerificationResults.NO_MATCH.getAddressVerificationResult(), validationResponse.getServiceResponseFieldsMap().get(FieldID.ADDRESS_VERIFICATION.getFieldId()))

        );



    }

    @Test
    void validateCustomerServiceNoNameInValidAddressNoPostalCode() {

        String customerId = setupCustomer("test   name  1", "123 testAddress 456", "672019");
        String containerId = UUID.randomUUID().toString().replace("-", "");
        String accountId = UUID.randomUUID().toString().replace("-", "");
        String cardId = UUID.randomUUID().toString().replace("-", "");
        String instrument = UUID.randomUUID().toString().replace("-", "");
        String aggregatorInstance = UUID.randomUUID().toString().replace("-", "");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());

        IdentifierValidator.Builder builder = createIdentifierValidator(containerId, accountId, cardId, instrument,
                customerId, aggregatorInstance, balanceTypeList, limitTypeList, null, "123 testAddress 4568", null);

        IdentifierValidator validator = builder.build();
        ValidationResponse validationResponse = customerService.validateCustomerService(validator).await().indefinitely();


        assertAll(
                () -> assertEquals(ServiceResponse.ADDRESS_VERIFICATION.getServiceResponse(), validationResponse.getValidationResponse(0)),
                () -> assertEquals(1, validationResponse.getValidationResponseCount()),
                () -> assertEquals(ServiceID.CUSTOMER_VALIDATOR, validationResponse.getServiceId()),
                () -> assertEquals(1, validationResponse.getServiceResponseFieldsCount()),
                () -> assertEquals(AddressVerificationResults.NO_MATCH.getAddressVerificationResult(), validationResponse.getServiceResponseFieldsMap().get(FieldID.ADDRESS_VERIFICATION.getFieldId()))
        );
    }

    @Test
    void validateCustomerServiceNoNameInValidAddressValidPostalCode() {

        String customerId = setupCustomer("test   name  1", "123 testAddress 456", "682019");
        String containerId = UUID.randomUUID().toString().replace("-", "");
        String accountId = UUID.randomUUID().toString().replace("-", "");
        String cardId = UUID.randomUUID().toString().replace("-", "");
        String instrument = UUID.randomUUID().toString().replace("-", "");
        String aggregatorInstance = UUID.randomUUID().toString().replace("-", "");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());

        IdentifierValidator.Builder builder = createIdentifierValidator(containerId, accountId, cardId, instrument,
                customerId, aggregatorInstance, balanceTypeList, limitTypeList, null, "123 testAddress 4568", "682019");

        IdentifierValidator validator = builder.build();
        ValidationResponse validationResponse = customerService.validateCustomerService(validator).await().indefinitely();


        assertAll(
                () -> assertEquals(ServiceResponse.ADDRESS_VERIFICATION.getServiceResponse(), validationResponse.getValidationResponse(0)),
                () -> assertEquals(1, validationResponse.getValidationResponseCount()),
                () -> assertEquals(ServiceID.CUSTOMER_VALIDATOR, validationResponse.getServiceId()),
                () -> assertEquals(1, validationResponse.getServiceResponseFieldsCount()),
                () -> assertEquals(AddressVerificationResults.POSTAL_CODE_MATCH_ADDRESS_NO_MATCH.getAddressVerificationResult(), validationResponse.getServiceResponseFieldsMap().get(FieldID.ADDRESS_VERIFICATION.getFieldId()))
        );

    }

    @Test
    void validateCustomerServiceNoNameValidAddressNoPostalCode() {

        String customerId = setupCustomer("test   name  1", "123 testAddress 456", "682019");
        String containerId = UUID.randomUUID().toString().replace("-", "");
        String accountId = UUID.randomUUID().toString().replace("-", "");
        String cardId = UUID.randomUUID().toString().replace("-", "");
        String instrument = UUID.randomUUID().toString().replace("-", "");
        String aggregatorInstance = UUID.randomUUID().toString().replace("-", "");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());

        IdentifierValidator.Builder builder = createIdentifierValidator(containerId, accountId, cardId, instrument,
                customerId, aggregatorInstance, balanceTypeList, limitTypeList, null, "123 testAddress 456", null);

        IdentifierValidator validator = builder.build();
        ValidationResponse validationResponse = customerService.validateCustomerService(validator).await().indefinitely();


        assertAll(
                () -> assertEquals(ServiceResponse.ADDRESS_VERIFICATION.getServiceResponse(), validationResponse.getValidationResponse(0)),
                () -> assertEquals(1, validationResponse.getValidationResponseCount()),
                () -> assertEquals(ServiceID.CUSTOMER_VALIDATOR, validationResponse.getServiceId()),
                () -> assertEquals(1, validationResponse.getServiceResponseFieldsCount()),
                () -> assertEquals(AddressVerificationResults.STREET_ADDRESS_MATCH_POSTAL_CODE_NOT_VERIFIED.getAddressVerificationResult(), validationResponse.getServiceResponseFieldsMap().get(FieldID.ADDRESS_VERIFICATION.getFieldId()))
        );

    }

    @Test
    void validateCustomerServiceNoNameNoAddressValidPostalCode() {

        String customerId = setupCustomer("test   name  1", "123 testAddress 456", "682019");
        String containerId = UUID.randomUUID().toString().replace("-", "");
        String accountId = UUID.randomUUID().toString().replace("-", "");
        String cardId = UUID.randomUUID().toString().replace("-", "");
        String instrument = UUID.randomUUID().toString().replace("-", "");
        String aggregatorInstance = UUID.randomUUID().toString().replace("-", "");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());

        IdentifierValidator.Builder builder = createIdentifierValidator(containerId, accountId, cardId, instrument,
                customerId, aggregatorInstance, balanceTypeList, limitTypeList, null, null, "682019");

        IdentifierValidator validator = builder.build();
        ValidationResponse validationResponse = customerService.validateCustomerService(validator).await().indefinitely();


        assertAll(
                () -> assertEquals(ServiceResponse.ADDRESS_VERIFICATION.getServiceResponse(), validationResponse.getValidationResponse(0)),
                () -> assertEquals(ServiceID.CUSTOMER_VALIDATOR, validationResponse.getServiceId()),
                () -> assertEquals(1, validationResponse.getServiceResponseFieldsCount()),
                () -> assertEquals(AddressVerificationResults.POSTAL_CODE_MATCH_ADDRESS_NO_MATCH.getAddressVerificationResult(), validationResponse.getServiceResponseFieldsMap().get(FieldID.ADDRESS_VERIFICATION.getFieldId()))
        );

    }

    @Test
    void validateCustomerServiceNoNameNoAddressInvalidPostalCode() {

        String customerId = setupCustomer("test   name  1", "123 testAddress 456", "672019");
        String containerId = UUID.randomUUID().toString().replace("-", "");
        String accountId = UUID.randomUUID().toString().replace("-", "");
        String cardId = UUID.randomUUID().toString().replace("-", "");
        String instrument = UUID.randomUUID().toString().replace("-", "");
        String aggregatorInstance = UUID.randomUUID().toString().replace("-", "");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());

        IdentifierValidator.Builder builder = createIdentifierValidator(containerId, accountId, cardId, instrument,
                customerId, aggregatorInstance, balanceTypeList, limitTypeList, null, null, "682019");

        IdentifierValidator validator = builder.build();
        ValidationResponse validationResponse = customerService.validateCustomerService(validator).await().indefinitely();


        assertAll(
                () -> assertEquals(ServiceResponse.ADDRESS_VERIFICATION.getServiceResponse(), validationResponse.getValidationResponse(0)),
                () -> assertEquals(1, validationResponse.getValidationResponseCount()),
                () -> assertEquals(ServiceID.CUSTOMER_VALIDATOR, validationResponse.getServiceId()),
                () -> assertEquals(1, validationResponse.getServiceResponseFieldsCount()),
                () -> assertEquals(AddressVerificationResults.NO_MATCH.getAddressVerificationResult(),
                        validationResponse.getServiceResponseFieldsMap().get(FieldID.ADDRESS_VERIFICATION.getFieldId()))
        );

    }

    @Test
    void validateCustomerServiceNoNameNoAddressNoPostalCode() {

        String customerId = setupCustomer("test   name  1", "123 testAddress 456", "672019");
        String containerId = UUID.randomUUID().toString().replace("-", "");
        String accountId = UUID.randomUUID().toString().replace("-", "");
        String cardId = UUID.randomUUID().toString().replace("-", "");
        String instrument = UUID.randomUUID().toString().replace("-", "");
        String aggregatorInstance = UUID.randomUUID().toString().replace("-", "");

        List<String> limitTypeList = new ArrayList<>();
        limitTypeList.add(LimitType.NO_SPECIFIC.getLimitType());
        limitTypeList.add(LimitType.RETAIL.getLimitType());
        limitTypeList.add(LimitType.OTC.getLimitType());

        List<String> balanceTypeList = new ArrayList<>();
        balanceTypeList.add(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        balanceTypeList.add(BalanceTypes.CASH_BALANCE.getBalanceTypes());

        IdentifierValidator.Builder builder = createIdentifierValidator(containerId, accountId, cardId, instrument,
                customerId, aggregatorInstance, balanceTypeList, limitTypeList, null, null, null);

        IdentifierValidator validator = builder.build();
        ValidationResponse validationResponse = customerService.validateCustomerService(validator).await().indefinitely();

        assertNull(validationResponse);

//        assertAll(
//                () -> assertEquals(ServiceResponse.OK.getServiceResponse(), validationResponse.getValidationResponse(0)),
//                () -> assertEquals(1, validationResponse.getValidationResponseCount()),
//                () -> assertEquals(ServiceID.CUSTOMER_VALIDATOR, validationResponse.getServiceId()),
//                () -> assertEquals(1, validationResponse.getServiceResponseFieldsCount()),
//                () -> assertEquals(AddressVerificationResults.ADDRESS_INFORMATION_NOT_VERIFIED.getAddressVerificationResult(), validationResponse.getServiceResponseFieldsMap().get(FieldID.ADDRESS_VERIFICATION.getFieldId()))
//
//        );

    }

    IdentifierValidator.Builder createIdentifierValidator(String containerId, String accountId,
                                                          String cardId, String instrument,
                                                          String customerId, String aggregatorInstance, List<String> balanceTypesList, List<String> limitTypeList,
                                                          String name, String address, String postalCode) {

        IdentifierValidator.Builder builder = IdentifierValidator.newBuilder()
                .setMessageId(UUID.randomUUID().toString().replace("-", ""))
                .setMessageTypeId("0100")
                .setContainerId(containerId)
                .setChannelId(UUID.randomUUID().toString().replace("-", ""))
                .setAggregatorInstance(aggregatorInstance)
                .setCardNumber(cardId)
                .setAccountNumber(accountId)
                .setInstrument(instrument)
                .setCustomerNumber(customerId)
                .setAvsType(AVSType.AVS_NOT_PRESENT.getAvsType())
                .setTransactionAmount(1000L)
                .setBillingAmount(2000L)
                .setTransactionCurrencyCode("840")
                .setBillingCurrencyCode("124")
                .setTransactionType(TransactionType.CASH.getTransactionType())
                .setCountryCode("USA")
                .setAuthorizationType(AuthorizationType.AUTH.getAuthorizationType())
                .setInstallmentType(InstallmentType.NO_INSTALLMENT_TYPE.getInstallmentType())
                .setCashBack(CashBack.NO_CASH_BACK.getCashBack())
                .setCashBackAmount(0)
                .setCardAcceptorId("123456789012")
                .setCardAcceptorTerminalID("123456790123456")
                .setMerchantName("Best Buy")
                .setMerchantCountry("124")
                .setMerchantState("Ottwa")
                .setMerchantCity("Ottawa")
                .setAcquirerId("123456789")
                .setAcquirerCountry("124")
                .setMerchantNumber("1234567")
                .setTerminalType(TerminalType.ATM.getTerminalType())
                .setRecurringTrans(RecurringTrans.NOT_RECURRING_TRANS.getRecurringTrans())
                .setPurchaseTypes(PurchaseTypes.OTHERS.getPurchaseTypes())
                .setEntryMode(EntryMode.ICC.getEntryMode())
                .setExpiryDate("20210330")
                .setInternational(International.INTERNATIONAL.getInternational())
                .addAllBalanceTypes(balanceTypesList)
                .addAllLimitTypes(limitTypeList);

        if (name != null) {
            builder.setCustomerName(name);
        }
        if (address != null) {
            builder.setAddress(address);
        }

        if (postalCode != null) {
            builder.setPostalCode(postalCode);
        }
        return builder;
    }



    private String setupCustomer(String name, String address, String postalCode) {
        CustomerDef customerDef = CustomerDef.builder()
                .customerId(UUID.randomUUID().toString())
                .addressType(AddressType.HOME)
                .customerType(CustomerType.OWNER)
                .customerName(name)
                .addressLine(address)
                .postalCode(postalCode)
                .state("Kerala")
                .countryCode("356")
                .primaryEmail("testemail@test.com")
                .primaryPhoneNumber("+91 9543345455")
                .build();

        customerDef.persist().await().indefinitely();
        return customerDef.getCustomerId();

    }

}
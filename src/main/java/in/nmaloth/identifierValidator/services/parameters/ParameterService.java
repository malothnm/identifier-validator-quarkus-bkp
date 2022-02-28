package in.nmaloth.identifierValidator.services.parameters;

import in.nmaloth.identifierValidator.model.ProductCriteria;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.account.BalanceTypes;
import in.nmaloth.payments.constants.card.LimitType;

import java.util.List;
import java.util.Optional;

public interface ParameterService {

    void loadAllAuthProductCriteria();

    Optional<ServiceResponse> blockingCountryCode(ProductCriteria productCriteria, String countryCode);
    Optional<ServiceResponse> blockingCurrencyCode(ProductCriteria productCriteria, String currencyCode);
    Optional<ServiceResponse> blockingMCC(ProductCriteria productCriteria, Integer mcc );
    Optional<ServiceResponse> blockingPurchaseTypes(ProductCriteria productCriteria, PurchaseTypes purchaseTypes, International international);
    Optional<ServiceResponse> blockingLimitTypes(ProductCriteria productCriteria, LimitType[] limitTypes, International international);
    Optional<ServiceResponse> blockingBalanceTypes(ProductCriteria productCriteria, BalanceTypes[] balanceTypes, International international);
    Optional<ServiceResponse> blockingTransactionTypes(ProductCriteria productCriteria, TransactionType transactionType, International international);
    Optional<ServiceResponse> blockingTerminalTypes(ProductCriteria productCriteria, TerminalType terminalType, International international);
    Optional<ServiceResponse> blockInternational(ProductCriteria productCriteria, International international);
    Optional<ServiceResponse> blockCashBack(ProductCriteria productCriteria, CashBack cashBack);
    Optional<ServiceResponse> blockInstallment(ProductCriteria productCriteria, InstallmentType installmentType);

    List<String> validateParameters(IdentifierValidator identifierValidator, ProductCriteria productCriteria);

    Optional<ProductCriteria> findCriteriaRecord(IdentifierValidator identifierValidator);

    Optional<ProductCriteria> findCriteriaRecord(Integer org, Integer product, Integer criteria);



}

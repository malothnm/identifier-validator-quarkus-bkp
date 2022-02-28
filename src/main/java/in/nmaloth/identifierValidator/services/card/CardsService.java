package in.nmaloth.identifierValidator.services.card;

import in.nmaloth.identifierValidator.model.ProductCriteria;
import in.nmaloth.identifierValidator.model.entity.card.CardsBasic;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.card.CardStatus;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CardsService {

    Optional<ServiceResponse> validateCardStatus(CardStatus cardStatus);

    Optional<ServiceResponse> validateCardBlock(BlockType blockType);

    Optional<ServiceResponse> checkForBlockTransactionType(String includeExclude, Map<String, String> blockTransactionType, String txnType, International international);

    Optional<ServiceResponse> checkForBlockTerminalType(String includeExclude, Map<String, String> blockTerminalType, String terminalType, International international);

    Optional<ServiceResponse> checkForBlockPurchaseType(String includeExclude, Map<String, String> blockPurchaseType, String purchaseType, International international);

    Optional<ServiceResponse> checkForBlockEntryMode(String includeExclude, Map<String, String> blockPurchaseType, String entryMode, International international );

    Optional<ServiceResponse> checkForBlockLimitType(String includeExclude, Map<String, String> blockPurchaseType, List<String> limitType, International international );

    Optional<ServiceResponse> checkForBlockCashBack(Boolean blockCashBack, String cashBack );

    Optional<ServiceResponse> checkForInstallment(Boolean blockInstallment, String installment);

    Optional<ServiceResponse> checkForInternational(Boolean blockInternational, International international);


    Uni<Optional<ServiceResponse>> validateCardLimits(CardsBasic cardsBasic, IdentifierValidator identifierValidator, ProductCriteria productCriteria, long convertAmount);

    Uni<ValidationResponse> validateCards(IdentifierValidator identifierValidator, ProductCriteria productCriteria, long convertAmount);

}

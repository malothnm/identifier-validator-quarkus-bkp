package in.nmaloth.identifierValidator.services.account;

import in.nmaloth.identifierValidator.model.AccountResponse;
import in.nmaloth.identifierValidator.model.ProductCriteria;
import in.nmaloth.identifierValidator.model.entity.account.AccountBalances;
import in.nmaloth.identifierValidator.model.entity.account.AccountInfo;
import in.nmaloth.identifierValidator.model.entity.card.CacheTempAccum;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponse;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.payments.constants.BlockType;
import in.nmaloth.payments.constants.ServiceResponse;
import in.nmaloth.payments.constants.account.BalanceTypes;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AccountServices {

    Optional<ServiceResponse> validateAccountBlock(BlockType blockType);

    AccountResponse validateLimit(Map<String,Long> limitMap, Map<String, AccountBalances> balancesMap,
                                  Map<String, Long> txnBalanceMap, List<String> responseList,String accountType);

    Uni<AccountResponse> validateAccount(IdentifierValidator identifierValidator, AccountInfo accountInfo, long convertAmount);

    Uni<ValidationResponse> validateAccount(IdentifierValidator identifierValidator, long convertAmount);

}

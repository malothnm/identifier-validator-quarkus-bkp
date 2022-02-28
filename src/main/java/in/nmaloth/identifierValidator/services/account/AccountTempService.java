package in.nmaloth.identifierValidator.services.account;

import in.nmaloth.identifierValidator.model.entity.temp.AccountTempBalance;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.Map;

public interface AccountTempService {

    Uni<List<AccountTempBalance>> getAllAccountTempBalance(String cardNumber);
    Uni<Map<String, Long>> getAllAccountTempBalance(IdentifierValidator identifierValidator, long convertedAmount);


    Uni<AccountTempBalance> updateAccountTempBalance(AccountTempBalance accountTempBalance);

    AccountTempBalance createNewAccountTempBalance(IdentifierValidator identifierValidator, long convertedAmount);

}

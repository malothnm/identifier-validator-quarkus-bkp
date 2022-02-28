package in.nmaloth.identifierValidator.services.card;

import in.nmaloth.identifierValidator.model.entity.card.CacheTempAccum;
import in.nmaloth.identifierValidator.model.entity.temp.CardTempBalance;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.Map;

public interface CardTempService {

    Uni<List<CardTempBalance>> getAllCardTempBalance(String cardNumber);
    Uni<Map<String, CacheTempAccum>> getAllCardTempBalance(IdentifierValidator identifierValidator,long convertedAmount);


    Uni<CardTempBalance> updateCardTempBalance(CardTempBalance cardTempBalance);

    CardTempBalance createNewCardTempBalance( IdentifierValidator identifierValidator,long convertedAmount);

}

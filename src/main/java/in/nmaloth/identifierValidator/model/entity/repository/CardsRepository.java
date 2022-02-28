//package in.nmaloth.identifierValidator.model.entity.repository;
//
//import in.nmaloth.identifierValidator.model.entity.card.CardsBasic;
//import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
//import io.smallrye.mutiny.Uni;
//
//import javax.enterprise.context.ApplicationScoped;
//import java.util.Optional;
//
//@ApplicationScoped
//public class CardsRepository implements ReactivePanacheMongoRepository<CardsBasic> {
//
//    public Uni<Optional<CardsBasic>> findByCardId(String cardId){
//
//        return find("card_id",cardId).firstResultOptional();
//    }
//}

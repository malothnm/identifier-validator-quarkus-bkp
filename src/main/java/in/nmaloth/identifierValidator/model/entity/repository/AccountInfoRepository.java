//package in.nmaloth.identifierValidator.model.entity.repository;
//
//
//import in.nmaloth.identifierValidator.model.entity.account.AccountInfo;
//import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
//import io.smallrye.mutiny.Uni;
//
//import javax.enterprise.context.ApplicationScoped;
//import java.util.Optional;
//
//@ApplicationScoped
//public class AccountInfoRepository implements ReactivePanacheMongoRepository<AccountInfo> {
//
//    public Uni<Optional<AccountInfo>> findByAccountId(String accountId){
//        return find("account_id",accountId).firstResultOptional();
//    }
//
//
//}

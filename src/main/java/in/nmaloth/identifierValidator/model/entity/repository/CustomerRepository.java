//package in.nmaloth.identifierValidator.model.entity.repository;
//
//import in.nmaloth.identifierValidator.model.entity.customer.CustomerDef;
//import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
//import io.smallrye.mutiny.Uni;
//
//import javax.enterprise.context.ApplicationScoped;
//import java.util.Optional;
//
//@ApplicationScoped
//public class CustomerRepository implements ReactivePanacheMongoRepository<CustomerDef> {
//
//    public Uni<Optional<CustomerDef>> findByCustomerId(String customerId){
//        return find("customer_id",customerId).firstResultOptional();
//    }
//
//}

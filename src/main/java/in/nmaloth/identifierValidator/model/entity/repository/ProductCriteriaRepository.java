//package in.nmaloth.identifierValidator.model.entity.repository;
//
//import in.nmaloth.identifierValidator.model.entity.product.ProductAuthCriteria;
//import in.nmaloth.identifierValidator.model.entity.product.ProductAuthCriteriaKey;
//import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
//import io.smallrye.mutiny.Uni;
//
//import javax.enterprise.context.ApplicationScoped;
//import java.util.Optional;
//
//@ApplicationScoped
//public class ProductCriteriaRepository implements ReactivePanacheMongoRepository<ProductAuthCriteria> {
//
//    public Uni<Optional<ProductAuthCriteria>> findByProductId(Integer org, Integer product, Integer criteria){
//        return find("org= ?1 and product= ?2 and criteria= ?3",org,product,criteria).firstResultOptional();
//    }
//}

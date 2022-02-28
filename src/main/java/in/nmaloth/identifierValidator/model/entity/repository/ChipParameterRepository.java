//package in.nmaloth.identifierValidator.model.entity.repository;
//
//
//import in.nmaloth.identifierValidator.model.entity.global.ChipParameters;
//import in.nmaloth.payments.constants.Strategy;
//import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
//import io.smallrye.mutiny.Uni;
//
//import javax.enterprise.context.ApplicationScoped;
//import java.util.Optional;
//
//@ApplicationScoped
//public class ChipParameterRepository implements ReactivePanacheMongoRepository<ChipParameters> {
//
//    public Uni<Optional<ChipParameters>> findBYChipVersionAndStrategy(String chipVersion, Strategy strategy){
//        return find("chip_version =?1 && strategy = ?2", chipVersion,strategy.getStrategy()).firstResultOptional();
//    }
//    public Uni<Long> deleteByChipVersionAndStrategy(String chipVersion, Strategy strategy){
//        return delete("chip_version =?1 && strategy = ?2", chipVersion,strategy.getStrategy());
//    }
//
//}

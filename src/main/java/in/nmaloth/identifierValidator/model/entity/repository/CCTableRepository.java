//package in.nmaloth.identifierValidator.model.entity.repository;
//
//
//import in.nmaloth.identifierValidator.model.entity.global.CurrencyConversionTable;
//import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
//import io.smallrye.mutiny.Multi;
//import io.smallrye.mutiny.Uni;
//
//import javax.enterprise.context.ApplicationScoped;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@ApplicationScoped
//public class CCTableRepository implements ReactivePanacheMongoRepository<CurrencyConversionTable> {
//
//    public Uni<List<CurrencyConversionTable>> findAllCurrencyConversionTables(String network,LocalDate conversionDate){
//
//        return find("network = ?1 && conversion_date = ?2",network,conversionDate).list();
//
//    }
//
//    public Uni<List<CurrencyConversionTable>> findAllCurrencyConversionTables(String network, LocalDateTime conversionDateTime){
//        return find("network = ?1 && conversion_date_time = ?2", network,conversionDateTime).list();
//    }
//}

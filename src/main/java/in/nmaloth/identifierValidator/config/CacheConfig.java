package in.nmaloth.identifierValidator.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponseSummary;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.time.Duration;

public class CacheConfig {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    private Cache<String, ValidationResponseSummary> cacheAggregator;



    public void startup(@Observes StartupEvent startupEvent){

        this.cacheAggregator = Caffeine.newBuilder()
                .evictionListener((o, o2, removalCause) -> {

                    ValidationResponseSummary validationResponseSummary = (ValidationResponseSummary)o2;
                    logger.info("Aggregator has not processed message for {}", validationResponseSummary.getMessageId());

                })
                .expireAfterWrite(Duration.ofMillis(500))
                .build();
    }

    public  Cache<String,  ValidationResponseSummary> getAggregatorCache(){
        return this.cacheAggregator;
    }


}

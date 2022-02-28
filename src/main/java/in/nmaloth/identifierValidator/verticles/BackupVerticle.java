package in.nmaloth.identifierValidator.verticles;

import in.nmaloth.identifierValidator.config.EventBusNames;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponseSummary;
import io.quarkus.infinispan.client.Remote;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.infinispan.client.hotrod.RemoteCache;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class BackupVerticle extends AbstractVerticle {


    @Inject
    @Remote("backupIdentifier")
    RemoteCache<String, byte[]> backupCache;


    @Override
    public Uni<Void> asyncStart() {

       return vertx.eventBus().<ValidationResponseSummary>consumer(EventBusNames.BACKUP_CACHE)
                .handler(validationResponseSummaryMessage -> {
                    ValidationResponseSummary validationResponseSummary = validationResponseSummaryMessage.body();
                    backupCache.putAsync(validationResponseSummary.getMessageId(),validationResponseSummary.toByteArray());
                }).completionHandler();
    }
}

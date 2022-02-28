package in.nmaloth.identifierValidator.verticles;

import in.nmaloth.identifierValidator.config.EventBusNames;
import in.nmaloth.identifierValidator.model.proto.aggregator.ValidationResponseSummary;
import in.nmaloth.identifierValidator.model.proto.identifier.IdentifierValidator;
import in.nmaloth.identifierValidator.serviceEvents.model.ServiceAction;
import in.nmaloth.identifierValidator.serviceEvents.model.ServiceEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.LocalEventBusCodec;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.impl.Deployment;
import io.vertx.mutiny.core.Vertx;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class DeployVerticle {

    private static final Logger logger = LoggerFactory.getLogger(DeployVerticle.class);


    @ConfigProperty(name = "processor.instances")
    Optional<Integer> processorsVerticleDeployInstances;

    @ConfigProperty(name = "backup.instances")
    Optional<Integer> backUpVerticleDeployInstances;

    @Inject
    Vertx vertx;

    @Inject
    io.vertx.core.Vertx vertx1;


    public void startup(@Observes StartupEvent startupEvent,
                        Instance<MessageProcessorVerticle> messageProcessorVerticles,
                        Instance<BackupVerticle> backupVerticles,
                        ServiceEventVerticle serviceEventVerticle

                        ){


        vertx1.eventBus().registerDefaultCodec(IdentifierValidator.class,new LocalEventBusCodec<IdentifierValidator>());
        vertx1.eventBus().registerDefaultCodec(ValidationResponseSummary.class,new LocalEventBusCodec<ValidationResponseSummary>());
        vertx1.eventBus().registerDefaultCodec(ServiceEvent.class,new LocalEventBusCodec<ServiceEvent>());


        vertx.deployVerticle(messageProcessorVerticles::get,new DeploymentOptions().setInstances(processorsVerticleDeployInstances.orElse(4)))
                .onFailure().invoke(throwable -> {
                    throwable.printStackTrace();
                    throw new RuntimeException(throwable.getMessage());
                })
                .await().indefinitely()
        ;
        logger.info("############### deployed processor Verticle");



        vertx.deployVerticle(backupVerticles::get,new DeploymentOptions().setInstances(backUpVerticleDeployInstances.orElse(1)))
                .onFailure().invoke(throwable -> {
                    throwable.printStackTrace();
                    throw new RuntimeException(throwable.getMessage());
                })
                .await().indefinitely()
        ;
        logger.info("############### deployed backup Verticle");


        vertx.deployVerticle(serviceEventVerticle)
                .onFailure().invoke(throwable -> {
                    throwable.printStackTrace();
                    throw new RuntimeException(throwable.getMessage());
                })
                .await().indefinitely()
        ;
        logger.info(" ########## deployed  Service Event Verticle");



        vertx.eventBus().send(EventBusNames.SERVICE_EVENTS,
                ServiceEvent.builder().serviceAction(ServiceAction.SERVICE_DISCOVERY_ALL).build());

    }

}

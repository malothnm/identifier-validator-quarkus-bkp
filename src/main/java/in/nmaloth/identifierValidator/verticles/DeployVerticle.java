package in.nmaloth.identifierValidator.verticles;

import in.nmaloth.identifierValidator.config.EventBusNames;
import in.nmaloth.identifierValidator.model.IdentifierAmount;
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

    @ConfigProperty(name = "cards.instances")
    Optional<Integer> cardsVerticleDeployInstances;

    @ConfigProperty(name = "accounts.instances")
    Optional<Integer> accountVerticleDeployInstances;


    @ConfigProperty(name = "customer.instances")
    Optional<Integer> customerVerticleDeployInstances;



    @ConfigProperty(name = "backup.instances")
    Optional<Integer> backUpVerticleDeployInstances;

    @Inject
    Vertx vertx;

    @Inject
    io.vertx.core.Vertx vertx1;


    public void startup(@Observes StartupEvent startupEvent,
                        Instance<MessageProcessorVerticle> messageProcessorVerticles,
                        Instance<BackupVerticle> backupVerticles,
                        Instance<CardVerticle> cardVerticles,
                        Instance<CustomerVerticle> customerVerticles,
                        Instance<AccountVerticle> accountVerticles,
                        ServiceEventVerticle serviceEventVerticle

                        ){


        vertx1.eventBus().registerDefaultCodec(IdentifierValidator.class, new LocalEventBusCodec<>());
        vertx1.eventBus().registerDefaultCodec(ValidationResponseSummary.class, new LocalEventBusCodec<>());
        vertx1.eventBus().registerDefaultCodec(ServiceEvent.class, new LocalEventBusCodec<>());
        vertx1.eventBus().registerDefaultCodec(IdentifierAmount.class,new LocalEventBusCodec<>());



        vertx.deployVerticle(messageProcessorVerticles::get,new DeploymentOptions().setInstances(processorsVerticleDeployInstances.orElse(2)))
                .onFailure().invoke(throwable -> {
                    throwable.printStackTrace();
                    throw new RuntimeException(throwable.getMessage());
                })
                .await().indefinitely()
        ;
        logger.info("############### deployed processor Verticle");



        vertx.deployVerticle(cardVerticles::get,new DeploymentOptions().setInstances(cardsVerticleDeployInstances.orElse(2)))
                .onFailure().invoke(throwable -> {
                    throwable.printStackTrace();
                    throw new RuntimeException(throwable.getMessage());
                })
                .await().indefinitely()
        ;
        logger.info("############### deployed Cards Verticle");


        vertx.deployVerticle(accountVerticles::get,new DeploymentOptions().setInstances(accountVerticleDeployInstances.orElse(2)))
                .onFailure().invoke(throwable -> {
                    throwable.printStackTrace();
                    throw new RuntimeException(throwable.getMessage());
                })
                .await().indefinitely()
        ;
        logger.info("############### deployed Account Verticle");

        vertx.deployVerticle(customerVerticles::get,new DeploymentOptions().setInstances(customerVerticleDeployInstances.orElse(2)))
                .onFailure().invoke(throwable -> {
                    throwable.printStackTrace();
                    throw new RuntimeException(throwable.getMessage());
                })
                .await().indefinitely()
        ;
        logger.info("############### deployed Customer Verticle");




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

package in.nmaloth.identifierValidator.verticles;

import in.nmaloth.identifierValidator.config.EventBusNames;
import in.nmaloth.identifierValidator.config.RequestNames;
import in.nmaloth.identifierValidator.serviceEvents.model.Connections;
import in.nmaloth.identifierValidator.serviceEvents.model.ServiceAction;
import in.nmaloth.identifierValidator.serviceEvents.model.ServiceEvent;
import in.nmaloth.identifierValidator.serviceEvents.services.DependentServiceDiscovery;
import in.nmaloth.identifierValidator.services.grpc.client.MessageDistributorClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ServiceEventVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ServiceEventVerticle.class);

    @ConfigProperty(name = "quarkus.test.mode")
    public Optional<Boolean> testModeOptional;


    @ConfigProperty(name = "grpc.reattempts")
    public Optional<Integer> grpcReAttemptTimes;


    @ConfigProperty(name = "service.discovery.retry")
    Optional<Integer> serviceDiscoveryRetry;

    @ConfigProperty(name = "service.discovery.server-present")
    Optional<Boolean> serverPresent;


    @Inject
    MessageDistributorClient messageDistributorClient;

    @Inject
    DependentServiceDiscovery dependentServiceDiscovery;

    private final List<Connections> connectionsList = new ArrayList<>();

    @Override
    public Uni<Void> asyncStart() {
        return vertx.eventBus().<ServiceEvent>consumer(EventBusNames.SERVICE_EVENTS)
                .handler(serviceEventMessage -> {
                    processServiceEvents(serviceEventMessage.body());

                }).completionHandler()
                ;
    }

    private void processServiceEvents(ServiceEvent serviceEvent) {

        logger.info(" ####Service Event {}", serviceEvent.toString());

        switch (serviceEvent.getServiceAction()) {

            case SETUP_CLIENT: {

                logger.info("##########Entered Setup Request Client ");

                newConnectionServiceEvent(serviceEvent);
                break;
            }
            case PROCESS_REQUEST: {
                logger.info("##########Entered Process Request Client ");

                messageDistributorClient.createDistributorStream(serviceEvent);
                break;
            }

            case REMOVE_CLIENT: {

                logger.info("##########Entered remove Client ");
                removeConnections(serviceEvent);
                break;
            }
            case SERVICE_DISCOVERY: {
                logger.info("##########Entered ServiceDiscovery Client ");

                serviceDiscovery(serviceEvent);
                break;
            }

            case SERVICE_DISCOVERY_ALL: {
                logger.info("############ Entered Service Discovery All");
                serviceDiscoveryAll();

            }
            default: {

                break;
            }
        }
    }

    private void serviceDiscovery(ServiceEvent serviceEvent) {

        dependentServiceDiscovery.discoverServices(serviceEvent.getServiceName())
                .onItem().invoke(serverInfoList -> {
                    if (serverInfoList.size() == 0) {
                        vertx.executeBlocking(Uni.createFrom().<ServiceEvent>emitter(uniEmitter -> {

                            try {
                                Thread.sleep(serviceDiscoveryRetry.orElse(10000));
                                uniEmitter.complete(serviceEvent);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        })).subscribe().with(serviceEvent1 -> vertx.eventBus().send(EventBusNames.SERVICE_EVENTS, serviceEvent1));
                    }
                }).onItem().transformToMulti(serverInfoList -> Multi.createFrom().iterable(serverInfoList))
                .onItem().transform(serverInfo -> ServiceEvent.builder()
                        .serviceAction(ServiceAction.SETUP_CLIENT)
                        .requestName(RequestNames.DISTRIBUTOR)
                        .host(serverInfo.getHost())
                        .port(serverInfo.getPort())
                        .serviceName(serverInfo.getServiceName())
                        .instance(serverInfo.getServiceInstance())
                        .attempts(0)
                        .build()

                )
                .onFailure().invoke(throwable -> throwable.printStackTrace())
                .subscribe().with(serviceEvent1 -> vertx.eventBus().send(EventBusNames.SERVICE_EVENTS, serviceEvent1));
    }

    private void removeConnections(ServiceEvent serviceEvent) {

        Optional<Connections> connectionsOptional = connectionsList.stream()
                .filter(connections -> connections.getServiceName().equalsIgnoreCase(serviceEvent.getServiceName()))
                .filter(connections -> connections.getInstance().equalsIgnoreCase(serviceEvent.getInstance()))
                .findFirst();

        connectionsList.forEach(connections -> logger.info("###########Connection {}", connections.toString()));

        if (connectionsOptional.isEmpty()) {
            logger.error(" ################## Invalid Service Event {}", serviceEvent.toString());
            return;
        }
        Connections connection = connectionsOptional.get();
        connectionsList.remove(connection);
        logger.info(" Are these Channels Shutdown {} or Terminated {}", connection.getManagedChannel().isShutdown(), connection.getManagedChannel().isTerminated());

        vertx.executeBlocking(Uni.createFrom().<ServiceEvent>emitter(uniEmitter -> {
            try {
                Thread.sleep(2000);

                logger.info(" Reconnecting ... {}",serviceEvent.toString());
                if (serviceEvent.getAttempts() < grpcReAttemptTimes.orElse(2)) {

                    uniEmitter.complete(ServiceEvent.builder()
                            .serviceName(connection.getServiceName())
                            .instance(connection.getInstance())
                            .attempts(serviceEvent.getAttempts() + 1)
                            .host(connection.getHost())
                            .port(connection.getPort())
                            .serviceAction(ServiceAction.SETUP_CLIENT)
                            .build());
                } else {

                    logger.info(" #############Invoking Service Discovery");
                    Thread.sleep(serviceDiscoveryRetry.orElse(10000));

                    ServiceEvent serviceEvent1 = ServiceEvent.builder()
                            .serviceAction(ServiceAction.SERVICE_DISCOVERY)
                            .serviceName(serviceEvent.getServiceName())
                            .build();
                    vertx.eventBus().send(EventBusNames.SERVICE_EVENTS,serviceEvent1);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })).subscribe().with(serviceEvent1 -> vertx.eventBus().send(EventBusNames.SERVICE_EVENTS, serviceEvent1))
        ;

    }


    private void serviceDiscoveryAll() {

        dependentServiceDiscovery.discoverAllDependentServices()
                .onItem().invoke(serverInfoList -> {

                            if (serverInfoList.size() == 0 && serverPresent.orElse(true)) {
                                vertx.executeBlocking(Uni.createFrom().<ServiceEvent>emitter(uniEmitter -> {

                                    try {
                                        Thread.sleep(serviceDiscoveryRetry.orElse(10000));
                                        ServiceEvent.builder().serviceAction(ServiceAction.SERVICE_DISCOVERY_ALL).build();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                })).subscribe().with(serviceEvent -> vertx.eventBus().send(EventBusNames.SERVICE_EVENTS, serviceEvent))
                                ;
                            }
                        }

                )
                .onFailure().invoke(throwable -> throwable.printStackTrace())
                .onItem().transformToMulti(serverInfoList -> Multi.createFrom().iterable(serverInfoList))
                .onItem().transform(serverInfo -> ServiceEvent.builder()
                        .serviceName(serverInfo.getServiceName())
                        .instance(serverInfo.getServiceInstance())
                        .host(serverInfo.getHost())
                        .port(serverInfo.getPort())
                        .attempts(0)
                        .serviceAction(ServiceAction.SETUP_CLIENT)
                        .requestName(RequestNames.DISTRIBUTOR)
                        .build()

                )
                .subscribe().with(serviceEvent -> vertx.eventBus().send(EventBusNames.SERVICE_EVENTS, serviceEvent));

    }

    private void newConnectionServiceEvent(ServiceEvent serviceEvent) {
        ManagedChannel channel = createNewChannel(serviceEvent);
        Connections connections = Connections.builder()
                .serviceName(serviceEvent.getServiceName())
                .host(serviceEvent.getHost())
                .port(serviceEvent.getPort())
                .instance(serviceEvent.getInstance())
                .managedChannel(channel)
                .build();

        connectionsList.add(connections);


        ServiceEvent serviceEvent1 = ServiceEvent.builder()
                .serviceAction(ServiceAction.PROCESS_REQUEST)
                .serviceAction(ServiceAction.PROCESS_REQUEST)
                .channel(channel)
                .requestName(serviceEvent.getRequestName())
                .serviceName(serviceEvent.getServiceName())
                .instance(serviceEvent.getInstance())
                .attempts(serviceEvent.getAttempts())
                .build();

        vertx.eventBus().send(EventBusNames.SERVICE_EVENTS, serviceEvent1);
    }

    private ManagedChannel createNewChannel(ServiceEvent serviceEvent) {
        ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forAddress(serviceEvent.getHost(), serviceEvent.getPort());

        if (testModeOptional.orElse(false)) {
            builder.usePlaintext();
        }
        return builder.build();
    }
}

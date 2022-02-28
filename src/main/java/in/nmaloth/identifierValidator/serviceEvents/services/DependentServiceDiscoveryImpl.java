package in.nmaloth.identifierValidator.serviceEvents.services;

import in.nmaloth.identifierValidator.serviceEvents.config.ServiceDiscoveryConfig;
import in.nmaloth.identifierValidator.serviceEvents.model.discovery.ServerInfo;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DependentServiceDiscoveryImpl implements DependentServiceDiscovery{

    private final static Logger logger = LoggerFactory.getLogger(DependentServiceDiscoveryImpl.class);

    @Inject
    ServiceDiscoveryConfig serviceDiscoveryConfig;


    @Override
    public Uni<List<ServerInfo>> discoverServices(String serviceName) {

        if(serviceDiscoveryConfig.method().equalsIgnoreCase("static")) {

            if(serviceDiscoveryConfig.services().isEmpty()){

                throw new RuntimeException("No Service Info mentioned for connections");
            }

            return Uni.createFrom().item(serviceDiscoveryConfig.services().get()
                    .stream().filter(service -> serviceName.equalsIgnoreCase(service.grpc()))
                    .flatMap(service -> processServiceInfo(service).stream())
                    .collect(Collectors.toList()))
                    ;


        }  else {throw new RuntimeException("Discovery Method " + serviceDiscoveryConfig.method() + " Not Supported");}
    }

    @Override
    public Uni<List<ServerInfo>> discoverAllDependentServices() {

        if(serviceDiscoveryConfig.method().equalsIgnoreCase("static")){

            if(serviceDiscoveryConfig.services().isEmpty()){

                throw new RuntimeException("No Service Info mentioned for connections");
            }


            return Uni.createFrom().item(serviceDiscoveryConfig.services().get()
                    .stream()
                    .flatMap(service -> processServiceInfo(service).stream())
                    .collect(Collectors.toList()))
                    ;



        } else {

            throw new RuntimeException("Discovery Method " + serviceDiscoveryConfig.method() + " Not Supported");
        }
    }

    private List<ServerInfo> processServiceInfo(ServiceDiscoveryConfig.Service service) {

        if(service.instances().isEmpty()){
            throw new RuntimeException(" Instance Information is required for static service discovery");
        }
        return service.instances().get()
                .stream().map(instance -> ServerInfo.builder()
                        .serviceName(service.grpc())
                        .serviceInstance(instance.instance())
                        .host(instance.host())
                        .port(instance.port())
                        .build()
                )
                .collect(Collectors.toList())

        ;
    }


}

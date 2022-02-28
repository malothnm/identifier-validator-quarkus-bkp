package in.nmaloth.identifierValidator.serviceEvents.config;

import io.smallrye.config.ConfigMapping;

import java.util.List;
import java.util.Optional;

@ConfigMapping(prefix = "discovery")
public interface ServiceDiscoveryConfig {

    String method();
    Optional<List<Service>> services();



    interface Service {

        String name();
        String grpc();
        Optional<List<Instance>> instances();


        interface Instance {
            String instance();
            String host();
            Integer port();
        }

    }

}

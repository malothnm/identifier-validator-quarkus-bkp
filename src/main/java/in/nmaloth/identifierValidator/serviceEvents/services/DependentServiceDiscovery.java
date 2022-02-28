package in.nmaloth.identifierValidator.serviceEvents.services;

import in.nmaloth.identifierValidator.serviceEvents.model.discovery.ServerInfo;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface DependentServiceDiscovery {

    Uni<List<ServerInfo>> discoverServices(String serviceName);
    Uni<List<ServerInfo>> discoverAllDependentServices();

}

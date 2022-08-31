package in.nmaloth.testResource;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

public class GRPCWireResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger logger = LoggerFactory.getLogger(GRPCWireResource.class);

    final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));


    ConnectionServer server1 = new ConnectionServer();

//    ConnectionServer server2 = new ConnectionServer();


    @Override
    public Map<String, String> start() {


        server1.start(7000);

//        server2.start(7001);

        mongoDBContainer.start();


        return Map.of(
                "discovery.method", "static",
                "discovery.services[0].name", "message-distributor",
                "discovery.services[0].grpc", "distributor",
                "discovery.services[0].instances[0].host", "localhost",
                "discovery.services[0].instances[0].port", "7000",
//                "discovery.services[0].instances[1].host", "localhost",
//                "discovery.services[0].instances[1].port", "7001",
                "quarkus.infinispan-client.server-list", "localhost:11222",
                "quarkus.infinispan-client.auth-username", "admin",
                "quarkus.infinispan-client.auth-password", "password",
                "quarkus.test.mode", "true",
                "quarkus.mongodb.connection-string", "mongodb://" + mongoDBContainer.getContainerIpAddress() + ":" + mongoDBContainer.getFirstMappedPort()

        );

    }

    @Override
    public void stop() {

        server1.stop();
//        server2.stop();
        mongoDBContainer.stop();

    }

    @Override
    public void inject(TestInjector testInjector) {

        testInjector.injectIntoFields(server1, new TestInjector.AnnotatedAndMatchesType(InjectConnectionServer.class, ConnectionServer.class));

    }
}

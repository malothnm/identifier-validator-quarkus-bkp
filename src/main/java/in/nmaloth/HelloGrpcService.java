package in.nmaloth;

import io.quarkus.grpc.GrpcService;

import io.smallrye.mutiny.Uni;

@GrpcService
public class HelloGrpcService implements in.nmaloth.HelloGrpc {

    @Override
    public Uni<in.nmaloth.HelloReply> sayHello(in.nmaloth.HelloRequest request) {
        return Uni.createFrom().item("Hello " + request.getName() + "!")
                .map(msg -> in.nmaloth.HelloReply.newBuilder().setMessage(msg).build());
    }

}

package scg.fusion.grpc;

import io.grpc.*;

import scg.fusion.ComponentFactory;
import scg.fusion.Environment;

import scg.fusion.annotation.Factory;

import scg.fusion.annotation.InitializeBy;
import scg.fusion.annotation.UtilizeBy;

import java.io.IOException;
import java.util.List;

import static io.grpc.netty.NettyServerBuilder.forPort;

import static scg.fusion.grpc.GrpcUtils.listServices;

public final class NettyGrpcUnit {

    public static final String GRPC_PORT_PROPERTY_NAME = "fusion.grpc.port";

    private static final class ServerLifecycleHook {

        private final Server server;

        ServerLifecycleHook(Server server) {
            this.server = server;
        }

        private void shutdown() {
            this.server.shutdown();
        }

        private void startup() throws IOException {
            this.server.start();
        }

    }

    @Factory
    @UtilizeBy("shutdown")
    @InitializeBy("startup")
    private static ServerLifecycleHook grpcServerLifecycle(ComponentFactory components, Environment environment) {

        int port = environment.getInt(GRPC_PORT_PROPERTY_NAME);

        List<ServerServiceDefinition> services = listServices(components);

        if (!services.isEmpty()) {
            return new ServerLifecycleHook(forPort(port).addServices(services).build());
        }

        throw new IllegalStateException("grpc endpoints undefined");

    }
}

package scg.fusion.grpc;

import io.grpc.BindableService;
import io.grpc.Context;
import io.grpc.ServerInterceptor;
import io.grpc.ServerServiceDefinition;
import scg.fusion.ComponentFactory;

import java.util.List;
import java.util.stream.Stream;

import static io.grpc.Context.current;
import static io.grpc.ServerInterceptors.intercept;
import static java.util.stream.Collectors.toList;

public final class GrpcUtils {

    private GrpcUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T> T lookup(Context.Key<T> key) {
        return key.get(current());
    }

    static List<ServerServiceDefinition> listServices(ComponentFactory factory) {

        Stream<ServerServiceDefinition> services = factory.streamAllSubtypes(BindableService.class).map(BindableService::bindService);

        if (factory.hasSubtypeComponents(ServerInterceptor.class)) {

            List<ServerInterceptor> interceptors = factory.streamAllSubtypes(ServerInterceptor.class).collect(toList());

            services = services.map(service -> intercept(service, interceptors));

        }

        return services.collect(toList());

    }

}

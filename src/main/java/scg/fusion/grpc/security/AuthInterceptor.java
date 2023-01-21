package scg.fusion.grpc.security;

import io.grpc.*;
import io.grpc.Metadata.Key;
import io.grpc.ServerCall.Listener;


import java.util.Objects;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;
import static io.grpc.Status.UNAUTHENTICATED;
import static java.util.Objects.nonNull;
import static scg.fusion.grpc.security.Authorization.UNAUTHORIZED;

public abstract class AuthInterceptor implements ServerInterceptor {

    public static final Context.Key<Authorization> AUTHORIZATION = Context.keyWithDefault("Authorization", UNAUTHORIZED);

    public static final Metadata.Key<String> AUTH_HEADER = Key.of("Authorization", ASCII_STRING_MARSHALLER);

    protected abstract Authorization authenticate(String header);

    @Override
    public final  <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {

        try {
            if (metadata.containsKey(AUTH_HEADER)) {

                Authorization authorization = authenticate(metadata.get(AUTH_HEADER));

                if (nonNull(authorization)) {

                    Context current = Context.current();

                    current = current.withValue(AUTHORIZATION, authorization);

                    return Contexts.interceptCall(current, serverCall, metadata, serverCallHandler);

                }
            }
        } catch (Throwable cause) {

            StatusRuntimeException status = new StatusRuntimeException(UNAUTHENTICATED, metadata);

            status.initCause(cause);

            throw status;

        }

        serverCall.close(UNAUTHENTICATED, metadata);

        return new Listener<ReqT>() {};

    }
}

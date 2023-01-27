package scg.fusion.grpc.security;

import io.grpc.Context;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;

import static scg.fusion.grpc.security.AuthInterceptor.AUTHORIZATION;

public interface Authorization<A extends GrantedAuthority, P extends Principal, C extends Credentials> {

    Authorization<GrantedAuthority, Principal, Credentials> UNAUTHORIZED = new Authorization<GrantedAuthority, Principal, Credentials>() {
        @Override
        public boolean isAuthenticated() {
            return false;
        }

        @Override
        public Principal getPrincipal() {
            throw new NoSuchElementException();
        }

        @Override
        public Credentials getCredentials() {
            throw new NoSuchElementException();
        }

        @Override
        public Set<GrantedAuthority> getAuthorities() {
            return Collections.emptySet();
        }
    };

    boolean isAuthenticated();

    P getPrincipal();

    C getCredentials();

    Set<A> getAuthorities();

    default void attach() {
        Context.current().withValue(AUTHORIZATION, this).attach();
    }

    @Nullable
    static <G extends GrantedAuthority, P extends Principal, C extends Credentials, A extends Authorization<G, P, C>> A lookup(Class<? extends A> authorizationType) {

        Object authorization = AUTHORIZATION.get();

        return authorizationType.isInstance(authorization) ? authorizationType.cast(authorization) : (null);

    }

}

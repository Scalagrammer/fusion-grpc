package scg.fusion.grpc.security;

import java.util.Collections;
import java.util.Set;

public interface Authorization {

    Authorization UNAUTHORIZED = new Authorization() {
        @Override
        public boolean isAuthenticated() {
            return false;
        }

        @Override
        public <T> T getPrincipal() {
            return null;
        }

        @Override
        public <T> T getCredentials() {
            return null;
        }

        @Override
        public <T> Set<GrantedAuthority> getAuthorities() {
            return Collections.emptySet();
        }
    };

    boolean isAuthenticated();

    <T> T getPrincipal();

    <T> T getCredentials();

    <T> Set<GrantedAuthority> getAuthorities();

}

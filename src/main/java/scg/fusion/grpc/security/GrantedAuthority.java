package scg.fusion.grpc.security;

public interface GrantedAuthority {
    <T> T getAuthority();
}

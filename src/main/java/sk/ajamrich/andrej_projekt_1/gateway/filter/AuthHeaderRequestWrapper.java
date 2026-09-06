package sk.ajamrich.andrej_projekt_1.gateway.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;

public class AuthHeaderRequestWrapper extends HttpServletRequestWrapper {

    private final String authUser;
    private final String authRoles;

    public AuthHeaderRequestWrapper(HttpServletRequest request, String authUser, String authRoles) {
        super(request);
        this.authUser = authUser;
        this.authRoles = authRoles;
    }

    @Override
    public String getHeader(String name) {
        if ("X-Auth-User".equalsIgnoreCase(name)) return authUser;
        if ("X-Auth-Roles".equalsIgnoreCase(name)) return authRoles;
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if ("X-Auth-User".equalsIgnoreCase(name)) return Collections.enumeration(List.of(authUser));
        if ("X-Auth-Roles".equalsIgnoreCase(name)) return Collections.enumeration(List.of(authRoles));
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(
                Stream.concat(
                        Collections.list(super.getHeaderNames()).stream(),
                        Stream.of("X-Auth-User", "X-Auth-Roles")
                ).distinct().toList()
        );
    }
}
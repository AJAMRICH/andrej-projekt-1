package sk.ajamrich.andrej_projekt_1.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sk.ajamrich.andrej_projekt_1.gateway.auth.JwtService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/login",
            "/auth/users",
            "/actuator/health"
    );

    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jwt.header:Authorization}")
    private String headerName;

    @Value("${jwt.prefix:Bearer }")
    private String prefix;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(headerName);

        if (header == null || !header.startsWith(prefix)) {
            unauthorized(response, "Chyba Authorization hlavicka");
            return;
        }

        String token = header.substring(prefix.length());

        try {
            Claims claims = jwtService.validateAndParse(token);
            String username = claims.getSubject();
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);

            // Zabalime request tak, aby downstream (dalsi filter/controller/gateway route)
            // videl pridane hlavicky X-Auth-User a X-Auth-Roles
            HttpServletRequest wrappedRequest = new AuthHeaderRequestWrapper(
                    request, username, roles != null ? String.join(",", roles) : "");

            filterChain.doFilter(wrappedRequest, response);

        } catch (JwtException | IllegalArgumentException e) {
            unauthorized(response, "Neplatny alebo expirovany token");
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(Map.of("message", message)));
    }
}
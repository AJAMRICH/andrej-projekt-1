package sk.ajamrich.andrej_projekt_1.gateway.auth.dto;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInMs
) {}
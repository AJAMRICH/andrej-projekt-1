package sk.ajamrich.andrej_projekt_1.gateway.auth.dto;

import java.util.List;

public record CreateUserResponse(
        String username,
        List<String> roles
) {}
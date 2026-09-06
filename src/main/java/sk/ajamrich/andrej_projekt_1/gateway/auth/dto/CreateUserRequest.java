package sk.ajamrich.andrej_projekt_1.gateway.auth.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateUserRequest(
        @NotBlank String username,
        @NotBlank String password,
        List<String> roles
) {}
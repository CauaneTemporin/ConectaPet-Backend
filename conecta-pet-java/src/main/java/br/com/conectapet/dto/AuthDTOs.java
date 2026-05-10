package br.com.conectapet.dto;

import jakarta.validation.constraints.*;

public class AuthDTOs {

    public record RegisterRequest(
        @NotBlank(message = "Nome é obrigatório") String name,
        @Email(message = "E-mail inválido") @NotBlank(message = "E-mail é obrigatório") String email,
        @NotBlank(message = "Senha é obrigatória") @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres") String password,
        String city
    ) {}

    public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
    ) {}

    public record AuthResponse(String token, String type, UserDTOs.UserResponse user) {
        public AuthResponse(String token, UserDTOs.UserResponse user) {
            this(token, "Bearer", user);
        }
    }

    public record UpdateProfileRequest(String name, String city, String bio) {}
}

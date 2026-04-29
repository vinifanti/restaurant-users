package com.fiap.restaurant_users.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para validação de login")
public record LoginRequest(

        @Schema(description = "Login do usuário", example = "joao123")
        @NotBlank(message = "Login é obrigatório")
        String login,

        @Schema(description = "Senha do usuário", example = "senha123")
        @NotBlank(message = "Senha é obrigatória")
        String password
) {}

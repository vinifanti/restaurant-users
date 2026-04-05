package com.fiap.restaurant_users.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para troca de senha")
public record ChangePasswordRequest(

        @Schema(description = "Senha atual do usuário", example = "senha123")
        @NotBlank(message = "Senha atual é obrigatória")
        String currentPassword,

        @Schema(description = "Nova senha do usuário", example = "novaSenha456")
        @NotBlank(message = "Nova senha é obrigatória")
        @Size(min = 6, message = "Nova senha deve ter no mínimo 6 caracteres")
        String newPassword
) {}
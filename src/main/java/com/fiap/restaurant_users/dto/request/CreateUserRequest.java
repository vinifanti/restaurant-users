package com.fiap.restaurant_users.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para cadastro de usuário")
public record CreateUserRequest(

        @Schema(description = "Nome completo do usuário", example = "João Silva")
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @Schema(description = "E-mail único do usuário", example = "joao@email.com")
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @Schema(description = "Login de acesso", example = "joao123")
        @NotBlank(message = "Login é obrigatório")
        String login,

        @Schema(description = "Senha de acesso", example = "senha123")
        @NotBlank(message = "Senha é obrigatória")
        String password,

        @Schema(description = "Tipo do usuário", example = "CUSTOMER",
                allowableValues = {"CUSTOMER", "RESTAURANT_OWNER"})
        @NotBlank(message = "Tipo é obrigatório. Use CUSTOMER ou RESTAURANT_OWNER")
        String userType,

        @Schema(description = "Endereço do usuário")
        @NotNull(message = "Endereço é obrigatório")
        @Valid
        AddressRequest address
) {}
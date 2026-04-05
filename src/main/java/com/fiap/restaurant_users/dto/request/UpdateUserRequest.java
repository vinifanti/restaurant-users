package com.fiap.restaurant_users.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para atualização do usuário")
public record UpdateUserRequest(

        @Schema(description = "Nome completo do usuário", example = "João Silva Atualizado")
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @Schema(description = "E-mail único do usuário", example = "joao.atualizado@email.com")
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @Schema(description = "Login de acesso", example = "joao456")
        @NotBlank(message = "Login é obrigatório")
        String login,

        @Schema(description = "Endereço do usuário")
        @NotNull(message = "Endereço é obrigatório")
        @Valid
        AddressRequest address
) {}
package com.fiap.restaurant_users.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Endereço do usuário")
public record AddressRequest(

        @Schema(description = "Rua", example = "Rua das Flores")
        @NotBlank(message = "Rua é obrigatória")
        String street,

        @Schema(description = "Número", example = "123")
        @NotBlank(message = "Número é obrigatório")
        String number,

        @Schema(description = "Cidade", example = "São Paulo")
        @NotBlank(message = "Cidade é obrigatória")
        String city,

        @Schema(description = "CEP", example = "01310-100")
        @NotBlank(message = "CEP é obrigatório")
        String zipCode
) {}
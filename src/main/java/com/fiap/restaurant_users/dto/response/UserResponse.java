package com.fiap.restaurant_users.dto.response;

import com.fiap.restaurant_users.model.Address;
import com.fiap.restaurant_users.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Dados retornados do usuário")
public record UserResponse(

        @Schema(description = "ID do usuário", example = "1")
        Long id,

        @Schema(description = "Nome do usuário", example = "João Silva")
        String name,

        @Schema(description = "E-mail do usuário", example = "joao@email.com")
        String email,

        @Schema(description = "Login do usuário", example = "joao123")
        String login,

        @Schema(description = "Tipo do usuário", example = "Customer")
        String userType,

        @Schema(description = "Endereço do usuário")
        Address address,

        @Schema(description = "Data da última alteração", example = "2024-01-01T10:00:00")
        LocalDateTime lastModifiedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getClass().getSimpleName(),
                user.getAddress(),
                user.getLastModifiedAt()
        );
    }
}
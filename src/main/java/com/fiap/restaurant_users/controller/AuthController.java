package com.fiap.restaurant_users.controller;

import com.fiap.restaurant_users.dto.request.LoginRequest;
import com.fiap.restaurant_users.dto.response.AuthResponse;
import com.fiap.restaurant_users.dto.response.UserResponse;
import com.fiap.restaurant_users.usecase.AuthUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Operações de autenticação do sistema")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @Operation(summary = "Realizar login", description = "Autentica o usuário com login e senha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login válido"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Login ou senha inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(value = """
                {
                  "type": "https://api.restaurant.com/errors/invalid-request",
                  "title": "Requisição inválida",
                  "status": 400,
                  "detail": "Login ou senha inválidos",
                  "instance": "/api/v1/auth/login"
                }
            """)
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid LoginRequest request) {

        AuthResponse response = authUseCase.authenticate(request);
        return ResponseEntity.ok(response);
    }
}

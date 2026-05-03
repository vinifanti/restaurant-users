package com.fiap.restaurant_users.controller;

import com.fiap.restaurant_users.dto.request.ChangePasswordRequest;
import com.fiap.restaurant_users.dto.request.CreateUserRequest;
import com.fiap.restaurant_users.dto.request.LoginRequest;
import com.fiap.restaurant_users.dto.request.UpdateUserRequest;
import com.fiap.restaurant_users.dto.response.UserResponse;
import com.fiap.restaurant_users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ProblemDetail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Cadastrar usuário", description = "Cria um novo usuário do tipo CUSTOMER ou RESTAURANT_OWNER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "E-mail já cadastrado ou dados inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(value = """
                {
                  "type": "https://api.restaurant.com/errors/invalid-request",
                  "title": "Requisição inválida",
                  "status": 400,
                  "detail": "E-mail já cadastrado",
                  "instance": "/api/v1/users"
                }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação nos campos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(value = """
                {
                  "type": "https://api.restaurant.com/errors/validation",
                  "title": "Erro de validação",
                  "status": 422,
                  "detail": "email: E-mail inválido, name: Nome é obrigatório",
                  "instance": "/api/v1/users"
                }
            """)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<UserResponse> create(
            @RequestBody @Valid CreateUserRequest request) {
        UserResponse response = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um usuário pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(value = """
                {
                  "type": "https://api.restaurant.com/errors/not-found",
                  "title": "Recurso não encontrado",
                  "status": 404,
                  "detail": "Usuário com id 1 não encontrado",
                  "instance": "/api/v1/users/1"
                }
            """)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        UserResponse response = userService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar usuários por nome", description = "Retorna uma lista de usuários que contém o nome informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> findByName(
            @RequestParam String name) {
        List<UserResponse> response = userService.findByName(name);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar dados do usuário", description = "Atualiza nome, e-mail, login e endereço. Não altera a senha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "E-mail já cadastrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(value = """
                {
                  "type": "https://api.restaurant.com/errors/invalid-request",
                  "title": "Requisição inválida",
                  "status": 400,
                  "detail": "E-mail já cadastrado",
                  "instance": "/api/v1/users/1"
                }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(value = """
                {
                  "type": "https://api.restaurant.com/errors/not-found",
                  "title": "Recurso não encontrado",
                  "status": 404,
                  "detail": "Usuário com id 1 não encontrado",
                  "instance": "/api/v1/users/1"
                }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação nos campos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(value = """
                {
                  "type": "https://api.restaurant.com/errors/validation",
                  "title": "Erro de validação",
                  "status": 422,
                  "detail": "email: E-mail inválido, name: Nome é obrigatório",
                  "instance": "/api/v1/users/1"
                }
            """)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRequest request) {
        UserResponse response = userService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Trocar senha", description = "Endpoint exclusivo para troca de senha. Requer senha atual para confirmação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Senha atual incorreta",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(value = """
                {
                  "type": "https://api.restaurant.com/errors/invalid-request",
                  "title": "Requisição inválida",
                  "status": 400,
                  "detail": "Senha atual incorreta",
                  "instance": "/api/v1/users/1/password"
                }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(value = """
                {
                  "type": "https://api.restaurant.com/errors/not-found",
                  "title": "Recurso não encontrado",
                  "status": 404,
                  "detail": "Usuário com id 1 não encontrado",
                  "instance": "/api/v1/users/1/password"
                }
            """)
                    )
            )
    })
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Excluir usuário", description = "Remove um usuário do sistema pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(value = """
                {
                  "type": "https://api.restaurant.com/errors/not-found",
                  "title": "Recurso não encontrado",
                  "status": 404,
                  "detail": "Usuário com id 1 não encontrado",
                  "instance": "/api/v1/users/1"
                }
            """)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Validar login", description = "Verifica se o login e senha são válidos")
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
                  "instance": "/api/v1/users/login"
                }
            """)
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @RequestBody @Valid LoginRequest request) {
        UserResponse response = userService.validateLogin(request);
        return ResponseEntity.ok(response);
    }
}
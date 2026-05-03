package com.fiap.restaurant_users.service;

import com.fiap.restaurant_users.dto.request.AddressRequest;
import com.fiap.restaurant_users.dto.request.ChangePasswordRequest;
import com.fiap.restaurant_users.dto.request.CreateUserRequest;
import com.fiap.restaurant_users.dto.request.LoginRequest;
import com.fiap.restaurant_users.dto.request.UpdateUserRequest;
import com.fiap.restaurant_users.dto.response.UserResponse;
import com.fiap.restaurant_users.exception.ResourceNotFoundException;
import com.fiap.restaurant_users.model.Customer;
import com.fiap.restaurant_users.model.User;
import com.fiap.restaurant_users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private AddressRequest addressRequest;

    @BeforeEach
    void setUp() {
        addressRequest = new AddressRequest(
                "Rua das Flores", "123", "São Paulo", "01310-100"
        );

        user = new Customer();
        user.setName("João Silva");
        user.setEmail("joao@email.com");
        user.setLogin("joao123");
        user.setPassword("senha123");
        user.setLastModifiedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void deveCriarUsuarioComSucesso() {
        CreateUserRequest request = new CreateUserRequest(
                "João Silva", "joao@email.com", "joao123",
                "senha123", "CUSTOMER", addressRequest
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("João Silva");
        assertThat(response.email()).isEqualTo("joao@email.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar e-mail duplicado")
    void deveLancarExcecaoEmailDuplicado() {
        CreateUserRequest request = new CreateUserRequest(
                "João Silva", "joao@email.com", "joao123",
                "senha123", "CUSTOMER", addressRequest
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("E-mail já cadastrado");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com tipo inválido")
    void deveLancarExcecaoTipoInvalido() {
        CreateUserRequest request = new CreateUserRequest(
                "João Silva", "joao@email.com", "joao123",
                "senha123", "TIPO_INVALIDO", addressRequest
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tipo inválido. Use CUSTOMER ou RESTAURANT_OWNER");
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorIdComSucesso() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.findById(1L);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("joao@email.com");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ID inexistente")
    void deveLancarExcecaoIdInexistente() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário com id 999 não encontrado");
    }

    @Test
    @DisplayName("Deve buscar usuários por nome com sucesso")
    void deveBuscarUsuariosPorNome() {
        when(userRepository.findByNameContainingIgnoreCase("João"))
                .thenReturn(List.of(user));

        List<UserResponse> response = userService.findByName("João");

        assertThat(response).isNotEmpty();
        assertThat(response).hasSize(1);
        assertThat(response.getFirst().name()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar nome inexistente")
    void deveRetornarListaVaziaAoBuscarNomeInexistente() {
        when(userRepository.findByNameContainingIgnoreCase("Inexistente"))
                .thenReturn(List.of());

        List<UserResponse> response = userService.findByName("Inexistente");

        assertThat(response).isEmpty();
    }

    @Test
    @DisplayName("Deve trocar senha com sucesso")
    void deveTrocarSenhaComSucesso() {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "senha123", "novaSenha456"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha123", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("novaSenha456")).thenReturn("$2a$10$hashedNovaSenha");

        userService.changePassword(1L, request);

        assertThat(user.getPassword()).isEqualTo("$2a$10$hashedNovaSenha");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Deve lançar exceção ao trocar senha com senha atual incorreta")
    void deveLancarExcecaoSenhaAtualIncorreta() {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "senhaErrada", "novaSenha456"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senhaErrada", user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Senha atual incorreta");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve validar login com sucesso")
    void deveValidarLoginComSucesso() {
        LoginRequest request = new LoginRequest("joao123", "senha123");

        when(userRepository.findByLogin("joao123")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha123", user.getPassword())).thenReturn(true);

        UserResponse response = userService.validateLogin(request);

        assertThat(response).isNotNull();
        assertThat(response.login()).isEqualTo("joao123");
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar login inválido")
    void deveLancarExcecaoLoginInvalido() {
        LoginRequest request = new LoginRequest("joao123", "senhaErrada");

        when(userRepository.findByLogin("joao123")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senhaErrada", user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> userService.validateLogin(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Login ou senha inválidos");
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deveDeletarUsuarioComSucesso() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void deveAtualizarUsuarioComSucesso() {
        UpdateUserRequest request = new UpdateUserRequest(
                "João Silva Atualizado",
                "joao.atualizado@email.com",
                "joao456",
                addressRequest
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.update(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("João Silva Atualizado");
        assertThat(response.email()).isEqualTo("joao.atualizado@email.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário com e-mail duplicado")
    void deveLancarExcecaoAoAtualizarComEmailDuplicado() {
        UpdateUserRequest request = new UpdateUserRequest(
                "João Silva Atualizado",
                "joao.atualizado@email.com",
                "joao456",
                addressRequest
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> userService.update(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("E-mail já cadastrado");

        verify(userRepository, never()).save(any(User.class));
    }
}
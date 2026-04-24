package com.fiap.restaurant_users.service;

import com.fiap.restaurant_users.dto.request.LoginRequest;
import com.fiap.restaurant_users.dto.request.AddressRequest;
import com.fiap.restaurant_users.dto.response.UserResponse;
import com.fiap.restaurant_users.exception.InvalidCredentialsException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User user;
    private AddressRequest addressRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        addressRequest = new AddressRequest(
                "Rua das Flores", "123", "São Paulo", "01310-100"
        );
        user = new Customer();
        user.setName("João Silva");
        user.setEmail("joao@email.com");
        user.setLogin("joao123");
        user.setPassword("senhaCriptografada");
        user.setLastModifiedAt(LocalDateTime.now());

        loginRequest = new LoginRequest("joao123", "123");
    }

    @Test
    @DisplayName("Deve autenticar usuário com sucesso quando login e senha estão corretos")
    void deveAutenticarUsuarioComSucesso() {
        // Given
        given(userRepository.findByLogin("joao123"))
                .willReturn(Optional.of(user));

        given(passwordEncoder.matches("123", "senhaCriptografada"))
                .willReturn(true);

        // When
        UserResponse response = authService.authenticate(loginRequest);

        // Then
        assertNotNull(response);
        then(userRepository).should().findByLogin("joao123");
        then(passwordEncoder).should().matches("123", "senhaCriptografada");
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não for encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Given
        given(userRepository.findByLogin("joao123"))
                .willReturn(Optional.empty());

        // When / Then
        assertThrows(InvalidCredentialsException.class,
                () -> authService.authenticate(loginRequest));

        then(userRepository).should().findByLogin("joao123");
        then(passwordEncoder).should(never()).matches(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha estiver incorreta")
    void deveLancarExcecaoQuandoSenhaInvalida() {
        // Given
        given(userRepository.findByLogin("joao123"))
                .willReturn(Optional.of(user));

        given(passwordEncoder.matches("123", "senhaCriptografada"))
                .willReturn(false);

        // When / Then
        assertThrows(InvalidCredentialsException.class,
                () -> authService.authenticate(loginRequest));

        then(userRepository).should().findByLogin("joao123");
        then(passwordEncoder).should().matches("123", "senhaCriptografada");
    }
}

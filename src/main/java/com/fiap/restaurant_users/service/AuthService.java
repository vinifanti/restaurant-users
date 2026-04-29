package com.fiap.restaurant_users.service;

import com.fiap.restaurant_users.dto.request.LoginRequest;
import com.fiap.restaurant_users.dto.response.AuthResponse;
import com.fiap.restaurant_users.exception.InvalidCredentialsException;
import com.fiap.restaurant_users.model.User;
import com.fiap.restaurant_users.repository.UserRepository;
import com.fiap.restaurant_users.usecase.AuthUseCase;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse authenticate(LoginRequest request) {

        User user = userRepository.findByLogin(request.login())
                .orElseThrow(() -> {
                    return new InvalidCredentialsException();
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getLogin());

        return new AuthResponse(
                token
        );
    }
}

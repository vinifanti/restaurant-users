package com.fiap.restaurant_users.service;

import com.fiap.restaurant_users.dto.request.LoginRequest;
import com.fiap.restaurant_users.dto.response.UserResponse;
import com.fiap.restaurant_users.exception.InvalidCredentialsException;
import com.fiap.restaurant_users.model.User;
import com.fiap.restaurant_users.repository.UserRepository;
import com.fiap.restaurant_users.usecase.AuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse authenticate(LoginRequest request) {
        User user = userRepository.findByLogin(request.login())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return UserResponse.from(user);
    }
}

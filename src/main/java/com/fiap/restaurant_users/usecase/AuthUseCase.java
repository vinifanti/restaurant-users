package com.fiap.restaurant_users.usecase;

import com.fiap.restaurant_users.dto.request.LoginRequest;
import com.fiap.restaurant_users.dto.response.AuthResponse;

public interface AuthUseCase {

    AuthResponse authenticate(LoginRequest request);
}

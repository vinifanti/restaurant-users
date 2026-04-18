package com.fiap.restaurant_users.usecase;

import com.fiap.restaurant_users.dto.request.LoginRequest;
import com.fiap.restaurant_users.dto.response.UserResponse;

public interface AuthUseCase {

    UserResponse authenticate(LoginRequest request);
}

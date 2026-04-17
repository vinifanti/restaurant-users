package com.fiap.restaurant_users.usecase;

import com.fiap.restaurant_users.dto.request.ChangePasswordRequest;
import com.fiap.restaurant_users.dto.request.CreateUserRequest;
import com.fiap.restaurant_users.dto.request.UpdateUserRequest;
import com.fiap.restaurant_users.dto.response.UserResponse;

import java.util.List;

public interface UserUseCase {

    UserResponse create(CreateUserRequest request);

    UserResponse findById(Long id);

    List<UserResponse> findByName(String name);

    UserResponse update(Long id, UpdateUserRequest request);

    void changePassword(Long id, ChangePasswordRequest request);

    void delete(Long id);
}

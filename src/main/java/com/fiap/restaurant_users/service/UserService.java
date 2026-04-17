package com.fiap.restaurant_users.service;

import com.fiap.restaurant_users.dto.request.*;
import com.fiap.restaurant_users.dto.response.UserResponse;
import com.fiap.restaurant_users.exception.ResourceNotFoundException;
import com.fiap.restaurant_users.model.Address;
import com.fiap.restaurant_users.model.Customer;
import com.fiap.restaurant_users.model.RestaurantOwner;
import com.fiap.restaurant_users.model.User;
import com.fiap.restaurant_users.repository.UserRepository;
import com.fiap.restaurant_users.usecase.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        User user = buildUser(request);
        user.setLastModifiedAt(LocalDateTime.now());
        userRepository.save(user);

        return UserResponse.from(user);
    }

    @Override
    public UserResponse findById(Long id) {
        User user = findUserById(id);
        return UserResponse.from(user);
    }

    @Override
    public List<UserResponse> findByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        User user = findUserById(id);
        user.setName(request.name());
        user.setEmail(request.email());
        user.setLogin(request.login());
        user.setAddress(buildAddress(request.address()));
        user.setLastModifiedAt(LocalDateTime.now());
        userRepository.save(user);

        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = findUserById(id);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setLastModifiedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário com id " + id + " não encontrado"
                ));
    }

    private User buildUser(CreateUserRequest request) {
        User user = switch (request.userType().toUpperCase()) {
            case "CUSTOMER" -> new Customer();
            case "RESTAURANT_OWNER" -> new RestaurantOwner();
            default -> throw new IllegalArgumentException(
                    "Tipo inválido. Use CUSTOMER ou RESTAURANT_OWNER"
            );
        };

        user.setName(request.name());
        user.setEmail(request.email());
        user.setLogin(request.login());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setAddress(buildAddress(request.address()));

        return user;
    }

    private Address buildAddress (AddressRequest request) {
        Address address = new Address();
        address.setStreet(request.street());
        address.setNumber(request.number());
        address.setCity(request.city());
        address.setZipCode(request.zipCode());
        return address;
    }
}
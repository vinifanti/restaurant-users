package com.fiap.restaurant_users.repository;

import com.fiap.restaurant_users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByLoginAndPassword(String login, String password);

    List<User> findByNameContainingIgnoreCase(String name);
}
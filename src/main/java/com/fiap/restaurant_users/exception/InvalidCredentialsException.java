package com.fiap.restaurant_users.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Login ou senha inválidos");
    }
}

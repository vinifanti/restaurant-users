package com.fiap.restaurant_users.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer extends User {
    // campos específicos do cliente podem ser adicionados aqui futuramente
}
package com.fiap.restaurant_users.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "restaurant_owners")
public class RestaurantOwner extends User {
    // campos específicos do dono podem ser adicionados aqui futuramente
}
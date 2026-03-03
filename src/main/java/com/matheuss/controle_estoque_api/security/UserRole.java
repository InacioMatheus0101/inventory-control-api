package com.matheuss.controle_estoque_api.security;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name; // Ex: "ROLE_ADMIN", "ROLE_TECHNICIAN"

    // Método exigido pela interface GrantedAuthority do Spring Security.
    @Override
    public String getAuthority() {
        return this.name;
    }
}

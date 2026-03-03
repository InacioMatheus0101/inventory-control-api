package com.matheuss.controle_estoque_api.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    // Método para buscar um perfil pelo nome (ex: "ROLE_ADMIN")
    Optional<UserRole> findByName(String name);
}

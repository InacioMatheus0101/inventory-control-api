package com.matheuss.controle_estoque_api.security;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<UserRole, Integer> {
    Optional<UserRole> findByName(String name);
}

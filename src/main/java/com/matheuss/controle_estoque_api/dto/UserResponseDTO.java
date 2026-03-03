package com.matheuss.controle_estoque_api.dto;

import java.util.Set;

public record UserResponseDTO(
    Long id,
    String username,
    Set<String> roles
) {}

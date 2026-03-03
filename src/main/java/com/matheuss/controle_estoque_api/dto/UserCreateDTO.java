package com.matheuss.controle_estoque_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record UserCreateDTO(
    @NotBlank(message = "O nome de usuário é obrigatório.")
    String username,

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    String password,

    @NotEmpty(message = "O usuário deve ter pelo menos um perfil.")
    Set<String> roles
) {}

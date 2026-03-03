package com.matheuss.controle_estoque_api.security;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "O nome de usuário não pode ser vazio.")
    private String username;

    @NotBlank(message = "A senha não pode ser vazia.")
    private String password;
}

package com.matheuss.controle_estoque_api.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Facilita a criação do objeto de resposta.
public class LoginResponseDTO {
    private String token;
}

package com.matheuss.controle_estoque_api.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; 

@Data
@AllArgsConstructor // Mantém o construtor com todos os argumentos
@NoArgsConstructor  // Adiciona um construtor sem argumentos
public class LoginResponseDTO {
    // MODIFICADO: Renomeado 'token' para 'accessToken' para maior clareza.
    private String accessToken;
    
    // NOVO: Adicionado o campo para o refreshToken.
    private String refreshToken;
}

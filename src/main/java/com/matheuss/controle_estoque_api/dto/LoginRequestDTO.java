package com.matheuss.controle_estoque_api.dto;

import lombok.AllArgsConstructor; 
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // necessario para o jackson
public class LoginRequestDTO {
    private String username;
    private String password;
}

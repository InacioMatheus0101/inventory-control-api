package com.matheuss.controle_estoque_api.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LocationUpdateDTO {

    @Size(max = 20, message = "O número da PA deve ter no máximo 20 caracteres.")
    private String paNumber;

    @Size(max = 50, message = "O andar deve ter no máximo 50 caracteres.")
    private String floor;

    @Size(max = 100, message = "O setor deve ter no máximo 100 caracteres.")
    private String sector;

    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres.")
    private String description;
}
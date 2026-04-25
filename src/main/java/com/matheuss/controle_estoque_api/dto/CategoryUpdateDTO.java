package com.matheuss.controle_estoque_api.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryUpdateDTO {

    @Size(max = 100, message = "O nome da categoria deve ter no máximo 100 caracteres.")
    private String name;
}
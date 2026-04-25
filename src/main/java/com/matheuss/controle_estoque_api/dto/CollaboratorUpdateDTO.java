package com.matheuss.controle_estoque_api.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CollaboratorUpdateDTO {

    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String name;

    @Size(max = 50, message = "A matrícula deve ter no máximo 50 caracteres.")
    private String matricula;

    @Size(max = 100, message = "O departamento deve ter no máximo 100 caracteres.")
    private String department;
}
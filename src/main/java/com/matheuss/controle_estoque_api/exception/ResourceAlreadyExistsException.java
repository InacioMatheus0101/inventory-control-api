package com.matheuss.controle_estoque_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Anotação para que o Spring já saiba que esta exceção deve retornar 409 Conflict
@ResponseStatus(HttpStatus.CONFLICT )
public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}

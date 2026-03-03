package com.matheuss.controle_estoque_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Anotação para que o Spring já saiba que esta exceção deve retornar 400 Bad Request
@ResponseStatus(HttpStatus.BAD_REQUEST )
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}

package com.matheuss.controle_estoque_api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component("customAuthenticationEntryPoint" )
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Não precisamos mais do HandlerExceptionResolver aqui.

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        // 1. Define o status da resposta HTTP como 401 Unauthorized.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // 2. Define o tipo de conteúdo da resposta como JSON.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 3. Cria um corpo de resposta JSON claro e informativo.
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", "Credenciais inválidas ou token de autenticação ausente/expirado.");
        body.put("path", request.getRequestURI());

        // 4. Escreve o corpo JSON diretamente na resposta de saída.
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}

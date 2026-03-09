package com.matheuss.controle_estoque_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private final long expirationTime = TimeUnit.MINUTES.toMillis(20);

    public String generateToken(UserDetails userDetails) {
        Key key = getSigningKey();
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setIssuer("inventory-control-api")
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

 
    public void validateToken(String token) {

        // O método parseClaimsJws já faz toda a validação.
        // Se for inválido, ele mesmo lançará a exceção apropriada.
        // Não precisamos de um try-catch aqui, pois queremos que a exceção
        // seja propagada para o SecurityFilter.

        Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
    }

    
    public String getSubject(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    // Método auxiliar privado para extrair todas as informações (claims) do token.

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Método auxiliar privado para gerar a chave de assinatura.
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}

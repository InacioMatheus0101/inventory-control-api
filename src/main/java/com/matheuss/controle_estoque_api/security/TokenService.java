package com.matheuss.controle_estoque_api.security;

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

    // Tempo de expiração ajustado para 20 minutos.
    private final long expirationTime = TimeUnit.MINUTES.toMillis(20);

    public String generateToken(UserDetails userDetails) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());

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
  public String validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secret.getBytes());

            return Jwts.parserBuilder()
                    .setSigningKey(key) // Define a chave para verificar a assinatura.
                    .build()
                    .parseClaimsJws(token) // Faz o parse e valida o token. Lança exceção se for inválido.
                    .getBody()
                    .getSubject(); // Retorna o "subject" (username) do token.
        } catch (Exception e) {
            // Se o token for inválido (expirado, assinatura errada, etc.), retorna uma string vazia.
            return "";
        }
    }

}

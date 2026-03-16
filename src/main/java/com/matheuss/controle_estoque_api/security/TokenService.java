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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    // MODIFICADO: Definimos os tempos de expiração de forma clara e configurável.
    private final long accessTokenExpirationTime = TimeUnit.MINUTES.toMillis(15); // 15 minutos para o Access Token
    private final long refreshTokenExpirationTime = TimeUnit.HOURS.toMillis(8);   // 8 horas para o Refresh Token

    // NOVO: Método público para gerar o par de tokens.
    public LoginResponseDTO generateTokenPair(UserDetails userDetails) {
        String accessToken = generateAccessToken(userDetails);
        String refreshToken = generateRefreshToken(userDetails);
        return new LoginResponseDTO(accessToken, refreshToken);
    }

    // MODIFICADO: Renomeado de generateToken para generateAccessToken para clareza.
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "ACCESS"); // NOVO: Adicionamos um 'claim' para identificar o tipo do token.

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("inventory-control-api")
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // NOVO: Método para gerar o Refresh Token.
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "REFRESH"); // NOVO: Identifica este como um Refresh Token.

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("inventory-control-api")
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // MODIFICADO: Renomeado de validateToken para clareza.
    public void validateAccessToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        String tokenType = claims.get("type", String.class);
        if (!"ACCESS".equals(tokenType)) {
            throw new RuntimeException("Invalid token type provided. Expected ACCESS.");
        }
        // A validação de assinatura e expiração já é feita por parseClaimsJws.
    }

    // NOVO: Método para validar o Refresh Token.
    public void validateRefreshToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        String tokenType = claims.get("type", String.class);
        if (!"REFRESH".equals(tokenType)) {
            throw new RuntimeException("Invalid token type provided. Expected REFRESH.");
        }
    }

    public String getSubject(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}

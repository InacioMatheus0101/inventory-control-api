package com.matheuss.controle_estoque_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.matheuss.controle_estoque_api.exception.InvalidTokenException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.access-expiration-minutes}")
    private long accessExpirationMinutes;

    @Value("${api.security.token.refresh-expiration-hours}")
    private long refreshExpirationHours;

    public LoginResponseDTO generateTokenPair(UserDetails userDetails) {
        String accessToken  = generateAccessToken(userDetails);
        String refreshToken = generateRefreshToken(userDetails);
        return new LoginResponseDTO(accessToken, refreshToken);
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "ACCESS");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("inventory-control-api")
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()
                        + TimeUnit.MINUTES.toMillis(accessExpirationMinutes)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "REFRESH");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("inventory-control-api")
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()
                        + TimeUnit.HOURS.toMillis(refreshExpirationHours)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateAccessToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        if (!"ACCESS".equals(claims.get("type", String.class))) {
            throw new InvalidTokenException("Token inválido: tipo esperado ACCESS.");
        }
    }

    public void validateRefreshToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        if (!"REFRESH".equals(claims.get("type", String.class))) {
            throw new InvalidTokenException("Token inválido: tipo esperado REFRESH.");
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
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
package com.matheuss.controle_estoque_api.security;

import com.matheuss.controle_estoque_api.security.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    
    private final AuthenticationManager authenticationManager;

    
    private final TokenService tokenService;

    
    private final UserDetailsService userDetailsService;

  
    @PostMapping
    public ResponseEntity<LoginResponseDTO> login(
        @RequestBody @Valid LoginRequestDTO data
    ) {
        // Criar token de autenticação com username e password
        var usernamePassword = new UsernamePasswordAuthenticationToken(
            data.getUsername(),
            data.getPassword()
        );

        // Autenticar usando o AuthenticationManager
        Authentication auth = authenticationManager.authenticate(usernamePassword);

        // Obter detalhes do utilizador autenticado
        var userDetails = (User) auth.getPrincipal();

        // Gerar par de tokens (access token + refresh token)
        LoginResponseDTO tokenPair = tokenService.generateTokenPair(userDetails);

        return ResponseEntity.ok(tokenPair);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(
        @RequestBody @Valid RefreshTokenRequestDTO data
    ) {
        // Validar o refresh token
        tokenService.validateRefreshToken(data.refreshToken());

        // Extrair username do refresh token
        String username = tokenService.getSubject(data.refreshToken());

        // Carregar detalhes do utilizador
        var userDetails = this.userDetailsService.loadUserByUsername(username);

        // Gerar novo par de tokens
        LoginResponseDTO newTokenPair = tokenService.generateTokenPair(userDetails);

        return ResponseEntity.ok(newTokenPair);
    }
}

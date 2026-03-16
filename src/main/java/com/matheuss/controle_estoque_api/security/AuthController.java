package com.matheuss.controle_estoque_api.security;

import com.matheuss.controle_estoque_api.security.User;
import jakarta.validation.Valid;
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
@RequestMapping("/api/auth" )
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword());
        Authentication auth = authenticationManager.authenticate(usernamePassword);

        var userDetails = (User) auth.getPrincipal();
        
        // MODIFICADO: Agora usamos o método que retorna o par de tokens.
        LoginResponseDTO tokenPair = tokenService.generateTokenPair(userDetails);

        return ResponseEntity.ok(tokenPair);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(@RequestBody @Valid RefreshTokenRequestDTO data) {
        tokenService.validateRefreshToken(data.refreshToken());
        String username = tokenService.getSubject(data.refreshToken());
        var userDetails = this.userDetailsService.loadUserByUsername(username);
        
        // MODIFICADO: Geramos e retornamos o novo par de tokens.
        LoginResponseDTO newTokenPair = tokenService.generateTokenPair(userDetails);

        return ResponseEntity.ok(newTokenPair);
    }
}

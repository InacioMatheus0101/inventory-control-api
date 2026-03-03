package com.matheuss.controle_estoque_api.security;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO data) {
        // 1. Cria um objeto de autenticação com as credenciais recebidas.
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword());

        // 2. O Spring Security usa o AuthenticationManager para validar as credenciais.
        // Ele chamará nosso UserDetailsServiceImpl e usará o PasswordEncoder.
        // Se as credenciais estiverem erradas, ele lança uma exceção (tratada pelo Spring).
        Authentication auth = authenticationManager.authenticate(usernamePassword);

        // 3. Se a autenticação for bem-sucedida, o objeto 'auth' contém os detalhes do usuário.
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        // 4. Usamos nosso TokenService para gerar o token JWT.
        String token = tokenService.generateToken(userDetails);

        // 5. Retornamos o token em um DTO de resposta.
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}

package com.matheuss.controle_estoque_api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public PasswordEncoder passwordEncoder( ) {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http ) throws Exception {
        http
            // Desabilita o CSRF, pois não usaremos sessões/cookies (padrão para APIs JWT ).
            .csrf(csrf -> csrf.disable())
            
            // Configura a política de gerenciamento de sessão para ser STATELESS.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Define as regras de autorização para as requisições HTTP.
            .authorizeHttpRequests(authorize -> authorize
                // Permite acesso público ao endpoint de login.
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                
                // Permite acesso público à documentação do Swagger.
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // Exige autenticação para qualquer outra requisição.
                .anyRequest().authenticated()
            )
            
            // Adiciona nosso filtro customizado para ser executado antes do filtro padrão do Spring.
            // Isso garante que nosso token seja validado em cada requisição protegida.
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build( );
    }
}

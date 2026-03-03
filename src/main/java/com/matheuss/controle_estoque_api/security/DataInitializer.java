package com.matheuss.controle_estoque_api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

   @Override
    public void run(String... args) throws Exception {
        // Cria os perfis se eles não existirem.
        UserRole adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> roleRepository.save(new UserRole(null, "ROLE_ADMIN")));
        UserRole techRole = roleRepository.findByName("ROLE_TECHNICIAN").orElseGet(() -> roleRepository.save(new UserRole(null, "ROLE_TECHNICIAN")));

        // Cria um usuário administrador se ele não existir.
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            // Garante que o admin tenha ambos os perfis.
            adminUser.setRoles(Set.of(adminRole, techRole));
            userRepository.save(adminUser);
        }
        
        // Cria um usuário técnico se ele não existir.
        if (userRepository.findByUsername("tech").isEmpty()) {
            User techUser = new User();
            techUser.setUsername("tech");
            techUser.setPassword(passwordEncoder.encode("tech123"));
            // Garante que o técnico tenha apenas o perfil de técnico.
            techUser.setRoles(Set.of(techRole));
            userRepository.save(techUser);
        }
    }
}

package com.matheuss.controle_estoque_api.service;

import com.matheuss.controle_estoque_api.dto.UserCreateDTO;
import com.matheuss.controle_estoque_api.dto.UserResponseDTO;
import com.matheuss.controle_estoque_api.exception.BusinessRuleException; // Import
import com.matheuss.controle_estoque_api.exception.ResourceAlreadyExistsException; // Import
import com.matheuss.controle_estoque_api.mapper.UserMapper;
import com.matheuss.controle_estoque_api.security.User;
import com.matheuss.controle_estoque_api.security.UserRepository;
import com.matheuss.controle_estoque_api.security.UserRole;
import com.matheuss.controle_estoque_api.security.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userMapper.toResponseDTOList(userRepository.findAll());
    }

    @Transactional
    public UserResponseDTO createUser(UserCreateDTO dto) {
        // Verifica se o nome de usuário já existe
        if (userRepository.findByUsername(dto.username()).isPresent()) {
            // Lança exceção de recurso existente
            throw new ResourceAlreadyExistsException("Nome de usuário já existe: " + dto.username());
        }

        // Busca os perfis no banco de dados
        Set<UserRole> roles = dto.roles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        // Lança exceção de regra de negócio se um perfil não for encontrado
                        .orElseThrow(() -> new BusinessRuleException("Perfil (role) inválido: " + roleName)))
                .collect(Collectors.toSet());

        User newUser = new User();
        newUser.setUsername(dto.username());
        newUser.setPassword(passwordEncoder.encode(dto.password()));
        newUser.setRoles(roles);

        User savedUser = userRepository.save(newUser);
        return userMapper.toResponseDTO(savedUser);
    }
}

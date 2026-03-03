package com.matheuss.controle_estoque_api.mapper;

import com.matheuss.controle_estoque_api.dto.UserResponseDTO;
import com.matheuss.controle_estoque_api.security.User;
import com.matheuss.controle_estoque_api.security.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToString")
    UserResponseDTO toResponseDTO(User user);

    List<UserResponseDTO> toResponseDTOList(List<User> users);

    @Named("rolesToString")
    default Set<String> rolesToString(Set<UserRole> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                    .map(UserRole::getName)
                    .collect(Collectors.toSet());
    }
}

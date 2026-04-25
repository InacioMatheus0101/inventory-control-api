package com.matheuss.controle_estoque_api.mapper;

import com.matheuss.controle_estoque_api.domain.Peripheral;
import com.matheuss.controle_estoque_api.dto.PeripheralCreateDTO;
import com.matheuss.controle_estoque_api.dto.PeripheralResponseDTO;
import com.matheuss.controle_estoque_api.dto.PeripheralUpdateDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {LocationMapper.class, CollaboratorMapper.class, ComputerSimpleMapper.class}
)
public interface PeripheralMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "computer", ignore = true)
    @Mapping(target = "collaborator", ignore = true)
    @Mapping(target = "history", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Peripheral toEntity(PeripheralCreateDTO dto);

    @Mapping(source = "collaborator.id", target = "collaboratorId")
    @Mapping(source = "collaborator", target = "user")
    PeripheralResponseDTO toResponseDTO(Peripheral entity);

    List<PeripheralResponseDTO> toResponseDTOList(List<Peripheral> list);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "computer", ignore = true)
    @Mapping(target = "collaborator", ignore = true)
    @Mapping(target = "history", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(PeripheralUpdateDTO dto, @MappingTarget Peripheral entity);
}
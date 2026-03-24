package com.matheuss.controle_estoque_api.mapper;

import com.matheuss.controle_estoque_api.domain.Computer;
import com.matheuss.controle_estoque_api.dto.ComputerCreateDTO;
import com.matheuss.controle_estoque_api.dto.ComputerResponseDTO;
import com.matheuss.controle_estoque_api.dto.ComputerUpdateDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                CategoryMapper.class,
                LocationMapper.class,
                CollaboratorMapper.class,
                AssetHistoryMapper.class,
                ComponentMapper.class
        }
)
public interface ComputerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "collaborator", ignore = true)
    @Mapping(target = "history", ignore = true)
    @Mapping(target = "components", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
     @Mapping(target = "nameComputer", ignore = true)
    Computer toEntity(ComputerCreateDTO dto);

    ComputerResponseDTO toResponseDTO(Computer entity);

    List<ComputerResponseDTO> toResponseDTOList(List<Computer> entities);

    /**
     * Atualiza uma entidade Computer a partir de um DTO, ignorando campos nulos no DTO.
     * Isso permite atualizações parciais: apenas os campos fornecidos no DTO serão alterados na entidade.
     * Campos complexos como relacionamentos e status são ignorados e devem ser tratados no Service.
     *
     * @param dto    O objeto de transferência de dados com os novos valores.
     * @param entity A entidade a ser atualizada, que será modificada diretamente.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "collaborator", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "history", ignore = true)
    @Mapping(target = "components", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
     @Mapping(target = "nameComputer", ignore = true)
    void updateEntityFromDto(ComputerUpdateDTO dto, @MappingTarget Computer entity);
}

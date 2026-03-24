package com.matheuss.controle_estoque_api.mapper;

import com.matheuss.controle_estoque_api.domain.*;
import com.matheuss.controle_estoque_api.dto.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CollaboratorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assets", ignore = true)
    Collaborator toEntity(CollaboratorCreateDTO dto);

    CollaboratorResponseDTO toResponseDTO(Collaborator entity);

    CollaboratorSimpleResponseDTO toSimpleResponseDTO(Collaborator entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "assets", ignore = true)
    void updateEntityFromDto(CollaboratorUpdateDTO dto, @MappingTarget Collaborator entity);

    // ===== Assets dentro de UserResponseDTO =====
    // Este método agora usa a lógica corrigida de 'resolveAssetName'
    @Mapping(target = "name", expression = "java(resolveAssetName(asset))")
    AssetSimpleResponseDTO toAssetSimpleResponseDTO(Asset asset);

    List<AssetSimpleResponseDTO> toAssetSimpleResponseDTOList(List<Asset> assets);

    /**
     * Resolve o nome de exibição de um ativo (Asset) de forma polimórfica.
     * Esta é a correção principal: agora ele busca 'hostname' para a entidade Computer.
     *
     * @param asset O ativo a ser resolvido.
     * @return O nome de exibição apropriado (hostname, name, etc.).
     */
    default String resolveAssetName(Asset asset) {
        if (asset == null) {
            return null;
        }

        // Usando pattern matching do Java 17+ para um código mais limpo e seguro
        if (asset instanceof Computer computer) {
            // CORREÇÃO APLICADA: Usa getHostname() para a entidade Computer.
            return computer.getHostname();
        }
        if (asset instanceof Peripheral peripheral) {
            // Para Periféricos, continuamos usando getName().
            // Futuramente, podemos refatorar para um nome mais específico se necessário.
            return peripheral.getName();
        }
        if (asset instanceof Component component) {
            // Para Componentes, também continuamos usando getName().
            return component.getName();
        }

        // Retorna um valor padrão caso o tipo de ativo não seja reconhecido.
        return "Ativo Desconhecido";
    }
}

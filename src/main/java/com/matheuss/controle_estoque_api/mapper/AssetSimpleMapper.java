package com.matheuss.controle_estoque_api.mapper;

import com.matheuss.controle_estoque_api.domain.Asset;
import com.matheuss.controle_estoque_api.dto.AssetSimpleResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssetSimpleMapper {

    // ✅ CORREÇÃO: Não tentar mapear campos que não existem em AssetSimpleResponseDTO
    // AssetSimpleResponseDTO tem apenas: id, assetTag, status, name
    AssetSimpleResponseDTO toResponseDTO(Asset asset);
}

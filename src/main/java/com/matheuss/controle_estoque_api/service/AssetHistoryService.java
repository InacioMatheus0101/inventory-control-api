package com.matheuss.controle_estoque_api.service;

import com.matheuss.controle_estoque_api.domain.Asset;
import com.matheuss.controle_estoque_api.domain.Collaborator;
import com.matheuss.controle_estoque_api.domain.history.AssetHistory;
import com.matheuss.controle_estoque_api.domain.history.HistoryEventType;
import com.matheuss.controle_estoque_api.dto.AssetHistoryResponseDTO;
import com.matheuss.controle_estoque_api.dto.CollaboratorSimpleResponseDTO;
import com.matheuss.controle_estoque_api.mapper.AssetHistoryMapper;
import com.matheuss.controle_estoque_api.repository.AssetHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetHistoryService {

    private final AssetHistoryRepository assetHistoryRepository;
    private final AssetHistoryMapper assetHistoryMapper;

    @Transactional
    public void registerEvent(Asset asset, HistoryEventType eventType, String details, Collaborator collaborator) {
        AssetHistory history = new AssetHistory();
        history.setAsset(asset);
        history.setEventType(eventType);
        history.setDetails(details);
        history.setAssociatedUser(collaborator);
        assetHistoryRepository.save(history);
    }

    // ✅ CORREÇÃO: Adicionar paginação ao retorno do histórico
    @Transactional(readOnly = true)
    public Page<AssetHistoryResponseDTO> getHistoryByAssetId(Long assetId, Pageable pageable) {
        Page<AssetHistory> historyPage = assetHistoryRepository.findByAssetIdOrderByEventDateDesc(assetId, pageable);
        
        List<AssetHistoryResponseDTO> dtoList = historyPage.getContent().stream()
                .map(history -> {
                    AssetHistoryResponseDTO dto = new AssetHistoryResponseDTO();
                    dto.setId(history.getId());
                    dto.setEventType(history.getEventType().toString());
                    dto.setDetails(history.getDetails());
                    dto.setEventDate(history.getEventDate());
                    
                    // ✅ CORREÇÃO: Usar apenas os campos que existem em CollaboratorSimpleResponseDTO
                    if (history.getAssociatedUser() != null) {
                        CollaboratorSimpleResponseDTO userDto = new CollaboratorSimpleResponseDTO();
                        userDto.setId(history.getAssociatedUser().getId());
                        userDto.setName(history.getAssociatedUser().getName());
                        // Não adicionar email - CollaboratorSimpleResponseDTO já tem os campos necessários
                        dto.setAssociatedUser(userDto);
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtoList, pageable, historyPage.getTotalElements());
    }
}

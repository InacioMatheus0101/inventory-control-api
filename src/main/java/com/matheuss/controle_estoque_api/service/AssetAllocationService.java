package com.matheuss.controle_estoque_api.service;

import com.matheuss.controle_estoque_api.domain.*;
import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.domain.enums.EquipmentState;
import com.matheuss.controle_estoque_api.domain.history.HistoryEventType;
import com.matheuss.controle_estoque_api.dto.AssetSimpleResponseDTO;
import com.matheuss.controle_estoque_api.exception.BusinessRuleException;
import com.matheuss.controle_estoque_api.mapper.AssetSimpleMapper;
import com.matheuss.controle_estoque_api.repository.AssetRepository;
import com.matheuss.controle_estoque_api.repository.CollaboratorRepository;
import com.matheuss.controle_estoque_api.repository.LocationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssetAllocationService {

    private final AssetRepository assetRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final LocationRepository locationRepository;
    private final AssetHistoryService assetHistoryService;
    private final AssetSimpleMapper assetSimpleMapper;  // ✅ ADICIONADO

    // ✅ CORRIGIDO: Retorna AssetSimpleResponseDTO
    @Transactional
    public AssetSimpleResponseDTO assignToCollaborator(Long assetId, Long collaboratorId) {
        Asset asset = requireAsset(assetId);
        Collaborator collaborator = requireCollaborator(collaboratorId);

        validateCanAssignFromStock(asset);

        asset.setCollaborator(collaborator);
        asset.setLocation(null);
        asset.setStatus(AssetStatus.EM_USO);

        Asset savedAsset = assetRepository.save(asset);  // ✅ SALVAR E CAPTURAR

        assetHistoryService.registerEvent(savedAsset, HistoryEventType.ALOCACAO,
                "Ativo alocado para o colaborador: " + collaborator.getName() + " (ID: " + collaborator.getId() + ").",
                collaborator
        );

        return assetSimpleMapper.toResponseDTO(savedAsset);  // ✅ RETORNAR DTO COM MÉTODO CORRETO
    }

    // ✅ CORRIGIDO: Retorna AssetSimpleResponseDTO
    @Transactional
    public AssetSimpleResponseDTO assignToLocation(Long assetId, Long locationId) {
        Asset asset = requireAsset(assetId);
        Location location = requireLocation(locationId);

        validateCanAssignFromStock(asset);

        asset.setCollaborator(null);
        asset.setLocation(location);
        asset.setStatus(AssetStatus.EM_USO);

        Asset savedAsset = assetRepository.save(asset);  // ✅ SALVAR E CAPTURAR

        assetHistoryService.registerEvent(savedAsset, HistoryEventType.ALOCACAO,
                "Ativo alocado para a localização: PA " + location.getPaNumber() + " (" + location.getSector() + ").",
                null
        );

        return assetSimpleMapper.toResponseDTO(savedAsset);  // ✅ RETORNAR DTO COM MÉTODO CORRETO
    }

    // ✅ CORRIGIDO: Retorna AssetSimpleResponseDTO
    @Transactional
    public AssetSimpleResponseDTO unassignToStock(Long assetId) {
        Asset asset = requireAsset(assetId);

        validateCanUnassignToStock(asset);

        Collaborator previousCollaborator = asset.getCollaborator();
        Location previousLocation = asset.getLocation();

        asset.setCollaborator(null);
        asset.setLocation(null);
        asset.setStatus(AssetStatus.EM_ESTOQUE);

        Asset savedAsset = assetRepository.save(asset);  // ✅ SALVAR E CAPTURAR

        recordReturn(savedAsset, previousCollaborator, previousLocation);

        return assetSimpleMapper.toResponseDTO(savedAsset);  // ✅ RETORNAR DTO COM MÉTODO CORRETO
    }

    // ✅ CORRIGIDO: Retorna AssetSimpleResponseDTO
    @Transactional
    public AssetSimpleResponseDTO disposeAsset(Long assetId) {
        Asset asset = requireAsset(assetId);

        if (asset.getStatus() == AssetStatus.DESCARTADO) {
            throw new BusinessRuleException("Operação não permitida: O ativo já foi descartado.");
        }
        if (asset.getStatus() == AssetStatus.EM_USO) {
            throw new BusinessRuleException("Operação não permitida: O ativo não pode ser descartado pois está em uso.");
        }

        if (asset instanceof Computer) {
            Computer computer = (Computer) asset;
            if (computer.getComponents() != null && !computer.getComponents().isEmpty()) {
                for (Component component : computer.getComponents()) {
                    component.setComputer(null);
                    component.setStatus(AssetStatus.EM_ESTOQUE);
                    assetHistoryService.registerEvent(component, HistoryEventType.DEVOLUCAO,
                        "Componente retornado ao estoque após descarte do computador: " + computer.getHostname(), null);
                }
            }
        }

        asset.setStatus(AssetStatus.DESCARTADO);
        asset.setEquipmentState(EquipmentState.DESCARTADO);
        asset.setCollaborator(null);
        asset.setLocation(null);

        Asset savedAsset = assetRepository.save(asset);  // ✅ SALVAR E CAPTURAR

        assetHistoryService.registerEvent(savedAsset, HistoryEventType.DESCARTE, "Ativo foi marcado como descartado.", null);

        return assetSimpleMapper.toResponseDTO(savedAsset);  // ✅ RETORNAR DTO COM MÉTODO CORRETO
    }

    // --- Helpers ---

    private void validateCanAssignFromStock(Asset asset) {
        if (asset.getStatus() != AssetStatus.EM_ESTOQUE) {
            throw new BusinessRuleException("Operação não permitida: o ativo '" + asset.getAssetTag() + "' não está em estoque.");
        }
        if (asset.getCollaborator() != null || asset.getLocation() != null) {
            throw new BusinessRuleException("Operação não permitida: o ativo já está alocado.");
        }
    }

    private void validateCanUnassignToStock(Asset asset) {
        if (asset.getStatus() != AssetStatus.EM_USO) {
            throw new BusinessRuleException("Operação não permitida: o ativo não está em uso para ser devolvido.");
        }
        if (asset.getCollaborator() == null && asset.getLocation() == null) {
            throw new BusinessRuleException("Operação não permitida: o ativo não está alocado a ninguém/nenhuma PA.");
        }
    }

    private void recordReturn(Asset asset, Collaborator previousCollaborator, Location previousLocation) {
        String details;
        if (previousCollaborator != null) {
            details = "Ativo devolvido ao estoque. Estava anteriormente com o colaborador: " + previousCollaborator.getName();
        } else if (previousLocation != null) {
            details = "Ativo devolvido ao estoque. Estava anteriormente na localização: PA " + previousLocation.getPaNumber();
        } else {
            details = "Ativo devolvido ao estoque.";
        }
        assetHistoryService.registerEvent(asset, HistoryEventType.DEVOLUCAO, details, previousCollaborator);
    }

    private Asset requireAsset(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ativo não encontrado com o ID: " + id));
    }

    private Collaborator requireCollaborator(Long id) {
        return collaboratorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Colaborador não encontrado com o ID: " + id));
    }

    private Location requireLocation(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Localização não encontrada com o ID: " + id));
    }
}

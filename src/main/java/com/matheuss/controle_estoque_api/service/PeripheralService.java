package com.matheuss.controle_estoque_api.service;

import com.matheuss.controle_estoque_api.domain.Collaborator;
import com.matheuss.controle_estoque_api.domain.Location;
import com.matheuss.controle_estoque_api.domain.Peripheral;
import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.domain.history.HistoryEventType;
import com.matheuss.controle_estoque_api.dto.PeripheralCreateDTO;
import com.matheuss.controle_estoque_api.dto.PeripheralResponseDTO;
import com.matheuss.controle_estoque_api.dto.PeripheralUpdateDTO;
import com.matheuss.controle_estoque_api.exception.BusinessRuleException; // Import
import com.matheuss.controle_estoque_api.exception.ResourceAlreadyExistsException; // Import
import com.matheuss.controle_estoque_api.mapper.PeripheralMapper;
import com.matheuss.controle_estoque_api.repository.AssetRepository;
import com.matheuss.controle_estoque_api.repository.PeripheralRepository;
import com.matheuss.controle_estoque_api.repository.specification.PeripheralSpecification;
import com.matheuss.controle_estoque_api.service.support.EntityResolver;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PeripheralService {

    private final PeripheralRepository peripheralRepository;
    private final AssetRepository assetRepository;
    private final PeripheralMapper peripheralMapper;
    private final AssetHistoryService assetHistoryService;
    private final EntityResolver resolver;

    @Transactional
    public PeripheralResponseDTO createPeripheral(PeripheralCreateDTO dto) {
        if (dto.getPatrimonio() != null && !dto.getPatrimonio().isBlank() && assetRepository.existsByPatrimonio(dto.getPatrimonio())) {
            // Lança exceção de recurso existente
            throw new ResourceAlreadyExistsException("Já existe um ativo com o número de patrimônio: " + dto.getPatrimonio());
        }
        if (dto.getAssetTag() != null && !dto.getAssetTag().isBlank() && assetRepository.existsByAssetTag(dto.getAssetTag())) {
            // Lança exceção de recurso existente
            throw new ResourceAlreadyExistsException("Já existe um ativo com o Asset Tag: " + dto.getAssetTag());
        }
        
        Peripheral entity = peripheralMapper.toEntity(dto);
        entity.setLocation(resolver.optionalLocation(dto.getLocationId()));
        entity.setComputer(resolver.optionalComputer(dto.getComputerId()));
        entity.setStatus(AssetStatus.EM_ESTOQUE);
        entity.setCollaborator(null);
        Peripheral saved = peripheralRepository.save(entity);
        assetHistoryService.registerEvent(saved, HistoryEventType.CRIACAO, "Periférico cadastrado no sistema.", null);
        return peripheralMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<PeripheralResponseDTO> getAllPeripherals(
            AssetStatus status, String type, String name, Pageable pageable) {
        
        Specification<Peripheral> spec = Specification.where(PeripheralSpecification.hasStatus(status))
                .and(PeripheralSpecification.typeContains(type))
                .and(PeripheralSpecification.nameContains(name));

        Page<Peripheral> peripheralPage = peripheralRepository.findAll(spec, pageable);
        
        return peripheralPage.map(peripheralMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public PeripheralResponseDTO getPeripheralById(Long id) {
        return peripheralRepository.findById(id)
                .map(peripheralMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Periférico não encontrado com o ID: " + id));
    }

    @Transactional
    public PeripheralResponseDTO updatePeripheral(Long id, PeripheralUpdateDTO dto) {
        Peripheral peripheral = resolver.requirePeripheral(id);
        Location newLocation = resolver.optionalLocation(dto.getLocationId());
        Collaborator newCollaborator = resolver.optionalCollaborator(dto.getCollaboratorId());

        if (dto.getPatrimonio() != null && !dto.getPatrimonio().isBlank() && !Objects.equals(peripheral.getPatrimonio(), dto.getPatrimonio())) {
            if (assetRepository.existsByPatrimonio(dto.getPatrimonio())) {
                // Lança exceção de recurso existente
                throw new ResourceAlreadyExistsException("Operação não permitida: Já existe outro ativo com o patrimônio: " + dto.getPatrimonio());
            }
        }
        if (dto.getAssetTag() != null && !dto.getAssetTag().isBlank() && !Objects.equals(peripheral.getAssetTag(), dto.getAssetTag())) {
            if (assetRepository.existsByAssetTag(dto.getAssetTag())) {
                // Lança exceção de recurso existente
                throw new ResourceAlreadyExistsException("Operação não permitida: Já existe outro ativo com o Asset Tag: " + dto.getAssetTag());
            }
        }

        peripheralMapper.updateEntityFromDto(dto, peripheral);
        if (dto.getCategoryId() != null) {
            peripheral.setCategory(resolver.requireCategory(dto.getCategoryId()));
        }
        if (dto.getComputerId() != null) {
            peripheral.setComputer(resolver.optionalComputer(dto.getComputerId()));
        }

        Location oldLocation = peripheral.getLocation();
        Collaborator oldCollaborator = peripheral.getCollaborator();

        if (newLocation != null && newCollaborator != null) {
            // Lança exceção de regra de negócio
            throw new BusinessRuleException("Operação não permitida: Um ativo não pode ser alocado para um colaborador e uma localização ao mesmo tempo.");
        }

        peripheral.setLocation(newLocation);
        peripheral.setCollaborator(newCollaborator);

        if (newLocation != null || newCollaborator != null) {
            peripheral.setStatus(AssetStatus.EM_USO);
        } else {
            peripheral.setStatus(AssetStatus.EM_ESTOQUE);
        }

        assetHistoryService.registerEvent(peripheral, HistoryEventType.ATUALIZACAO, "Dados do ativo foram atualizados.", null);

        if (!Objects.equals(oldLocation, newLocation)) {
            if (newLocation != null) {
               assetHistoryService.registerEvent(peripheral, HistoryEventType.ALOCACAO, "Ativo alocado para a localização PA: " + newLocation.getPaNumber(), null);
            }
            if (oldLocation != null) {
                assetHistoryService.registerEvent(peripheral, HistoryEventType.DEVOLUCAO, "Ativo devolvido da localização PA: " + oldLocation.getPaNumber(), null);
            }
        }
        if (!Objects.equals(oldCollaborator, newCollaborator)) {
            if (newCollaborator != null) {
                assetHistoryService.registerEvent(peripheral, HistoryEventType.ALOCACAO, "Ativo alocado para o colaborador: " + newCollaborator.getName(), null);
            }
            if (oldCollaborator != null) {
                assetHistoryService.registerEvent(peripheral, HistoryEventType.DEVOLUCAO, "Ativo devolvido pelo colaborador: " + oldCollaborator.getName(), null);
            }
        }

        Peripheral updatedPeripheral = peripheralRepository.save(peripheral);
        return peripheralMapper.toResponseDTO(updatedPeripheral);
    }
}

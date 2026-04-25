package com.matheuss.controle_estoque_api.service;

import com.matheuss.controle_estoque_api.domain.Collaborator;
import com.matheuss.controle_estoque_api.domain.Computer;
import com.matheuss.controle_estoque_api.domain.Location;
import com.matheuss.controle_estoque_api.domain.Peripheral;
import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.domain.history.HistoryEventType;
import com.matheuss.controle_estoque_api.dto.PeripheralCreateDTO;
import com.matheuss.controle_estoque_api.dto.PeripheralResponseDTO;
import com.matheuss.controle_estoque_api.dto.PeripheralUpdateDTO;
import com.matheuss.controle_estoque_api.exception.BusinessRuleException;
import com.matheuss.controle_estoque_api.exception.ResourceAlreadyExistsException;
import com.matheuss.controle_estoque_api.mapper.PeripheralMapper;
import com.matheuss.controle_estoque_api.repository.AssetRepository;
import com.matheuss.controle_estoque_api.repository.PeripheralRepository;
import com.matheuss.controle_estoque_api.repository.specification.PeripheralSpecification;
import com.matheuss.controle_estoque_api.service.support.EntityResolver;
import com.matheuss.controle_estoque_api.service.validator.AssetValidator;
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
    private final AssetValidator assetValidator;

    private static final long REMOVE_SENTINEL = 0L;

    @Transactional
    public PeripheralResponseDTO createPeripheral(PeripheralCreateDTO dto) {

        if (dto.getPatrimonio() != null && !dto.getPatrimonio().isBlank()
                && assetRepository.existsByPatrimonio(dto.getPatrimonio())) {
            throw new ResourceAlreadyExistsException(
                    "Já existe um ativo com o número de patrimônio: " + dto.getPatrimonio());
        }

        if (dto.getAssetTag() != null && !dto.getAssetTag().isBlank()
                && assetRepository.existsByAssetTag(dto.getAssetTag())) {
            throw new ResourceAlreadyExistsException(
                    "Já existe um ativo com o Asset Tag: " + dto.getAssetTag());
        }

        Peripheral entity = peripheralMapper.toEntity(dto);

        entity.setLocation(resolver.optionalLocation(dto.getLocationId()));
        Computer computer = resolver.optionalComputer(dto.getComputerId());
        entity.setComputer(computer);
        entity.setCollaborator(null);

        // ─── fallback de status ─────────────────────────────
        if (entity.getStatus() == null) {
            if (computer != null || entity.getLocation() != null) {
                entity.setStatus(AssetStatus.EM_USO);
            } else {
                entity.setStatus(AssetStatus.EM_ESTOQUE);
            }
        }

        // ─── validações centralizadas ───────────────────────
        assetValidator.validateState(entity.getEquipmentState(), entity.getStatus());

        assetValidator.validateAllocationConsistency(
                entity.getStatus(),
                entity.getLocation(),
                null,
                computer
        );

        Peripheral saved = peripheralRepository.save(entity);

        String details = "Periférico cadastrado no sistema.";
        if (computer != null) {
            details += " Vinculado ao computador: " + computer.getHostname();
        }

        assetHistoryService.registerEvent(saved, HistoryEventType.CRIACAO, details, null);

        return peripheralMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<PeripheralResponseDTO> getAllPeripherals(
            AssetStatus status, String type, String name, String patrimonio, Pageable pageable) {

        Specification<Peripheral> spec = Specification
                .where(PeripheralSpecification.hasStatus(status))
                .and(PeripheralSpecification.typeContains(type))
                .and(PeripheralSpecification.nameContains(name))
                .and(PeripheralSpecification.patrimonioContains(patrimonio));

        return peripheralRepository.findAll(spec, pageable)
                .map(peripheralMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public PeripheralResponseDTO getPeripheralById(Long id) {
        return peripheralRepository.findByIdWithDetails(id)
                .map(peripheralMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Periférico não encontrado com o ID: " + id));
    }

    @Transactional
    public PeripheralResponseDTO updatePeripheral(Long id, PeripheralUpdateDTO dto) {

        Peripheral peripheral = resolver.requirePeripheral(id);

        Location oldLocation = peripheral.getLocation();
        Collaborator oldCollaborator = peripheral.getCollaborator();
        Computer oldComputer = peripheral.getComputer();

        Location newLocation = resolveLocation(dto.getLocationId(), oldLocation);
        Collaborator newCollaborator = resolveCollaborator(dto.getCollaboratorId(), oldCollaborator);
        Computer newComputer = resolveComputer(dto.getComputerId(), oldComputer);

        if (dto.getPatrimonio() != null && !dto.getPatrimonio().isBlank()
                && !Objects.equals(peripheral.getPatrimonio(), dto.getPatrimonio())
                && assetRepository.existsByPatrimonio(dto.getPatrimonio())) {
            throw new ResourceAlreadyExistsException(
                    "Operação não permitida: Já existe outro ativo com o patrimônio: " + dto.getPatrimonio());
        }

        if (dto.getAssetTag() != null && !dto.getAssetTag().isBlank()
                && !Objects.equals(peripheral.getAssetTag(), dto.getAssetTag())
                && assetRepository.existsByAssetTag(dto.getAssetTag())) {
            throw new ResourceAlreadyExistsException(
                    "Operação não permitida: Já existe outro ativo com o Asset Tag: " + dto.getAssetTag());
        }

        if (newLocation != null && newCollaborator != null) {
            throw new BusinessRuleException(
                    "Operação não permitida: Um ativo não pode ser alocado para um colaborador e uma localização ao mesmo tempo.");
        }

        if (oldCollaborator != null && newLocation != null && !Objects.equals(oldLocation, newLocation)) {
            throw new BusinessRuleException(
                    "Não é possível alterar a localização enquanto o ativo está alocado a um colaborador.");
        }

        if (oldLocation != null && newCollaborator != null && !Objects.equals(oldCollaborator, newCollaborator)) {
            throw new BusinessRuleException(
                    "Não é possível alterar o colaborador enquanto o ativo está alocado a uma localização.");
        }

        // ─── aplica campos simples ─────────────────────────
        peripheralMapper.updateEntityFromDto(dto, peripheral);

        if (dto.getCategoryId() != null) {
            peripheral.setCategory(resolver.requireCategory(dto.getCategoryId()));
        }

        // ─── aplica vínculos ───────────────────────────────
        peripheral.setLocation(newLocation);
        peripheral.setCollaborator(newCollaborator);
        peripheral.setComputer(newComputer);

        // ─── status automático ─────────────────────────────
        if (dto.getStatus() == null) {
            if (newLocation != null || newCollaborator != null || newComputer != null) {
                peripheral.setStatus(AssetStatus.EM_USO);
            } else {
                peripheral.setStatus(AssetStatus.EM_ESTOQUE);
            }
        }

        // ─── validações centralizadas ───────────────────────
        assetValidator.validateState(peripheral.getEquipmentState(), peripheral.getStatus());

        assetValidator.validateAllocationConsistency(
                peripheral.getStatus(),
                newLocation,
                newCollaborator,
                newComputer
        );

        // ─── histórico ─────────────────────────────────────
        assetHistoryService.registerEvent(
                peripheral, HistoryEventType.ATUALIZACAO, "Dados do ativo foram atualizados.", null);

        if (!Objects.equals(oldLocation, newLocation)) {
            if (newLocation != null) {
                assetHistoryService.registerEvent(peripheral, HistoryEventType.ALOCACAO,
                        "Ativo alocado para a localização PA: " + newLocation.getPaNumber(), null);
            }
            if (oldLocation != null) {
                assetHistoryService.registerEvent(peripheral, HistoryEventType.DEVOLUCAO,
                        "Ativo devolvido da localização PA: " + oldLocation.getPaNumber(), null);
            }
        }

        if (!Objects.equals(oldCollaborator, newCollaborator)) {
            if (newCollaborator != null) {
                assetHistoryService.registerEvent(peripheral, HistoryEventType.ALOCACAO,
                        "Ativo alocado para o colaborador: " + newCollaborator.getName(), null);
            }
            if (oldCollaborator != null) {
                assetHistoryService.registerEvent(peripheral, HistoryEventType.DEVOLUCAO,
                        "Ativo devolvido pelo colaborador: " + oldCollaborator.getName(), null);
            }
        }

        if (!Objects.equals(oldComputer, newComputer)) {
            if (newComputer != null) {
                assetHistoryService.registerEvent(peripheral, HistoryEventType.ALOCACAO,
                        "Ativo vinculado ao computador: " + newComputer.getHostname(), null);
            }
            if (oldComputer != null) {
                assetHistoryService.registerEvent(peripheral, HistoryEventType.DEVOLUCAO,
                        "Ativo desvinculado do computador: " + oldComputer.getHostname(), null);
            }
        }

        Peripheral updated = peripheralRepository.save(peripheral);
        return peripheralMapper.toResponseDTO(updated);
    }

    // ─── helpers ─────────────────────────────────────────

    private Location resolveLocation(Long id, Location current) {
        if (id == null) return current;
        if (id == REMOVE_SENTINEL) return null;
        return resolver.optionalLocation(id);
    }

    private Collaborator resolveCollaborator(Long id, Collaborator current) {
        if (id == null) return current;
        if (id == REMOVE_SENTINEL) return null;
        return resolver.optionalCollaborator(id);
    }

    private Computer resolveComputer(Long id, Computer current) {
        if (id == null) return current;
        if (id == REMOVE_SENTINEL) return null;
        return resolver.optionalComputer(id);
    }
}
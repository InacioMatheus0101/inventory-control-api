package com.matheuss.controle_estoque_api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.matheuss.controle_estoque_api.domain.Collaborator;
import com.matheuss.controle_estoque_api.domain.Component;
import com.matheuss.controle_estoque_api.domain.Computer;
import com.matheuss.controle_estoque_api.domain.Location;
import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.domain.history.HistoryEventType;
import com.matheuss.controle_estoque_api.dto.ComputerCreateDTO;
import com.matheuss.controle_estoque_api.dto.ComputerResponseDTO;
import com.matheuss.controle_estoque_api.dto.ComputerUpdateDTO;
import com.matheuss.controle_estoque_api.exception.BusinessRuleException;
import com.matheuss.controle_estoque_api.exception.ResourceAlreadyExistsException;
import com.matheuss.controle_estoque_api.mapper.ComputerMapper;
import com.matheuss.controle_estoque_api.repository.AssetRepository;
import com.matheuss.controle_estoque_api.repository.ComponentRepository;
import com.matheuss.controle_estoque_api.repository.ComputerRepository;
import com.matheuss.controle_estoque_api.repository.specification.ComputerSpecification;
import com.matheuss.controle_estoque_api.service.support.EntityResolver;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.matheuss.controle_estoque_api.service.validator.AssetValidator;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ComputerService {

    private final ComputerRepository computerRepository;
    private final AssetRepository assetRepository;
    private final ComponentRepository componentRepository;
    private final ComputerMapper computerMapper;
    private final AssetHistoryService assetHistoryService;
    private final EntityResolver resolver;
    private final AssetValidator assetValidator;

    // ══════════════════════════════════════════════════════════════════════
    // CRUD
    // ══════════════════════════════════════════════════════════════════════

    @Transactional
    public ComputerResponseDTO createComputer(ComputerCreateDTO dto) {
        if (assetRepository.existsByPatrimonio(dto.getPatrimonio())) {
            throw new ResourceAlreadyExistsException(
                "Já existe um ativo com o número de patrimônio: " + dto.getPatrimonio());
        }
        if (dto.getAssetTag() != null && !dto.getAssetTag().isBlank()
                && assetRepository.existsByAssetTag(dto.getAssetTag())) {
            throw new ResourceAlreadyExistsException(
                "Já existe um ativo com o Asset Tag: " + dto.getAssetTag());
        }

        Computer entity = computerMapper.toEntity(dto);
        entity.setCategory(resolver.requireCategory(dto.getCategoryId()));
        entity.setLocation(resolver.optionalLocation(dto.getLocationId()));
        entity.setStatus(AssetStatus.EM_ESTOQUE);
        entity.setCollaborator(null);

        assetValidator.validateState(entity.getEquipmentState(), entity.getStatus());
        assetValidator.validateAllocationConsistency(
                entity.getStatus(), entity.getLocation(), null, null);

        Computer saved = computerRepository.save(entity);
        assetHistoryService.registerEvent(saved, HistoryEventType.CRIACAO,
                "Ativo cadastrado no sistema.", null);

        return computerMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<ComputerResponseDTO> getAllComputers(
            AssetStatus status, String hostname, String patrimonio,
            String serialNumber, Pageable pageable) {

        Specification<Computer> spec = Specification
                .where(ComputerSpecification.hasStatus(status))
                .and(ComputerSpecification.hostnameContains(hostname))
                .and(ComputerSpecification.patrimonioContains(patrimonio))
                .and(ComputerSpecification.serialNumberContains(serialNumber));

        return computerRepository.findAll(spec, pageable)
                .map(computerMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public ComputerResponseDTO getComputerById(Long id) {
        Computer entity = computerRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Computador não encontrado com o ID: " + id));
        return computerMapper.toResponseDTO(entity);
    }

    @Transactional
    public ComputerResponseDTO updateComputer(Long id, ComputerUpdateDTO dto) {
        Computer computer = resolver.requireComputer(id);

        validateUniquenessOnUpdate(dto, computer);
        computerMapper.updateEntityFromDto(dto, computer);

        if (dto.getCategoryId() != null) {
            computer.setCategory(resolver.requireCategory(dto.getCategoryId()));
        }

        List<Runnable> historyEvents = applyAllocationChanges(dto, computer);

        assetValidator.validateState(computer.getEquipmentState(), computer.getStatus());
        assetValidator.validateAllocationConsistency(
                computer.getStatus(), computer.getLocation(), computer.getCollaborator(), null);

        Computer saved = computerRepository.save(computer);

        assetHistoryService.registerEvent(saved, HistoryEventType.ATUALIZACAO,
                "Dados do ativo foram atualizados.", null);
        historyEvents.forEach(Runnable::run);

        return computerMapper.toResponseDTO(saved);
    }

    // ══════════════════════════════════════════════════════════════════════
    // COMPONENTES
    // ══════════════════════════════════════════════════════════════════════

    @Transactional
    public ComputerResponseDTO swapComponent(Long computerId, Long componentToUninstallId, Long componentToInstallId) {
        Computer computer             = resolver.requireComputer(computerId);
        Component componentToUninstall = resolver.requireComponent(componentToUninstallId);
        Component componentToInstall   = resolver.requireComponent(componentToInstallId);

        // ── Validações de negócio ──────────────────────────────────────

        if (Objects.equals(componentToUninstallId, componentToInstallId)) {
            throw new BusinessRuleException(
                "Operação não permitida: não é possível trocar um componente por ele mesmo.");
        }

        if (!computer.getComponents().contains(componentToUninstall)) {
            throw new BusinessRuleException(String.format(
                "Operação não permitida: o componente '%s' (ID: %d) não está instalado no computador '%s'.",
                componentToUninstall.getName(), componentToUninstallId, computer.getHostname()));
        }

        if (componentToInstall.getStatus() != AssetStatus.EM_ESTOQUE) {
            throw new BusinessRuleException(String.format(
                "Operação não permitida: o componente '%s' (ID: %d) não está em estoque e não pode ser instalado.",
                componentToInstall.getName(), componentToInstallId));
        }

        if (componentToInstall.getComputer() != null) {
            throw new BusinessRuleException(String.format(
                "Operação não permitida: o componente '%s' (ID: %d) já está vinculado a outro computador.",
                componentToInstall.getName(), componentToInstallId));
        }

        if (!Objects.equals(componentToUninstall.getType(), componentToInstall.getType())) {
            throw new BusinessRuleException(String.format(
                "Operação não permitida: a troca só pode ser feita entre componentes do mesmo tipo. " +
                "Tipo atual: '%s' — Tipo novo: '%s'.",
                componentToUninstall.getType(), componentToInstall.getType()));
        }

        // ── Mutação de estado ──────────────────────────────────────────

        componentToUninstall.setComputer(null);
        componentToUninstall.setStatus(AssetStatus.EM_ESTOQUE);

        componentToInstall.setComputer(computer);
        componentToInstall.setStatus(AssetStatus.EM_USO);

        assetValidator.validateState(componentToUninstall.getEquipmentState(), componentToUninstall.getStatus());
        assetValidator.validateState(componentToInstall.getEquipmentState(), componentToInstall.getStatus());
        assetValidator.validateAllocationConsistency(componentToUninstall.getStatus(), null, null, null);
        assetValidator.validateAllocationConsistency(componentToInstall.getStatus(), null, null, computer);

        // ── Persistência ───────────────────────────────────────────────

        componentRepository.save(componentToUninstall);
        componentRepository.save(componentToInstall);
        computerRepository.save(computer);

        // ── Histórico ──────────────────────────────────────────────────

        assetHistoryService.registerEvent(componentToUninstall, HistoryEventType.DEVOLUCAO,
                String.format("Componente '%s' (Tipo: %s) trocado e devolvido ao estoque.",
                        componentToUninstall.getName(), componentToUninstall.getType()), null);

        assetHistoryService.registerEvent(componentToInstall, HistoryEventType.INSTALACAO,
                String.format("Componente '%s' (Tipo: %s) instalado via troca no computador '%s'.",
                        componentToInstall.getName(), componentToInstall.getType(), computer.getHostname()), null);

        return getComputerById(computerId);
    }

    // ══════════════════════════════════════════════════════════════════════
    // MÉTODOS PRIVADOS
    // ══════════════════════════════════════════════════════════════════════

    private void validateUniquenessOnUpdate(ComputerUpdateDTO dto, Computer computer) {
        if (dto.getPatrimonio() != null && !dto.getPatrimonio().isBlank()
                && !Objects.equals(computer.getPatrimonio(), dto.getPatrimonio())
                && assetRepository.existsByPatrimonio(dto.getPatrimonio())) {
            throw new ResourceAlreadyExistsException(
                "Operação não permitida: já existe outro ativo com o patrimônio: " + dto.getPatrimonio());
        }
        if (dto.getAssetTag() != null && !dto.getAssetTag().isBlank()
                && !Objects.equals(computer.getAssetTag(), dto.getAssetTag())
                && assetRepository.existsByAssetTag(dto.getAssetTag())) {
            throw new ResourceAlreadyExistsException(
                "Operação não permitida: já existe outro ativo com o Asset Tag: " + dto.getAssetTag());
        }
    }

    private List<Runnable> applyAllocationChanges(ComputerUpdateDTO dto, Computer computer) {
        boolean locationRequested     = dto.getLocationId() != null;
        boolean collaboratorRequested = dto.getCollaboratorId() != null;

        if (!locationRequested && !collaboratorRequested) {
            return Collections.emptyList();
        }

        Location     newLocation     = resolver.optionalLocation(dto.getLocationId());
        Collaborator newCollaborator = resolver.optionalCollaborator(dto.getCollaboratorId());
        Location     oldLocation     = computer.getLocation();
        Collaborator oldCollaborator = computer.getCollaborator();

        // ── Validações de negócio ──────────────────────────────────────

        if (newLocation != null && newCollaborator != null) {
            throw new BusinessRuleException(
                "Operação não permitida: um ativo não pode ser alocado para um colaborador e uma localização ao mesmo tempo.");
        }

        if (oldCollaborator != null && newLocation != null) {
            throw new BusinessRuleException(
                "Operação não permitida: não é possível alterar a localização enquanto o ativo está alocado a um colaborador. Devolva ao estoque primeiro.");
        }

        if (oldLocation != null && newCollaborator != null) {
            throw new BusinessRuleException(
                "Operação não permitida: não é possível alterar o colaborador enquanto o ativo está alocado a uma localização. Devolva ao estoque primeiro.");
        }

        // ── Mutação de estado ──────────────────────────────────────────

        computer.setLocation(newLocation);
        computer.setCollaborator(newCollaborator);
        computer.setStatus(resolveStatus(newLocation, newCollaborator, computer.getStatus()));

        // ── Coleta de eventos ──────────────────────────────────────────

        List<Runnable> events = new ArrayList<>();

        if (!Objects.equals(oldLocation, newLocation)) {
            if (newLocation != null)
                events.add(() -> assetHistoryService.registerEvent(computer, HistoryEventType.ALOCACAO,
                        "Ativo alocado para a localização PA: " + newLocation.getPaNumber(), null));
            if (oldLocation != null)
                events.add(() -> assetHistoryService.registerEvent(computer, HistoryEventType.DEVOLUCAO,
                        "Ativo devolvido da localização PA: " + oldLocation.getPaNumber(), null));
        }

        if (!Objects.equals(oldCollaborator, newCollaborator)) {
            if (newCollaborator != null)
                events.add(() -> assetHistoryService.registerEvent(computer, HistoryEventType.ALOCACAO,
                        "Ativo alocado para o colaborador: " + newCollaborator.getName(), null));
            if (oldCollaborator != null)
                events.add(() -> assetHistoryService.registerEvent(computer, HistoryEventType.DEVOLUCAO,
                        "Ativo devolvido pelo colaborador: " + oldCollaborator.getName(), null));
        }

        return events;
    }

    private AssetStatus resolveStatus(Location location, Collaborator collaborator, AssetStatus current) {
        if (location != null || collaborator != null) {
            return AssetStatus.EM_USO;
        }
        // não sobrescreve MANUTENÇÃO ou outros status — só regride EM_USO para EM_ESTOQUE
        return current == AssetStatus.EM_USO ? AssetStatus.EM_ESTOQUE : current;
    }
}
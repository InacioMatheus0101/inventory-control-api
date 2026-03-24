package com.matheuss.controle_estoque_api.service;

import com.matheuss.controle_estoque_api.domain.*;
import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.domain.enums.EquipmentState;
import com.matheuss.controle_estoque_api.domain.history.HistoryEventType;
import com.matheuss.controle_estoque_api.dto.BulkCreateRequestDTO;
import com.matheuss.controle_estoque_api.exception.BusinessRuleException;
import com.matheuss.controle_estoque_api.exception.ResourceAlreadyExistsException;
import com.matheuss.controle_estoque_api.mapper.ComputerMapper;
import com.matheuss.controle_estoque_api.mapper.ComponentMapper;
import com.matheuss.controle_estoque_api.repository.AssetRepository;
import com.matheuss.controle_estoque_api.service.support.EntityResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetBulkService {

    private final AssetRepository assetRepository;
    private final EntityResolver resolver;
    private final AssetHistoryService assetHistoryService;
    private final ComputerMapper computerMapper;
    private final ComponentMapper componentMapper;

    @Transactional
    public List<?> bulkCreate(BulkCreateRequestDTO dto) {
        if (dto.getLocationId() != null && dto.getCollaboratorId() != null) {
            throw new BusinessRuleException("Não é possível realizar a alocação inicial para um local e um colaborador simultaneamente.");
        }

        validateUniqueness(dto.getAssets());

        Category category = resolver.requireCategory(dto.getCategoryId());
        Location destinationLocation = resolver.optionalLocation(dto.getLocationId());
        Collaborator destinationCollaborator = resolver.optionalCollaborator(dto.getCollaboratorId());

        List<Asset> newAssets = new ArrayList<>();
        for (BulkCreateRequestDTO.AssetIndividualData individualData : dto.getAssets()) {
            Asset newAsset = createAndPopulateAsset(dto, individualData, category, destinationLocation, destinationCollaborator);
            newAssets.add(newAsset);
        }

        assetRepository.saveAll(newAssets);

        for (Asset savedAsset : newAssets) {
            String creationDetails = "Ativo criado em lote. NF: " + (dto.getInvoiceNumber() != null ? dto.getInvoiceNumber() : "N/A");
            assetHistoryService.registerEvent(savedAsset, HistoryEventType.CRIACAO, creationDetails, dto.getTicketNumber(), null);

            if (destinationCollaborator != null) {
                String allocationDetails = "Alocação inicial para: " + destinationCollaborator.getName();
                assetHistoryService.registerEvent(savedAsset, HistoryEventType.ALOCACAO, allocationDetails, dto.getTicketNumber(), null);
            }
        }

        return mapToResponseDTOs(newAssets);
    }

    private void validateUniqueness(List<BulkCreateRequestDTO.AssetIndividualData> assets) {
        List<String> patrimonios = assets.stream()
                .map(BulkCreateRequestDTO.AssetIndividualData::getPatrimonio)
                .collect(Collectors.toList());
        if (assetRepository.existsByPatrimonioIn(patrimonios)) {
            throw new ResourceAlreadyExistsException("Um ou mais números de patrimônio fornecidos já existem no sistema.");
        }
             List<String> serialNumbers = assets.stream()
                .map(BulkCreateRequestDTO.AssetIndividualData::getSerialNumber)
                .collect(Collectors.toList());
        if (assetRepository.existsBySerialNumberIn(serialNumbers)) {
            throw new ResourceAlreadyExistsException("Um ou mais números de série fornecidos já existem no sistema.");
        }
    
    }

    private Asset createAndPopulateAsset(BulkCreateRequestDTO dto, BulkCreateRequestDTO.AssetIndividualData individualData, Category category, Location location, Collaborator collaborator) {
        Asset newAsset;

        switch (dto.getAssetType()) {
            case COMPUTER: {
                Computer newComputer = new Computer();
                newComputer.setHostname(individualData.getHostname());
                newComputer.setSerialNumber(individualData.getSerialNumber());
                newAsset = newComputer;
                break;
            }
            case COMPONENT: {
                Component newComponent = new Component();
                newComponent.setName(individualData.getHostname());
                newComponent.setModel(individualData.getModel());
                newComponent.setSerialNumber(individualData.getSerialNumber());
                newAsset = newComponent;
                break;
            }
            default:
                throw new BusinessRuleException("Tipo de ativo não suportado: " + dto.getAssetType());
        }

        newAsset.setPatrimonio(individualData.getPatrimonio());
        newAsset.setAssetTag(individualData.getAssetTag());
        newAsset.setCategory(category);
        newAsset.setNf(dto.getInvoiceNumber());
        newAsset.setPedido(dto.getPurchaseOrder());
        newAsset.setChamadoCompra(dto.getPurchaseTicket());
        newAsset.setSc(dto.getPurchaseTicket());
        
        try {
            if (dto.getAcquisitionDate() != null && !dto.getAcquisitionDate().isBlank()) {
                LocalDate date = LocalDate.parse(dto.getAcquisitionDate());
                newAsset.setPurchaseDate(date);
                newAsset.setDataRecebimento(date);
            }
        } catch (DateTimeParseException e) {
            throw new BusinessRuleException("Formato de data inválido para 'acquisitionDate'. Use o formato AAAA-MM-DD.");
        }

        if (collaborator != null) {
            newAsset.setStatus(AssetStatus.EM_USO);
            newAsset.setCollaborator(collaborator);
        } else {
            newAsset.setStatus(AssetStatus.EM_ESTOQUE);
            newAsset.setLocation(location);
        }
        
        newAsset.setEquipmentState(EquipmentState.NOVO);

        return newAsset;
    }

    private List<?> mapToResponseDTOs(List<Asset> assets) {
        if (assets.isEmpty()) {
            return List.of();
        }

        Asset firstAsset = assets.get(0);
        if (firstAsset instanceof Computer) {
            return assets.stream().map(asset -> computerMapper.toResponseDTO((Computer) asset)).collect(Collectors.toList());
        }
        if (firstAsset instanceof Component) {
            return assets.stream().map(asset -> componentMapper.toResponseDTO((Component) asset)).collect(Collectors.toList());
        }
        return List.of();
    }
}

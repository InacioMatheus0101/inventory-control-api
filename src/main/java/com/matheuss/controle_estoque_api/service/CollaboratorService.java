package com.matheuss.controle_estoque_api.service;

import com.matheuss.controle_estoque_api.domain.Collaborator;
import com.matheuss.controle_estoque_api.dto.CollaboratorCreateDTO;
import com.matheuss.controle_estoque_api.dto.CollaboratorResponseDTO;
import com.matheuss.controle_estoque_api.dto.CollaboratorUpdateDTO;
import com.matheuss.controle_estoque_api.exception.BusinessRuleException; // Import da nova exceção
import com.matheuss.controle_estoque_api.mapper.CollaboratorMapper;
import com.matheuss.controle_estoque_api.repository.CollaboratorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollaboratorService {

    private final CollaboratorRepository collaboratorRepository;
    private final CollaboratorMapper collaboratorMapper;

    @Transactional
    public CollaboratorResponseDTO create(CollaboratorCreateDTO dto) {
        Collaborator entity = collaboratorMapper.toEntity(dto);
        Collaborator saved = collaboratorRepository.save(entity);
        return collaboratorMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<CollaboratorResponseDTO> getAll() {
        return collaboratorRepository.findAll().stream()
                .map(collaboratorMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CollaboratorResponseDTO getById(Long id) {
        return collaboratorRepository.findById(id)
                .map(collaboratorMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Colaborador não encontrado com o ID: " + id));
    }

    @Transactional
    public CollaboratorResponseDTO update(Long id, CollaboratorUpdateDTO dto) {
        Collaborator entity = collaboratorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Colaborador não encontrado com o ID: " + id));

        collaboratorMapper.updateEntityFromDto(dto, entity);

        Collaborator updated = collaboratorRepository.save(entity);
        return collaboratorMapper.toResponseDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        Collaborator collaborator = collaboratorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Colaborador não encontrado com o ID: " + id));

        // VERIFICAÇÃO DE SEGURANÇA: O colaborador possui ativos?
        if (collaborator.getAssets() != null && !collaborator.getAssets().isEmpty()) {
            // Lança a exceção de regra de negócio
            throw new BusinessRuleException("Não é possível deletar o colaborador '" + collaborator.getName() + "' pois ele possui " + collaborator.getAssets().size() + " ativo(s) alocado(s).");
        }

        collaboratorRepository.deleteById(id);
    }
}

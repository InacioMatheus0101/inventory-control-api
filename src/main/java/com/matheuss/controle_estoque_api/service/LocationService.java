package com.matheuss.controle_estoque_api.service;

import com.matheuss.controle_estoque_api.domain.Location;
import com.matheuss.controle_estoque_api.dto.LocationCreateDTO;
import com.matheuss.controle_estoque_api.dto.LocationResponseDTO;
import com.matheuss.controle_estoque_api.dto.LocationUpdateDTO;
import com.matheuss.controle_estoque_api.exception.BusinessRuleException;
import com.matheuss.controle_estoque_api.mapper.LocationMapper;
import com.matheuss.controle_estoque_api.repository.LocationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @Transactional
    public LocationResponseDTO createLocation(LocationCreateDTO dto) {
        Location entity = locationMapper.toEntity(dto);
        Location saved = locationRepository.save(entity);
        return locationMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<LocationResponseDTO> findAllLocations(Pageable pageable) {
        return locationRepository.findAll(pageable)
                .map(locationMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<LocationResponseDTO> findAllLocations() {
        return locationRepository.findAll().stream()
                .map(locationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LocationResponseDTO findLocationById(Long id) {
        return locationRepository.findById(id)
                .map(locationMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Localização não encontrada com o ID: " + id));
    }

    @Transactional
    public LocationResponseDTO updateLocation(Long id, LocationUpdateDTO dto) {
        Location existing = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Localização não encontrada com o ID: " + id));
        
        locationMapper.updateEntityFromDto(dto, existing);
        Location saved = locationRepository.save(existing);
        return locationMapper.toResponseDTO(saved);
    }

    @Transactional
    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Localização não encontrada com o ID: " + id));

        if (location.getAssets() != null && !location.getAssets().isEmpty()) {
            throw new BusinessRuleException(
                "Operação não permitida: A localização PA " + location.getPaNumber() + 
                " não pode ser deletada pois possui " + location.getAssets().size() + " ativo(s) associado(s)."
            );
        }

        locationRepository.delete(location);
    }
}

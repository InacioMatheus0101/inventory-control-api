package com.matheuss.controle_estoque_api.controller;

import com.matheuss.controle_estoque_api.dto.LocationCreateDTO;
import com.matheuss.controle_estoque_api.dto.LocationResponseDTO;
import com.matheuss.controle_estoque_api.dto.LocationUpdateDTO;
import com.matheuss.controle_estoque_api.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import necessário
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/locations" )
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<LocationResponseDTO> createLocation(@Valid @RequestBody LocationCreateDTO dto) {
        LocationResponseDTO createdLocation = locationService.createLocation(dto);
        URI locationUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdLocation.getId()).toUri();
        return ResponseEntity.created(locationUri).body(createdLocation);
    }

    @GetMapping
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<List<LocationResponseDTO>> getAllLocations() {
        return ResponseEntity.ok(locationService.findAllLocations());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<LocationResponseDTO> getLocationById(@PathVariable("id") Long id) {
        LocationResponseDTO location = locationService.findLocationById(id);
        return ResponseEntity.ok(location);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<LocationResponseDTO> updateLocation(@PathVariable("id") Long id, @Valid @RequestBody LocationUpdateDTO dto) {
        LocationResponseDTO updatedLocation = locationService.updateLocation(id, dto);
        return ResponseEntity.ok(updatedLocation);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Void> deleteLocation(@PathVariable("id") Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}

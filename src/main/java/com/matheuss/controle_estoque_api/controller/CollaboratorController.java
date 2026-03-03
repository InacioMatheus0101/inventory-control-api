package com.matheuss.controle_estoque_api.controller;

import com.matheuss.controle_estoque_api.dto.CollaboratorCreateDTO;
import com.matheuss.controle_estoque_api.dto.CollaboratorResponseDTO;
import com.matheuss.controle_estoque_api.dto.CollaboratorUpdateDTO;
import com.matheuss.controle_estoque_api.service.CollaboratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import necessário
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/collaborators" )
@RequiredArgsConstructor
public class CollaboratorController {

    private final CollaboratorService collaboratorService;

    @PostMapping
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<CollaboratorResponseDTO> create(@RequestBody @Valid CollaboratorCreateDTO dto) {
        CollaboratorResponseDTO created = collaboratorService.create(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<List<CollaboratorResponseDTO>> getAll() {
        return ResponseEntity.ok(collaboratorService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<CollaboratorResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(collaboratorService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<CollaboratorResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid CollaboratorUpdateDTO dto
    ) {
        return ResponseEntity.ok(collaboratorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        collaboratorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

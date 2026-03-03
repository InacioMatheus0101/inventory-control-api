package com.matheuss.controle_estoque_api.controller;

import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.dto.ComponentCreateDTO;
import com.matheuss.controle_estoque_api.dto.ComponentResponseDTO;
import com.matheuss.controle_estoque_api.dto.ComponentUpdateDTO;
import com.matheuss.controle_estoque_api.service.ComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/components" )
@Tag(name = "Components")
@RequiredArgsConstructor
public class ComponentController {

    private final ComponentService componentService;

    @Operation(summary = "Cria um novo componente")
    @PostMapping
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<ComponentResponseDTO> createComponent(@RequestBody @Valid ComponentCreateDTO dto) {
        ComponentResponseDTO createdComponent = componentService.createComponent(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdComponent.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdComponent);
    }

    @GetMapping
    @Operation(summary = "Lista componentes com paginação, ordenação e filtros")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Page<ComponentResponseDTO>> getAllComponents(
            @RequestParam(required = false) AssetStatus status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String name,
            Pageable pageable) {
        
        Page<ComponentResponseDTO> componentsPage = componentService.getAllComponents(status, type, name, pageable);
        return ResponseEntity.ok(componentsPage);
    }

    @Operation(summary = "Busca um componente por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<ComponentResponseDTO> getComponentById(@PathVariable Long id) {
        return ResponseEntity.ok(componentService.getComponentById(id));
    }

    @Operation(summary = "Atualiza um componente por ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<ComponentResponseDTO> updateComponent(@PathVariable Long id, @RequestBody @Valid ComponentUpdateDTO dto) {
        return ResponseEntity.ok(componentService.updateComponent(id, dto));
    }

    @Operation(summary = "Instala um componente em um computador")
    @PatchMapping("/{componentId}/install/{computerId}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<ComponentResponseDTO> installComponent(
            @PathVariable Long componentId,
            @PathVariable Long computerId) {
        ComponentResponseDTO updatedComponent = componentService.installComponent(componentId, computerId);
        return ResponseEntity.ok(updatedComponent);
    }
}

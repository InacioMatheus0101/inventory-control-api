package com.matheuss.controle_estoque_api.controller;

import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.dto.PeripheralCreateDTO;
import com.matheuss.controle_estoque_api.dto.PeripheralResponseDTO;
import com.matheuss.controle_estoque_api.dto.PeripheralUpdateDTO;
import com.matheuss.controle_estoque_api.service.PeripheralService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import necessário
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/peripherals" )
@RequiredArgsConstructor
public class PeripheralController {

    private final PeripheralService peripheralService;

    @PostMapping
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<PeripheralResponseDTO> createPeripheral(@RequestBody @Valid PeripheralCreateDTO dto) {
        PeripheralResponseDTO createdPeripheral = peripheralService.createPeripheral(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPeripheral.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdPeripheral);
    }

    @GetMapping
    @Operation(summary = "Lista periféricos com paginação, ordenação e filtros")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Page<PeripheralResponseDTO>> getAllPeripherals(
            @RequestParam(required = false) AssetStatus status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String name,
            Pageable pageable) {
        
        Page<PeripheralResponseDTO> peripheralsPage = peripheralService.getAllPeripherals(status, type, name, pageable);
        return ResponseEntity.ok(peripheralsPage);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<PeripheralResponseDTO> getPeripheralById(@PathVariable Long id) {
        return ResponseEntity.ok(peripheralService.getPeripheralById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<PeripheralResponseDTO> updatePeripheral(@PathVariable Long id, @RequestBody @Valid PeripheralUpdateDTO dto) {
        return ResponseEntity.ok(peripheralService.updatePeripheral(id, dto));
    }
}

package com.matheuss.controle_estoque_api.controller;

import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.dto.PeripheralCreateDTO;
import com.matheuss.controle_estoque_api.dto.PeripheralResponseDTO;
import com.matheuss.controle_estoque_api.dto.PeripheralUpdateDTO;
import com.matheuss.controle_estoque_api.service.PeripheralService;
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
@RequestMapping("/api/peripherals")
@RequiredArgsConstructor
@Tag(name = "Periféricos", description = "Gerenciamento de periféricos do estoque")
public class PeripheralController {

    private final PeripheralService peripheralService;

    @PostMapping
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN')")
    @Operation(summary = "Cadastra um novo periférico")
    public ResponseEntity<PeripheralResponseDTO> createPeripheral(
            @RequestBody @Valid PeripheralCreateDTO dto) {

        PeripheralResponseDTO createdPeripheral = peripheralService.createPeripheral(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPeripheral.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdPeripheral);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Lista periféricos com paginação, ordenação e filtros")
    public ResponseEntity<Page<PeripheralResponseDTO>> getAllPeripherals(
            @RequestParam(required = false) AssetStatus status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String patrimonio,
            Pageable pageable) {

        return ResponseEntity.ok(
                peripheralService.getAllPeripherals(status, type, name, patrimonio, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Busca um periférico pelo ID")
    public ResponseEntity<PeripheralResponseDTO> getPeripheralById(@PathVariable Long id) {
        return ResponseEntity.ok(peripheralService.getPeripheralById(id));
    }

    // PATCH com suporte a desalocação explícita via sentinela:
    // Enviar { "computerId": 0 } remove o vínculo com o computador atual.
    // Omitir o campo (null) mantém o valor existente sem alteração.
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN')")
    @Operation(
        summary = "Atualiza parcialmente um periférico",
        description = """
            Atualiza os dados do periférico. Campos omitidos (null) mantêm o valor atual.
            Para remover um vínculo de alocação, envie o ID correspondente com valor 0.
            Exemplo: { "computerId": 0 } desvincula o computador atual.
            """
    )
    public ResponseEntity<PeripheralResponseDTO> updatePeripheral(
            @PathVariable Long id,
            @RequestBody @Valid PeripheralUpdateDTO dto) {

        return ResponseEntity.ok(peripheralService.updatePeripheral(id, dto));
    }
}
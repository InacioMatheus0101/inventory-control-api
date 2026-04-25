package com.matheuss.controle_estoque_api.controller;

import com.matheuss.controle_estoque_api.dto.AssetSimpleResponseDTO;
import com.matheuss.controle_estoque_api.service.AssetAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets" )
@RequiredArgsConstructor
public class AssetAllocationController {

    private final AssetAllocationService assetAllocationService;

    // ✅ CORRIGIDO: Retorna AssetSimpleResponseDTO em vez de Void
    @PatchMapping("/{assetId}/assign/{collaboratorId}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<AssetSimpleResponseDTO> assignToCollaborator(
            @PathVariable Long assetId,
            @PathVariable Long collaboratorId
    ) {
        AssetSimpleResponseDTO result = assetAllocationService.assignToCollaborator(assetId, collaboratorId);
        return ResponseEntity.ok(result);
    }

    // ✅ CORRIGIDO: Retorna AssetSimpleResponseDTO em vez de Void
    @PatchMapping("/{assetId}/assign-location/{locationId}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<AssetSimpleResponseDTO> assignToLocation(
            @PathVariable Long assetId,
            @PathVariable Long locationId
    ) {
        AssetSimpleResponseDTO result = assetAllocationService.assignToLocation(assetId, locationId);
        return ResponseEntity.ok(result);
    }

    // ✅ CORRIGIDO: Retorna AssetSimpleResponseDTO em vez de Void
    @PatchMapping("/{assetId}/unassign")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<AssetSimpleResponseDTO> unassignToStock(@PathVariable Long assetId) {
        AssetSimpleResponseDTO result = assetAllocationService.unassignToStock(assetId);
        return ResponseEntity.ok(result);
    }

    // ✅ CORRIGIDO: Retorna AssetSimpleResponseDTO em vez de Void
    @PatchMapping("/{assetId}/dispose")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<AssetSimpleResponseDTO> disposeAsset(@PathVariable Long assetId) {
        AssetSimpleResponseDTO result = assetAllocationService.disposeAsset(assetId);
        return ResponseEntity.ok(result);
    }
}

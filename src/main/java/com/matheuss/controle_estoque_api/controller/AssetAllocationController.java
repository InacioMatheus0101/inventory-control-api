package com.matheuss.controle_estoque_api.controller;

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

    // Alocar para colaborador (Home office / empréstimo)
    @PatchMapping("/{assetId}/assign/{collaboratorId}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Void> assignToCollaborator(
            @PathVariable Long assetId,
            @PathVariable Long collaboratorId
    ) {
        assetAllocationService.assignToCollaborator(assetId, collaboratorId);
        return ResponseEntity.ok().build();
    }

    // Alocar para localização (PA)
    @PatchMapping("/{assetId}/assign-location/{locationId}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Void> assignToLocation(
            @PathVariable Long assetId,
            @PathVariable Long locationId
    ) {
        assetAllocationService.assignToLocation(assetId, locationId);
        return ResponseEntity.ok().build();
    }

    // Devolver para estoque
    @PatchMapping("/{assetId}/unassign")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Void> unassignToStock(@PathVariable Long assetId) {
        assetAllocationService.unassignToStock(assetId);
        return ResponseEntity.ok().build();
    }

    // Novo endpoint para descarte (soft delete)
    @PatchMapping("/{assetId}/dispose")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Void> disposeAsset(@PathVariable Long assetId) {
        assetAllocationService.disposeAsset(assetId);
        return ResponseEntity.ok().build();
    }
}

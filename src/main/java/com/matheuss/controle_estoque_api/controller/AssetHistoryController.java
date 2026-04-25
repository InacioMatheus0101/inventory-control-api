package com.matheuss.controle_estoque_api.controller;

import com.matheuss.controle_estoque_api.dto.AssetHistoryResponseDTO;
import com.matheuss.controle_estoque_api.service.AssetHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetHistoryController {

    private final AssetHistoryService assetHistoryService;

    @GetMapping("/{assetId}/history")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Page<AssetHistoryResponseDTO>> getHistoryByAssetId(
            @PathVariable Long assetId,
            Pageable pageable) {
        Page<AssetHistoryResponseDTO> history = assetHistoryService.getHistoryByAssetId(assetId, pageable);
        return ResponseEntity.ok(history);
    }
}

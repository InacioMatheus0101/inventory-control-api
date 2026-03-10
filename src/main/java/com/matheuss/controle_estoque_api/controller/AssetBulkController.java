package com.matheuss.controle_estoque_api.controller;

import com.matheuss.controle_estoque_api.dto.BulkCreateRequestDTO;
import com.matheuss.controle_estoque_api.service.AssetBulkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import necessário
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assets/bulk" )
@RequiredArgsConstructor
public class AssetBulkController {

    private final AssetBulkService assetBulkService;
    
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')") 
    public ResponseEntity<List<?>> bulkCreateAssets(@Valid @RequestBody BulkCreateRequestDTO request) {
        List<?> createdAssets = assetBulkService.bulkCreate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAssets);
    }
}

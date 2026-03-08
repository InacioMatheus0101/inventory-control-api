package com.matheuss.controle_estoque_api.controller;

import com.matheuss.controle_estoque_api.dto.BulkCreateRequestDTO;
import com.matheuss.controle_estoque_api.service.AssetBulkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /**
     * Endpoint para a criação de múltiplos ativos (Computadores, Componentes, Periféricos) em uma única requisição.
     * Ideal para cenários de importação de planilhas ou cadastro de novas compras.
     * @param request O DTO contendo os dados do lote e a lista de ativos individuais.
     * @return Uma lista dos ativos recém-criados.
     */
    
    @PostMapping("/create")
    public ResponseEntity<List<?>> bulkCreateAssets(@Valid @RequestBody BulkCreateRequestDTO request) {
        List<?> createdAssets = assetBulkService.bulkCreate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAssets);
    }
}

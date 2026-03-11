package com.matheuss.controle_estoque_api.controller;

import com.matheuss.controle_estoque_api.dto.KpiResponseDTO;
import com.matheuss.controle_estoque_api.service.DashboardStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller responsável por expor endpoints de estatísticas para o Dashboard.
 */

@RestController
@RequestMapping("/api/stats" ) 
public class DashboardStatsController {

    @Autowired
    private DashboardStatsService dashboardStatsService;

    /*
     * Endpoint para buscar a contagem de ativos agrupada por status.
     * Utilizado para alimentar gráficos no frontend.
     */

    @GetMapping("/assets-by-status")
    public ResponseEntity<Map<String, Long>> getAssetCountByStatus() {
        Map<String, Long> assetCounts = dashboardStatsService.getAssetCountByStatus();
        return ResponseEntity.ok(assetCounts);
    }

    /**
     * Endpoint para buscar os principais KPIs (Key Performance Indicators) do sistema.
     * Utilizado para alimentar os cards de resumo no dashboard.
     */

    @GetMapping("/kpis")
    public ResponseEntity<KpiResponseDTO> getKpis() {
        KpiResponseDTO kpis = dashboardStatsService.getKpis(); 
        return ResponseEntity.ok(kpis);
    }
}

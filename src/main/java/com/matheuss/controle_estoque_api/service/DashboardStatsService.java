package com.matheuss.controle_estoque_api.service;


import com.matheuss.controle_estoque_api.repository.AssetRepository;
import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.dto.KpiResponseDTO; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardStatsService {

    @Autowired
    private AssetRepository assetRepository;

    public Map<String, Long> getAssetCountByStatus() {
        return assetRepository.countByStatus().stream()
                .collect(Collectors.toMap(
                        result -> ((AssetStatus) result[0]).name(),
                        result -> (Long) result[1]
                ));
    }

    public KpiResponseDTO getKpis() {
        long totalAssets = assetRepository.count();
        long assignedAssets = assetRepository.countByStatus(AssetStatus.EM_USO);
        long availableAssets = assetRepository.countByStatus(AssetStatus.EM_ESTOQUE);
        long inMaintenanceAssets = assetRepository.countByStatus(AssetStatus.EM_MANUTENCAO);
        long discardedAssets = assetRepository.countByStatus(AssetStatus.DESCARTADO);

        return new KpiResponseDTO(
            totalAssets,
            assignedAssets,
            availableAssets,
            inMaintenanceAssets,
            discardedAssets
        );
    }
}

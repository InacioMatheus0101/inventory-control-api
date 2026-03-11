package com.matheuss.controle_estoque_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transportar os dados dos Key Performance Indicators (KPIs) para o dashboard.
 */

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class KpiResponseDTO {

    private long totalAssets;
    private long assignedAssets;
    private long availableAssets;
    private long inMaintenanceAssets;
    private long discardedAssets;
}

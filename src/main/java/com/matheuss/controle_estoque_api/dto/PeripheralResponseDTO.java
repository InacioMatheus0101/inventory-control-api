package com.matheuss.controle_estoque_api.dto;

import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.domain.enums.EquipmentState;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PeripheralResponseDTO {

    private Long id;
    private String assetTag;
    private String patrimonio;
    private AssetStatus status;
    private EquipmentState equipmentState;
    private LocalDate purchaseDate;
    private String notes;

    // ✅ CORREÇÃO item 5: collaboratorId mantido mas precisa de @Mapping explícito no mapper
    private Long collaboratorId;

    // CAMPOS ADMINISTRATIVOS (EXCEL)
    private LocalDate dataRecebimento;
    private String chamadoCompra;
    private String sc;
    private String pedido;
    private String nf;
    private String centroCusto;

    // JIRA (CONTROLE)
    private String ticketJira;
    private String ticketDevolucaoJira;

    private String type;
    private String name;
    private String model;
    private String serialNumber;

    // ✅ CORREÇÃO item 4: adicionados campos de auditoria presentes na entidade Asset
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocationResponseDTO location;
    private ComputerSimpleResponseDTO computer;
    private CollaboratorSimpleResponseDTO user;
}
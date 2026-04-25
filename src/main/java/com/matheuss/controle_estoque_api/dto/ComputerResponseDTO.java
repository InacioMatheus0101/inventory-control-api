package com.matheuss.controle_estoque_api.dto;

import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.domain.enums.EquipmentState;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ComputerResponseDTO {

    private Long id;
    private String assetTag;
    private AssetStatus status;
    private EquipmentState equipmentState;
    private LocalDate purchaseDate;
    private String notes;
    private String patrimonio;
    private String hostname;
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

    // RELACIONAMENTOS
    private LocationResponseDTO location;
    private CategoryResponseDTO category;
    private List<ComponentResponseDTO> components;

    // CAMPOS ESPECÍFICOS
    private String nameComputer;
    private String serialNumber;
    private String cpu;
    private int ramSizeInGB;
    private int storageSizeInGB;
    private String os;

    // AUDITORIA
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // COLABORADOR
    private CollaboratorSimpleResponseDTO user;

    // HISTÓRICO
    private List<AssetHistoryResponseDTO> history;
}

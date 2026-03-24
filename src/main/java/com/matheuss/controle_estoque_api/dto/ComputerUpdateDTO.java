package com.matheuss.controle_estoque_api.dto;

import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.domain.enums.EquipmentState;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ComputerUpdateDTO {


    private String hostname;
    private String nameComputer;
    private String assetTag;
    private String patrimonio;
    private AssetStatus status; 
    private LocalDate purchaseDate;
    private Long categoryId;
    private Long locationId;
    private Long collaboratorId;

    // Campos específicos do Computador
    private String serialNumber;
    private String cpu;
    private Integer ramSizeInGB; 
    private Integer storageSizeInGB; 
    private String os;

  
    private EquipmentState equipmentState;
    private String notes;

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
}

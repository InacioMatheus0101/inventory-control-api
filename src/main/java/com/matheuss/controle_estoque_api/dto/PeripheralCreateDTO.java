package com.matheuss.controle_estoque_api.dto;

import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.domain.enums.EquipmentState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PeripheralCreateDTO {

    private String patrimonio;
    private String assetTag;

    // Pode vir nulo (backend define EM_ESTOQUE)
    private AssetStatus status;

    @NotNull(message = "O estado do equipamento é obrigatório.")
    private EquipmentState equipmentState;

    private LocalDate purchaseDate;

    private Long locationId;
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

    // CAMPOS ESPECÍFICOS
    @NotBlank(message = "O tipo do periférico é obrigatório.")
    private String type;

    @NotBlank(message = "O nome do periférico é obrigatório.")
    private String name;

    private String model;

    @NotBlank(message = "O número de série é obrigatório.")
    private String serialNumber;

    private Long computerId;
}

package com.matheuss.controle_estoque_api.dto;

import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.domain.enums.EquipmentState;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public abstract class BaseAssetUpdateDTO {

    // ── Identificação ──────────────────────────────────────────────
    private String assetTag;
    private String patrimonio;

    // ── Status e estado ────────────────────────────────────────────
    private AssetStatus status;
    private EquipmentState equipmentState;

    // ── Datas ──────────────────────────────────────────────────────
    private LocalDate purchaseDate;

    // ── Vínculos ───────────────────────────────────────────────────
    private Long categoryId;
    private Long locationId;
    private Long collaboratorId;

    // ── Observações ────────────────────────────────────────────────
    private String notes;

    // ── Campos administrativos ─────────────────────────────────────
    private LocalDate dataRecebimento;
    private String chamadoCompra;
    private String sc;
    private String pedido;
    private String nf;
    private String centroCusto;

    // ── Jira ───────────────────────────────────────────────────────
    private String ticketJira;
    private String ticketDevolucaoJira;
}
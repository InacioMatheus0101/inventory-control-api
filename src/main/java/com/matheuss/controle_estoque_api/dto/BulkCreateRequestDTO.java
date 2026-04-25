package com.matheuss.controle_estoque_api.dto;

import com.matheuss.controle_estoque_api.domain.enums.AssetType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BulkCreateRequestDTO {

    @NotNull(message = "O tipo do ativo é obrigatório (COMPUTER, COMPONENT, PERIPHERAL).")
    private AssetType assetType;

    @NotNull(message = "O ID da categoria é obrigatório.")
    private Long categoryId;

    // ── Informações da compra (opcionais) ──────────────────────────
    private String acquisitionDate;
    private String invoiceNumber;
    private String purchaseOrder;
    private String purchaseTicket;
    private String ticketNumber;

    // ── Alocação imediata (opcional) ───────────────────────────────
    private Long locationId;
    private Long collaboratorId;

    @Valid
    @NotNull(message = "A lista de ativos não pode ser nula.")
    private List<AssetIndividualData> assets;

    @Data
    public static class AssetIndividualData {

        @NotNull(message = "O patrimônio é obrigatório.")
        private String patrimonio;

        @NotNull(message = "O número de série é obrigatório.")
        private String serialNumber;

        // ── opcional, apenas Computer utiliza ──────────────────────
        private String hostname;

        private String model;
        private String assetTag;
    }
}
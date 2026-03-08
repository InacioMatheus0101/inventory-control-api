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

    // Informações da Compra (Opcionais)
    private String acquisitionDate;
    private String invoiceNumber;
    private String purchaseOrder;
    private String purchaseTicket; // Chamado da Compra / SC
    
    private String ticketNumber; // O "chamado" que nós do TI fazemos

    // Dados de Alocação Imediata (Opcional)
    private Long locationId;
    private Long collaboratorId;

    @Valid
    @NotNull(message = "A lista de ativos não pode ser nula.")
    private List<AssetIndividualData> assets;

    @Data
    public static class AssetIndividualData {
        // OBRIGATÓRIOS
        @NotNull(message = "O patrimônio é obrigatório.")
        private String patrimonio;

        @NotNull(message = "O número de série é obrigatório.")
        private String serialNumber;

        @NotNull(message = "O modelo é obrigatório.")
        private String model; // Usaremos para preencher 'name' ou 'model'

        // OPCIONAL
        private String assetTag;
    }
}

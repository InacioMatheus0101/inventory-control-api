package com.matheuss.controle_estoque_api.service.validator;

import com.matheuss.controle_estoque_api.domain.Collaborator;
import com.matheuss.controle_estoque_api.domain.Computer;
import com.matheuss.controle_estoque_api.domain.Location;
import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.domain.enums.EquipmentState;
import com.matheuss.controle_estoque_api.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

@Component
public class AssetValidator {

    public void validateState(EquipmentState state, AssetStatus status) {

        if (state == null || status == null) return;

        switch (state) {

            case NOVO:
            case USADO:
            case REPARADO:
                // Pode estar em uso ou estoque
                break;

            case DANIFICADO:
                if (status != AssetStatus.EM_MANUTENCAO) {
                    throw new BusinessRuleException(
                            "Equipamento DANIFICADO deve estar EM_MANUTENCAO.");
                }
                break;

            case DESCARTADO:
                if (status != AssetStatus.DESCARTADO) {
                    throw new BusinessRuleException(
                            "Equipamento DESCARTADO deve possuir status DESCARTADO.");
                }
                break;
        }
    }

    public void validateAllocationConsistency(
            AssetStatus status,
            Location location,
            Collaborator collaborator,
            Computer computer
    ) {

        boolean hasLink = location != null || collaborator != null || computer != null;

        if (hasLink && status == AssetStatus.EM_ESTOQUE) {
            throw new BusinessRuleException(
                    "Ativo vinculado não pode estar EM_ESTOQUE.");
        }

        if ((status == AssetStatus.DESCARTADO || status == AssetStatus.EM_MANUTENCAO) && hasLink) {
            throw new BusinessRuleException(
                    "Ativo neste status não pode possuir vínculos.");
        }
    }
}
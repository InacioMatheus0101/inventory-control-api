package com.matheuss.controle_estoque_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComponentUpdateDTO extends BaseAssetUpdateDTO {

    // ── Específicos do Component ───────────────────────────────────
    private String name;
    private String model;
    private String serialNumber;
    private Long computerId;
}
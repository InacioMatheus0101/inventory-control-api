package com.matheuss.controle_estoque_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PeripheralUpdateDTO extends BaseAssetUpdateDTO {

    // ── Específicos do Peripheral ──────────────────────────────────
    private String type;
    private String name;
    private String model;
    private String serialNumber;
    private Long computerId;
}
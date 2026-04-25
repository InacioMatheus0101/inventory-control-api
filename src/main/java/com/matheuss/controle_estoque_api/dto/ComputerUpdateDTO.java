package com.matheuss.controle_estoque_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComputerUpdateDTO extends BaseAssetUpdateDTO {

    // ── Específicos do Computer ────────────────────────────────────
    private String hostname;
    private String nameComputer;
    private String serialNumber;
    private String cpu;
    private Integer ramSizeInGB;
    private Integer storageSizeInGB;
    private String os;
}
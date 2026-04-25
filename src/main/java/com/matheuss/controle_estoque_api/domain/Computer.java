package com.matheuss.controle_estoque_api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.NotAudited;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@DiscriminatorValue("COMPUTER")
@Getter
@Setter
@ToString(callSuper = true, exclude = {"components"})
public class Computer extends Asset {
    
    private String hostname;
    private String serialNumber;
    private String cpu;
    private Integer ramSizeInGB;
    private Integer storageSizeInGB;
    private String os;
    private String nameComputer;

    @OneToMany(mappedBy = "computer", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true)
    @JsonIgnore
    @NotAudited
    private List<Component> components = new ArrayList<>();
}

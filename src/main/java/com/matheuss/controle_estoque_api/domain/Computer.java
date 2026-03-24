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
@ToString(callSuper = true, exclude = {"components"}) // Herda o toString() de Asset e exclui a lista de componentes
public class Computer extends Asset {
    

    private String hostname;
    private String serialNumber;
    private String cpu;
    private int ramSizeInGB;
    private int storageSizeInGB;
    private String os;
    private String nameComputer;

    // A RELAÇÃO COM CATEGORY FOI REMOVIDA DAQUI.
    // ELA AGORA EXISTE APENAS NA CLASSE MÃE 'Asset'.

    @OneToMany(mappedBy = "computer", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true)
    @JsonIgnore
    @NotAudited
    private List<Component> components = new ArrayList<>();

    // A implementação de equals() e hashCode() é herdada de Asset,
    // então não precisamos reescrevê-la aqui.
}

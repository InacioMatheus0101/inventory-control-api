package com.matheuss.controle_estoque_api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "collaborators") 
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Collaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Nome completo do colaborador

    @Column(nullable = false, unique = true)
    private String matricula; // Pode ser a matrícula, login de rede, etc. Deve ser único.

    @Column(nullable = false)
    private String department; // Setor/Departamento do colaborador

    // ====================================================================
    // == RELACIONAMENTO INVERSO COM ASSET ==
    // Um usuário pode ter uma lista de ativos alocados a ele.
    // 'mappedBy = "user"' diz ao JPA que a tabela 'asset' é a dona do relacionamento.
    // CascadeType.ALL e orphanRemoval = true não são recomendados aqui,
    // pois não queremos deletar um ativo se o usuário for deletado.
    // ====================================================================
    @OneToMany(mappedBy = "collaborator", fetch = FetchType.LAZY)
    private List<Asset> assets = new ArrayList<>();
}

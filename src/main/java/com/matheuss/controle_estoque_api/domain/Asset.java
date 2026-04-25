package com.matheuss.controle_estoque_api.domain;

import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.domain.enums.EquipmentState;
import com.matheuss.controle_estoque_api.domain.history.AssetHistory;
import jakarta.persistence.*;
import lombok.Getter;     
import lombok.Setter;      
import lombok.ToString;   
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// ✅ CORREÇÃO: Adicionar índices para melhorar performance nas buscas
@Entity
@Table(name = "asset", indexes = {
       // ── Índices gerais do Asset ─────────────────────────────────────
    @Index(name = "idx_asset_status",           columnList = "status"),
    @Index(name = "idx_asset_patrimonio",       columnList = "patrimonio"),
    @Index(name = "idx_asset_asset_tag",        columnList = "asset_tag"),
    @Index(name = "idx_asset_type",             columnList = "asset_type"),
    @Index(name = "idx_asset_category_id",      columnList = "category_id"),
    @Index(name = "idx_asset_location_id",      columnList = "location_id"),
    @Index(name = "idx_asset_collaborator_id",  columnList = "collaborator_id"),
    @Index(name = "idx_asset_hostname",         columnList = "hostname"),
    @Index(name = "idx_asset_serial_number",    columnList = "serial_number")

})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "asset_type")
@Getter
@Setter
@ToString(exclude = {"category", "location", "collaborator", "history"})
@EntityListeners(AuditingEntityListener.class)
@Audited
public abstract class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String assetTag;

    @Column(unique = true)
    private String patrimonio;

    @Column(nullable = false)
    private LocalDate purchaseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentState equipmentState;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDate dataRecebimento;
    private String chamadoCompra;
    private String sc;
    private String pedido;
    private String nf;
    private String centroCusto;

    private String ticketJira;
    private String ticketDevolucaoJira;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Location location;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "collaborator_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Collaborator collaborator;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @NotAudited
    private List<AssetHistory> history = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return id != null && Objects.equals(id, asset.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

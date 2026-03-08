package com.matheuss.controle_estoque_api.domain.history;

import com.matheuss.controle_estoque_api.domain.Asset;
import com.matheuss.controle_estoque_api.domain.Collaborator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "asset_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AssetHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoryEventType eventType;

    @Column(nullable = false)
    private String details;

    @Column(name = "ticket_number")
    private String ticketNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "associated_user_id")
    private Collaborator associatedUser;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime eventDate;

    public AssetHistory(Asset asset, HistoryEventType eventType, String details, Collaborator associatedUser) {
        this.asset = asset;
        this.eventType = eventType;
        this.details = details;
        this.associatedUser = associatedUser;
    }

    public AssetHistory(Asset asset, HistoryEventType eventType, String details, String ticketNumber, Collaborator associatedUser) {
        this.asset = asset;
        this.eventType = eventType;
        this.details = details;
        this.ticketNumber = ticketNumber;
        this.associatedUser = associatedUser;
    }
}

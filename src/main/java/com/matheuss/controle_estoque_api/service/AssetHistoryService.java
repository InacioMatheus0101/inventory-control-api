package com.matheuss.controle_estoque_api.service;

import com.matheuss.controle_estoque_api.domain.Asset;
import com.matheuss.controle_estoque_api.domain.Collaborator;
import com.matheuss.controle_estoque_api.domain.history.AssetHistory;
import com.matheuss.controle_estoque_api.domain.history.HistoryEventType;
import com.matheuss.controle_estoque_api.repository.AssetHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssetHistoryService {

    private final AssetHistoryRepository assetHistoryRepository;

   
    public void registerEvent(Asset asset, HistoryEventType eventType, String details, Collaborator associatedUser) {
        this.registerEvent(asset, eventType, details, null, associatedUser);
    }


    public void registerEvent(Asset asset, HistoryEventType eventType, String details, String ticketNumber, Collaborator associatedUser) {
        AssetHistory historyRecord = new AssetHistory(asset, eventType, details, ticketNumber, associatedUser);
        assetHistoryRepository.save(historyRecord);
    }
}

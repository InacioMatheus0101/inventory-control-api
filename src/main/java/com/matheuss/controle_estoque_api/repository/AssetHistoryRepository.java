package com.matheuss.controle_estoque_api.repository;

import com.matheuss.controle_estoque_api.domain.history.AssetHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long> {
    
    // ✅ CORREÇÃO: Adicionar suporte a paginação
    Page<AssetHistory> findByAssetIdOrderByEventDateDesc(Long assetId, Pageable pageable);
}

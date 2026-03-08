package com.matheuss.controle_estoque_api.repository;

import com.matheuss.controle_estoque_api.domain.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByAssetTag(String assetTag);

    boolean existsByPatrimonio(String patrimonio);

    boolean existsByAssetTag(String assetTag);

  
    boolean existsByPatrimonioIn(List<String> patrimonios);


    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Asset a WHERE a.serialNumber IN :serialNumbers")
    boolean existsBySerialNumberIn(@Param("serialNumbers") List<String> serialNumbers);

}

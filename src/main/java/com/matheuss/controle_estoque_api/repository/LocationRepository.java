package com.matheuss.controle_estoque_api.repository;

import com.matheuss.controle_estoque_api.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    // JpaRepository já fornece suporte a paginação via findAll(Pageable)
}

package com.matheuss.controle_estoque_api.repository;

import com.matheuss.controle_estoque_api.domain.Peripheral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeripheralRepository extends JpaRepository<Peripheral, Long>, JpaSpecificationExecutor<Peripheral> {

    // Sobrescreve o findAll com paginação adicionando fetch dos relacionamentos
    // Resolve o N+1 sem quebrar os filtros e a paginação
    @Override
    @EntityGraph(attributePaths = {"location", "collaborator", "computer"})
    Page<Peripheral> findAll(Specification<Peripheral> spec, Pageable pageable);

    // Busca por ID já com os relacionamentos carregados (para a tela de detalhe)
    @Query("SELECT p FROM Peripheral p " +
           "LEFT JOIN FETCH p.location " +
           "LEFT JOIN FETCH p.collaborator " +
           "LEFT JOIN FETCH p.computer " +
           "WHERE p.id = :id")
    Optional<Peripheral> findByIdWithDetails(@Param("id") Long id);

    boolean existsByCategoryId(Long categoryId);
}
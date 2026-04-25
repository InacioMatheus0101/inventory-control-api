package com.matheuss.controle_estoque_api.repository.specification;

import com.matheuss.controle_estoque_api.domain.Peripheral;
import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import org.springframework.data.jpa.domain.Specification;

public class PeripheralSpecification {

    public static Specification<Peripheral> hasStatus(AssetStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Peripheral> typeContains(String type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null || type.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("type")), "%" + type.toLowerCase() + "%");
        };
    }

    public static Specification<Peripheral> nameContains(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    // ✅ CORREÇÃO item 2: padronizado para retornar conjunction() em vez de null
    public static Specification<Peripheral> patrimonioContains(String patrimonio) {
        return (root, query, criteriaBuilder) -> {
            if (patrimonio == null || patrimonio.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("patrimonio")), "%" + patrimonio.toLowerCase() + "%");
        };
    }
}
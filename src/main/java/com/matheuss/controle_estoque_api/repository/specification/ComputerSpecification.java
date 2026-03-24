package com.matheuss.controle_estoque_api.repository.specification;

import com.matheuss.controle_estoque_api.domain.Computer;
import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import org.springframework.data.jpa.domain.Specification;

public class ComputerSpecification {

    // Filtro por status (busca exata).
    public static Specification<Computer> hasStatus(AssetStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                // Se o status for nulo, não aplica filtro de status.
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    // A lógica agora busca no campo 'hostname' da entidade.
    public static Specification<Computer> hostnameContains(String hostname) {
        return (root, query, criteriaBuilder) -> {
            if (hostname == null || hostname.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            // A busca é feita no campo 'hostname', ignorando maiúsculas/minúsculas.
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("hostname")), "%" + hostname.toLowerCase() + "%");
        };
    }

    // Filtro por patrimônio (busca parcial, "contém", ignorando maiúsculas/minúsculas).
    public static Specification<Computer> patrimonioContains(String patrimonio) {
        return (root, query, criteriaBuilder) -> {
            if (patrimonio == null || patrimonio.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("patrimonio")), "%" + patrimonio.toLowerCase() + "%");
        };
    }

    // Filtro por número de série (busca parcial, "contém", ignorando maiúsculas/minúsculas).
    public static Specification<Computer> serialNumberContains(String serialNumber) {
        return (root, query, criteriaBuilder) -> {
            if (serialNumber == null || serialNumber.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("serialNumber")), "%" + serialNumber.toLowerCase() + "%");
        };
    }
}

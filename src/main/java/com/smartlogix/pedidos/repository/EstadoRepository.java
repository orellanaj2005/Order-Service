package com.smartlogix.pedidos.repository;

import com.smartlogix.pedidos.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Patrón Repository: abstrae el acceso al catálogo ESTADO (SL_PEDIDOS).
 */
@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {
}

package com.smartlogix.pedidos.repository;

import com.smartlogix.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Patrón Repository: abstrae el acceso a la tabla PEDIDO (SL_PEDIDOS).
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}

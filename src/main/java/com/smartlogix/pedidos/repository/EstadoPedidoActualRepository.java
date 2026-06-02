package com.smartlogix.pedidos.repository;

import com.smartlogix.pedidos.model.EstadoPedidoActual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Patrón Repository: abstrae el acceso a la tabla ESTADO_PEDIDO_ACTUAL (SL_PEDIDOS).
 * La clave es PEDIDO_ID (un único estado actual por pedido).
 */
@Repository
public interface EstadoPedidoActualRepository extends JpaRepository<EstadoPedidoActual, Long> {
}

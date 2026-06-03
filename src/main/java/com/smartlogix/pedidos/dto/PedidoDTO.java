package com.smartlogix.pedidos.dto;

import com.smartlogix.pedidos.model.Pedido;

import java.time.LocalDateTime;

/**
 * DTO 12 — PedidoDTO (salida de datos).
 *
 * Representa la información de un pedido hacia la API, desacoplada de la entidad
 * JPA. No arrastra detalles de mapeo ni la gestión de ESTADO_PEDIDO_ACTUAL: el
 * estado actual se entrega mediante {@link EstadoPedidoActualDTO}.
 *
 * Record inmutable de Java 21 con método de fábrica {@code from(entidad)} para
 * mantener el mapeo en un único lugar.
 */
public record PedidoDTO(
        Long id,
        Integer cantidad,
        Long total,
        Long usuarioId,
        Long productoId,
        LocalDateTime fecha) {

    public static PedidoDTO from(Pedido pedido) {
        return new PedidoDTO(
                pedido.getId(),
                pedido.getCantidad(),
                pedido.getTotal(),
                pedido.getUsuarioId(),
                pedido.getProductoId(),
                pedido.getFecha());
    }
}

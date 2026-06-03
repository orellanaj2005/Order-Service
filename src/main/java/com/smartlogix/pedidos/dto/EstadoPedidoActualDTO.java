package com.smartlogix.pedidos.dto;

import com.smartlogix.pedidos.model.EstadoPedidoActual;

import java.time.LocalDateTime;

/**
 * DTO 15 — EstadoPedidoActualDTO (salida de datos).
 *
 * Expone el estado actual de un pedido con el nombre del estado ya resuelto desde
 * el catálogo ESTADO, para no obligar al cliente a traducir el id. La entidad
 * {@link EstadoPedidoActual} solo guarda el estadoId; el nombre se agrega aquí.
 */
public record EstadoPedidoActualDTO(
        Long pedidoId,
        Long estadoId,
        String estadoNombre,
        LocalDateTime fecha) {

    public static EstadoPedidoActualDTO from(EstadoPedidoActual actual, String estadoNombre) {
        return new EstadoPedidoActualDTO(
                actual.getPedidoId(),
                actual.getEstadoId(),
                estadoNombre,
                actual.getFecha());
    }
}

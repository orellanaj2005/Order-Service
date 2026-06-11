package com.smartlogix.pedidos.dto;

import com.smartlogix.pedidos.model.EstadoPedidoActual;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO 15 — EstadoPedidoActualDTO (salida de datos).
 *
 * Expone el estado actual de un pedido con el nombre del estado ya resuelto desde
 * el catálogo ESTADO, para no obligar al cliente a traducir el id. La entidad
 * {@link EstadoPedidoActual} solo guarda el estadoId; el nombre se agrega aquí.
 */
@Schema(name = "EstadoPedidoActualDTO", description = "Estado actual de un pedido con el nombre del estado resuelto.")
public record EstadoPedidoActualDTO(
        @Schema(description = "Id del pedido", example = "1") Long pedidoId,
        @Schema(description = "Id del estado", example = "2") Long estadoId,
        @Schema(description = "Nombre del estado", example = "Confirmado") String estadoNombre,
        @Schema(description = "Fecha del cambio de estado") LocalDateTime fecha) {

    public static EstadoPedidoActualDTO from(EstadoPedidoActual actual, String estadoNombre) {
        return new EstadoPedidoActualDTO(
                actual.getPedidoId(),
                actual.getEstadoId(),
                estadoNombre,
                actual.getFecha());
    }
}

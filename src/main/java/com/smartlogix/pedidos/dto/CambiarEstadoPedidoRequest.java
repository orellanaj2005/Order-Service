package com.smartlogix.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO 14 — CambiarEstadoPedidoRequest (entrada de datos).
 *
 * Identifica el estado destino dentro del catálogo ESTADO
 * (1 Pendiente, 2 Confirmado, 3 Procesando, 4 Completado, 5 Cancelado).
 * La marca de tiempo de la actualización la pone el sistema, no el cliente.
 */
@Schema(name = "CambiarEstadoPedidoRequest", description = "Estado destino al que transicionar el pedido.")
public record CambiarEstadoPedidoRequest(
        @Schema(description = "Id del estado destino (1 Pendiente, 2 Confirmado, 3 Procesando, 4 Completado, 5 Cancelado)",
                example = "2") Long estadoId) {
}

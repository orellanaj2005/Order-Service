package com.smartlogix.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO 18 — PedidoEventoDTO (comunicación entre microservicios).
 *
 * Transporta el cambio de estado de un pedido hacia el microservicio de
 * Notificaciones (evento publicado, por ejemplo vía Kafka). Lleva solo lo
 * imprescindible para notificar: el pedido afectado, el estado al que pasó y a
 * quién avisar. No expone detalles internos del pedido (total, producto).
 */
@Schema(name = "PedidoEventoDTO", description = "Evento de cambio de estado de pedido para Notificaciones.")
public record PedidoEventoDTO(
        @Schema(description = "Id del pedido afectado", example = "1") Long pedidoId,
        @Schema(description = "Nuevo estado del pedido", example = "Confirmado") String nuevoEstado,
        @Schema(description = "Id del usuario a notificar", example = "1") Long usuarioId) {
}

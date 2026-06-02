package com.smartlogix.pedidos.dto;

/**
 * DTO 18 — PedidoEventoDTO (comunicación entre microservicios).
 *
 * Transporta el cambio de estado de un pedido hacia el microservicio de
 * Notificaciones (evento publicado, por ejemplo vía Kafka). Lleva solo lo
 * imprescindible para notificar: el pedido afectado, el estado al que pasó y a
 * quién avisar. No expone detalles internos del pedido (total, producto).
 */
public record PedidoEventoDTO(
        Long pedidoId,
        String nuevoEstado,
        Long usuarioId) {
}

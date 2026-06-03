package com.smartlogix.pedidos.dto;

/**
 * DTO 14 — CambiarEstadoPedidoRequest (entrada de datos).
 *
 * Identifica el estado destino dentro del catálogo ESTADO
 * (1 Pendiente, 2 Confirmado, 3 Procesando, 4 Completado, 5 Cancelado).
 * La marca de tiempo de la actualización la pone el sistema, no el cliente.
 */
public record CambiarEstadoPedidoRequest(Long estadoId) {
}

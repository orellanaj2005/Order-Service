package com.smartlogix.pedidos.dto;

/**
 * DTO 17 — TotalPedidosDTO (salida de datos, métrica de dashboard).
 *
 * Encapsula el conteo total de pedidos en un JSON autodescriptivo
 * ({@code {"totalPedidos": N}}) en lugar de devolver un número aislado.
 */
public record TotalPedidosDTO(Long totalPedidos) {
}

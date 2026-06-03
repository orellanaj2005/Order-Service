package com.smartlogix.pedidos.dto;

/**
 * DTO 16 — PedidosPorEstadoDTO (salida de datos, KPI de dashboard).
 *
 * Conteo de pedidos agrupados por estado (GROUP BY resuelto en la base de datos).
 * Alimenta el panel 'pedidos por estado' del dashboard.
 */
public record PedidosPorEstadoDTO(
        String estado,
        Long cantidad) {
}

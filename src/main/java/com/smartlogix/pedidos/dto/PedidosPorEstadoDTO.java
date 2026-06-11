package com.smartlogix.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO 16 — PedidosPorEstadoDTO (salida de datos, KPI de dashboard).
 *
 * Conteo de pedidos agrupados por estado (GROUP BY resuelto en la base de datos).
 * Alimenta el panel 'pedidos por estado' del dashboard.
 */
@Schema(name = "PedidosPorEstadoDTO", description = "KPI: cantidad de pedidos por estado.")
public record PedidosPorEstadoDTO(
        @Schema(description = "Nombre del estado", example = "Pendiente") String estado,
        @Schema(description = "Cantidad de pedidos en ese estado", example = "12") Long cantidad) {
}

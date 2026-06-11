package com.smartlogix.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO 17 — TotalPedidosDTO (salida de datos, métrica de dashboard).
 *
 * Encapsula el conteo total de pedidos en un JSON autodescriptivo
 * ({@code {"totalPedidos": N}}) en lugar de devolver un número aislado.
 */
@Schema(name = "TotalPedidosDTO", description = "KPI: cantidad total de pedidos.")
public record TotalPedidosDTO(
        @Schema(description = "Total de pedidos", example = "57") Long totalPedidos) {
}

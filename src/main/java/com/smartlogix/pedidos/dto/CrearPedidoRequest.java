package com.smartlogix.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO 13 — CrearPedidoRequest (entrada de datos).
 *
 * Datos mínimos que el cliente debe aportar para registrar un pedido. El id, la
 * fecha y el estado inicial (Pendiente) los asigna el sistema, no el cliente; por
 * eso no se aceptan aquí.
 */
@Schema(name = "CrearPedidoRequest", description = "Datos mínimos para registrar un pedido.")
public record CrearPedidoRequest(
        @Schema(description = "Cantidad de unidades", example = "5") Integer cantidad,
        @Schema(description = "Total del pedido", example = "149950") Long total,
        @Schema(description = "Id del usuario que realiza el pedido", example = "1") Long usuarioId,
        @Schema(description = "Id del producto solicitado", example = "1") Long productoId) {
}

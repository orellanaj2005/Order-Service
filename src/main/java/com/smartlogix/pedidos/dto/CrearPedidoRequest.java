package com.smartlogix.pedidos.dto;

/**
 * DTO 13 — CrearPedidoRequest (entrada de datos).
 *
 * Datos mínimos que el cliente debe aportar para registrar un pedido. El id, la
 * fecha y el estado inicial (Pendiente) los asigna el sistema, no el cliente; por
 * eso no se aceptan aquí.
 */
public record CrearPedidoRequest(
        Integer cantidad,
        Long total,
        Long usuarioId,
        Long productoId) {
}

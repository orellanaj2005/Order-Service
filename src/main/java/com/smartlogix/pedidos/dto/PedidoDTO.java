package com.smartlogix.pedidos.dto;

import com.smartlogix.pedidos.model.Pedido;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO 12 — PedidoDTO (salida de datos).
 *
 * Representa la información de un pedido hacia la API, desacoplada de la entidad
 * JPA. No arrastra detalles de mapeo ni la gestión de ESTADO_PEDIDO_ACTUAL: el
 * estado actual se entrega mediante {@link EstadoPedidoActualDTO}.
 *
 * Record inmutable de Java 21 con método de fábrica {@code from(entidad)} para
 * mantener el mapeo en un único lugar.
 */
@Schema(name = "PedidoDTO", description = "Representación de salida de un pedido.")
public record PedidoDTO(
        @Schema(description = "Identificador del pedido", example = "1") Long id,
        @Schema(description = "Cantidad de unidades", example = "5") Integer cantidad,
        @Schema(description = "Total del pedido", example = "149950") Long total,
        @Schema(description = "Id del usuario que realiza el pedido", example = "1") Long usuarioId,
        @Schema(description = "Id del producto solicitado", example = "1") Long productoId,
        @Schema(description = "Fecha de creación del pedido") LocalDateTime fecha) {

    public static PedidoDTO from(Pedido pedido) {
        return new PedidoDTO(
                pedido.getId(),
                pedido.getCantidad(),
                pedido.getTotal(),
                pedido.getUsuarioId(),
                pedido.getProductoId(),
                pedido.getFecha());
    }
}

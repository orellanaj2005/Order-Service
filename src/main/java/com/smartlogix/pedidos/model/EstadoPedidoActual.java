package com.smartlogix.pedidos.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidad EstadoPedidoActual — mapeada a la tabla ESTADO_PEDIDO_ACTUAL del schema SL_PEDIDOS.
 *
 * Mantiene el estado actual de cada pedido: hay como mucho una fila por pedido,
 * por lo que PEDIDO_ID es la clave primaria. Se inserta al crear el pedido y se
 * actualiza (in-place) en cada cambio de estado.
 */
@Entity
@Table(name = "ESTADO_PEDIDO_ACTUAL", schema = "SL_PEDIDOS")
public class EstadoPedidoActual {

    @Id
    @Column(name = "PEDIDO_ID")
    private Long pedidoId;

    @Column(name = "ESTADO_ID", nullable = false)
    private Long estadoId;

    @Column(name = "FECHA", nullable = false)
    private LocalDateTime fecha;

    public EstadoPedidoActual() {}

    public EstadoPedidoActual(Long pedidoId, Long estadoId, LocalDateTime fecha) {
        this.pedidoId = pedidoId;
        this.estadoId = estadoId;
        this.fecha    = fecha;
    }

    public Long getPedidoId()                 { return pedidoId; }
    public void setPedidoId(Long pedidoId)    { this.pedidoId = pedidoId; }
    public Long getEstadoId()                 { return estadoId; }
    public void setEstadoId(Long estadoId)    { this.estadoId = estadoId; }
    public LocalDateTime getFecha()           { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}

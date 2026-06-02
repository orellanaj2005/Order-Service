package com.smartlogix.pedidos.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad Pedido — mapeada a la tabla PEDIDO del schema SL_PEDIDOS.
 *
 * Un pedido pertenece a un usuario (USUARIO_ID → User-Service) y referencia un
 * producto (PRODUCTO_ID → Inventory-Service). El monto TOTAL llega ya calculado
 * desde el cliente (precio del producto × cantidad).
 */
@Entity
@Table(name = "PEDIDO", schema = "SL_PEDIDOS")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CANTIDAD", nullable = false)
    private Integer cantidad;

    @Column(name = "TOTAL", nullable = false)
    private Long total;

    @Column(name = "USUARIO_ID", nullable = false)
    private Long usuarioId;

    @Column(name = "PRODUCTO_ID", nullable = false)
    private Long productoId;

    /**
     * Fecha de creación del pedido. La asigna Hibernate en el INSERT
     * (@CreationTimestamp) y no se modifica después (updatable = false).
     */
    @CreationTimestamp
    @Column(name = "FECHA", nullable = false, updatable = false)
    private LocalDateTime fecha;

    public Pedido() {}

    public Pedido(Integer cantidad, Long total, Long usuarioId, Long productoId) {
        this.cantidad   = cantidad;
        this.total      = total;
        this.usuarioId  = usuarioId;
        this.productoId = productoId;
    }

    public Long getId()                       { return id; }
    public void setId(Long id)                { this.id = id; }
    public Integer getCantidad()              { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Long getTotal()                    { return total; }
    public void setTotal(Long total)          { this.total = total; }
    public Long getUsuarioId()                { return usuarioId; }
    public void setUsuarioId(Long usuarioId)  { this.usuarioId = usuarioId; }
    public Long getProductoId()               { return productoId; }
    public void setProductoId(Long productoId){ this.productoId = productoId; }
    public LocalDateTime getFecha()           { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}

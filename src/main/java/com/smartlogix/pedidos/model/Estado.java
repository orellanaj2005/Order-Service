package com.smartlogix.pedidos.model;

import jakarta.persistence.*;

/**
 * Entidad Estado — mapeada a la tabla catálogo ESTADO del schema SL_PEDIDOS.
 *
 * Contiene los estados posibles de un pedido (precargados):
 * 1 Pendiente, 2 Confirmado, 3 Procesando, 4 Completado, 5 Cancelado.
 * Es un catálogo: el ID se asigna explícitamente, no se autogenera.
 */
@Entity
@Table(name = "ESTADO", schema = "SL_PEDIDOS")
public class Estado {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "ESTADO", nullable = false, length = 400)
    private String estado;

    public Estado() {}

    public Estado(Long id, String estado) {
        this.id = id;
        this.estado = estado;
    }

    public Long getId()             { return id; }
    public void setId(Long id)      { this.id = id; }
    public String getEstado()       { return estado; }
    public void setEstado(String e) { this.estado = e; }
}

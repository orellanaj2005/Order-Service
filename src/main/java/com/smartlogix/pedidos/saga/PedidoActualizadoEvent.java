package com.smartlogix.pedidos.saga;

import org.springframework.context.ApplicationEvent;

/**
 * Patrón Saga (Choreography): evento de dominio publicado tras cada cambio de estado.
 * Los listeners (PedidoSaga) reaccionan y ejecutan compensaciones si es necesario.
 */
public class PedidoActualizadoEvent extends ApplicationEvent {

    private final Long pedidoId;
    private final String nuevoEstado;

    public PedidoActualizadoEvent(Object source, Long pedidoId, String nuevoEstado) {
        super(source);
        this.pedidoId = pedidoId;
        this.nuevoEstado = nuevoEstado;
    }

    public Long getPedidoId()    { return pedidoId;    }
    public String getNuevoEstado() { return nuevoEstado; }
}

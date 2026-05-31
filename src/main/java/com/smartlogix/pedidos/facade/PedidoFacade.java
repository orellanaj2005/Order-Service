package com.smartlogix.pedidos.facade;

import com.smartlogix.pedidos.model.Pedido;
import com.smartlogix.pedidos.saga.PedidoActualizadoEvent;
import com.smartlogix.pedidos.service.PedidoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Patrón Facade: punto de entrada único para las operaciones de pedido.
 * Oculta la coordinación entre PedidoService, el Circuit Breaker y la Saga.
 *
 * El controlador solo conoce este facade; no interactúa con PedidoService
 * ni con el publicador de eventos directamente.
 */
@Component
public class PedidoFacade {

    private final PedidoService pedidoService;
    private final ApplicationEventPublisher eventPublisher;

    public PedidoFacade(PedidoService pedidoService, ApplicationEventPublisher eventPublisher) {
        this.pedidoService = pedidoService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Patrón Circuit Breaker: si el servicio falla repetidamente, el circuito se
     * abre y se ejecuta fallbackActualizar() para devolver una respuesta degradada.
     */
    @CircuitBreaker(name = "pedidos", fallbackMethod = "fallbackActualizar")
    public void actualizarEstado(Long pedidoId, Integer estadoId, String nombreEstado) {
        pedidoService.actualizarEstadoPedido(pedidoId, estadoId, nombreEstado);
        // Patrón Saga: publica evento para que los listeners coordinen compensaciones
        eventPublisher.publishEvent(new PedidoActualizadoEvent(this, pedidoId, nombreEstado));
    }

    public Pedido getPedido(Long pedidoId) {
        return pedidoService.getPedido(pedidoId);
    }

    public java.util.List<Pedido> listarTodos() {
        return pedidoService.listarTodos();
    }

    public Pedido crear(Integer estadoId, String estadoNombre) {
        return pedidoService.crear(estadoId, estadoNombre);
    }

    public void eliminar(Long pedidoId) {
        pedidoService.eliminar(pedidoId);
    }

    // Fallback del Circuit Breaker: respuesta degradada cuando el circuito está abierto
    public void fallbackActualizar(Long pedidoId, Integer estadoId, String nombreEstado, Throwable t) {
        System.out.println("[Circuit Breaker ABIERTO] No se pudo actualizar pedido #"
                + pedidoId + ": " + t.getMessage());
    }
}

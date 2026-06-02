package com.smartlogix.pedidos.facade;

import com.smartlogix.pedidos.exception.NotFoundException;
import com.smartlogix.pedidos.exception.ServiceUnavailableException;
import com.smartlogix.pedidos.model.EstadoPedidoActual;
import com.smartlogix.pedidos.model.Pedido;
import com.smartlogix.pedidos.saga.PedidoActualizadoEvent;
import com.smartlogix.pedidos.service.PedidoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Patrón Facade: punto de entrada único para las operaciones de pedido.
 * Oculta la coordinación entre PedidoService, el Circuit Breaker y la Saga.
 */
@Component
public class PedidoFacade {

    private final PedidoService pedidoService;
    private final ApplicationEventPublisher eventPublisher;

    public PedidoFacade(PedidoService pedidoService, ApplicationEventPublisher eventPublisher) {
        this.pedidoService = pedidoService;
        this.eventPublisher = eventPublisher;
    }

    public Pedido crear(Integer cantidad, Long total, Long usuarioId, Long productoId) {
        return pedidoService.crearPedido(cantidad, total, usuarioId, productoId);
    }

    /**
     * Patrón Circuit Breaker: si el servicio falla repetidamente, el circuito se
     * abre y se ejecuta fallbackCambiarEstado().
     *
     * Patrón Saga: publica un evento tras el cambio para que los listeners
     * coordinen compensaciones (p. ej. liberar stock al cancelar).
     */
    @CircuitBreaker(name = "pedidos", fallbackMethod = "fallbackCambiarEstado")
    public void cambiarEstado(Long pedidoId, Long estadoId) {
        String nuevoEstado = pedidoService.cambiarEstado(pedidoId, estadoId);
        eventPublisher.publishEvent(new PedidoActualizadoEvent(this, pedidoId, nuevoEstado));
    }

    public Pedido getPedido(Long pedidoId) {
        return pedidoService.getPedido(pedidoId);
    }

    public List<Pedido> listarTodos() {
        return pedidoService.listarTodos();
    }

    public EstadoPedidoActual obtenerEstadoActual(Long pedidoId) {
        return pedidoService.obtenerEstadoActual(pedidoId);
    }

    public void eliminar(Long pedidoId) {
        pedidoService.eliminar(pedidoId);
    }

    /**
     * Fallback del Circuit Breaker. A diferencia de la versión anterior, NO oculta
     * el fallo devolviendo un falso 200 OK:
     *  - Si el error es de negocio (recurso inexistente), se re-lanza como 404.
     *  - Si es un fallo de infraestructura o el circuito está abierto, se devuelve 503.
     */
    public void fallbackCambiarEstado(Long pedidoId, Long estadoId, Throwable t) {
        if (t instanceof NotFoundException nfe) {
            throw nfe;
        }
        System.out.println("[Circuit Breaker] cambio de estado del pedido #" + pedidoId
                + " degradado: " + t.getMessage());
        throw new ServiceUnavailableException("Servicio de pedidos no disponible temporalmente");
    }
}

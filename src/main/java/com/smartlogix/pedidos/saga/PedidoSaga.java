package com.smartlogix.pedidos.saga;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Patrón Saga (Choreography): escucha eventos de dominio y coordina transacciones
 * distribuidas. Si un paso falla, ejecuta la transacción compensatoria correspondiente.
 *
 * Flujo normal:     actualizarEstado → PedidoActualizadoEvent → onPedidoActualizado()
 * Compensación:     estado=CANCELADO → compensar() → reponer stock
 * En producción:    compensar() publicaría un evento Kafka → Inventory-Service lo consumiría.
 */
@Component
public class PedidoSaga {

    @EventListener
    public void onPedidoActualizado(PedidoActualizadoEvent evento) {
        System.out.println("[Saga] Pedido #" + evento.getPedidoId()
                + " → nuevo estado: " + evento.getNuevoEstado());

        if ("CANCELADO".equalsIgnoreCase(evento.getNuevoEstado())) {
            compensar(evento.getPedidoId());
        }
    }

    private void compensar(Long pedidoId) {
        // Transacción compensatoria: revertir reserva de stock
        System.out.println("[Saga - Compensación] Liberando stock del pedido #" + pedidoId);
        // TODO producción: eventPublisher.publishEvent(new StockLiberadoEvent(pedidoId));
    }
}

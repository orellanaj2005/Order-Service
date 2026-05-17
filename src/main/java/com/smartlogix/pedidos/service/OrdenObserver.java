package com.smartlogix.pedidos.service;

import org.springframework.stereotype.Component;

/**
 * Patrón Observer: contrato para reaccionar a cambios de estado de un pedido.
 */
public interface OrdenObserver {
    void alCambiarEstado(Long pedidoId, String nuevoEstado);
}

/**
 * Observer 1: simula el envío de un evento a Notification-Service (vía Kafka en producción).
 */
@Component
class NotificacionSistemaObserver implements OrdenObserver {
    @Override
    public void alCambiarEstado(Long pedidoId, String nuevoEstado) {
        System.out.println("[Observer - Notificación] Pedido #" + pedidoId + " → " + nuevoEstado);
    }
}

/**
 * Observer 2: repone stock cuando el pedido es cancelado.
 */
@Component
class InventarioObserver implements OrdenObserver {
    @Override
    public void alCambiarEstado(Long pedidoId, String nuevoEstado) {
        if ("CANCELADO".equalsIgnoreCase(nuevoEstado)) {
            System.out.println("[Observer - Inventario] Reponiendo stock por cancelación del pedido #" + pedidoId);
        }
    }
}

//Service/OrdenObserver.java

package com.smartlogix.pedidos.service;

// define el contrato del Observer
public interface OrdenObserver {
    void alCambiarEstado(Long pedidoId, String nuevoEstado);
}

// Notificaciones del Sistema
class NotificacionSistemaObserver implements OrdenObserver {
    @Override
    public void alCambiarEstado(Long pedidoId, String nuevoEstado) {
        System.out.println("Evento [Kafka]: Enviando señal para notificar que el Pedido " + pedidoId + " está " + nuevoEstado);
    }
}

// Lógica de Inventario
class InventarioObserver implements OrdenObserver {
    @Override
    public void alCambiarEstado(Long pedidoId, String nuevoEstado) {
        if (nuevoEstado != null && nuevoEstado.equalsIgnoreCase("Cancelado")) {
            System.out.println("Inventario: Reponiendo stock por cancelación de Pedido #" + pedidoId);
        }
    }
}


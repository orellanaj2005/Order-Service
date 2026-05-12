//Service/OrdenObserver.java
//package com.smartlogix.pedidos.service;


public interface OrdenObserver {
    void alCambiarEstado(Long ordenId, String nuevoEstado);
}

// Reacción 1: Avisar al Microservicio de Notificaciones
public class NotificacionSistemaObserver implements OrdenObserver {
    @Override
    public void alCambiarEstado(Long ordenId, String nuevoEstado) {
        System.out.println("Evento [Kafka]: Enviando señal para notificar que la Orden " + ordenId + " está " + nuevoEstado);
    }
}

// Reacción 2: Lógica de Inventario
public class InventarioObserver implements OrdenObserver {
    @Override
    public void alCambiarEstado(Long ordenId, String nuevoEstado) {
        if (nuevoEstado.equalsIgnoreCase("Cancelado")) {
            System.out.println("Inventario: Reponiendo stock por cancelación de Orden #" + ordenId);
        }
    }
}
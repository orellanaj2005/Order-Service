//controller/OrderController.java

// Archivo: controller/OrdenController.java
package com.smartlogix.ordenes.controller;

import com.smartlogix.ordenes.service.OrdenService;
import com.smartlogix.ordenes.service.observers.NotificacionSistemaObserver;
import com.smartlogix.ordenes.service.observers.InventarioObserver;

public class OrdenController {
    private OrdenService ordenService;

    public OrdenController() {
        this.ordenService = new OrdenService();
        // Suscribimos los observadores del microservicio
        this.ordenService.suscribir(new NotificacionSistemaObserver());
        this.ordenService.suscribir(new InventarioObserver());
    }

    // Simula recibir un cambio de estado (ej: desde un endpoint API)
    public void recibirActualización(Long id, Integer estadoId, String estadoNombre) {
        ordenService.actualizarEstadoOrden(id, estadoId, estadoNombre);
    }
}
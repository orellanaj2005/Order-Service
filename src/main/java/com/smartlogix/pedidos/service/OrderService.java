//service/OrderService.java 

/// Archivo: service/OrdenService.java
package com.smartlogix.ordenes.service;

import com.smartlogix.ordenes.repository.OrdenRepository;
import com.smartlogix.ordenes.service.observers.OrdenObserver;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private List<OrdenObserver> observadores = new ArrayList<>();
    private OrdenRepository repository = OrdenRepository.getInstance();

    public void suscribir(OrdenObserver obs) {
        observadores.add(obs);
    }

    public void actualizarEstadoOrden(Long ordenId, Integer estadoId, String nombreEstado) {
        // 1. Guardar cambio en BD local (Singleton)
        repository.registrarEstado(ordenId, estadoId);

        // 2. Notificar a los observadores (Observer Pattern)
        for (OrdenObserver obs : observadores) {
            obs.alCambiarEstado(ordenId, nombreEstado);
        }
    }
}
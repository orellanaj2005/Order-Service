package com.smartlogix.pedidos.service;

import com.smartlogix.pedidos.model.Pedido;
import com.smartlogix.pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Patrón Singleton: Spring garantiza una única instancia de este bean (@Service).
 * Patrón Observer: notifica a todos los observadores registrados al cambiar el estado.
 */
@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final List<OrdenObserver> observadores;

    // Inyección por constructor: Spring inyecta automáticamente todos los beans OrdenObserver
    public PedidoService(PedidoRepository repository, List<OrdenObserver> observadores) {
        this.repository = repository;
        this.observadores = observadores;
    }

    public void actualizarEstadoPedido(Long pedidoId, Integer estadoId, String nombreEstado) {
        Pedido pedido = repository.findById(pedidoId).orElse(new Pedido());
        pedido.setId(pedidoId);
        pedido.setEstadoId(estadoId);
        pedido.setEstadoNombre(nombreEstado);
        pedido.setFechaActualizacion(LocalDateTime.now());
        repository.save(pedido);

        // Patrón Observer: avisar a todos los suscritos
        for (OrdenObserver obs : observadores) {
            obs.alCambiarEstado(pedidoId, nombreEstado);
        }
    }

    public Pedido getPedido(Long pedidoId) {
        return repository.findById(pedidoId).orElseThrow(
            () -> new RuntimeException("Pedido no encontrado: " + pedidoId)
        );
    }
}

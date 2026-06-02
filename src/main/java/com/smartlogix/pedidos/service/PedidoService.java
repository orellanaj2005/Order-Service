package com.smartlogix.pedidos.service;

import com.smartlogix.pedidos.exception.NotFoundException;
import com.smartlogix.pedidos.model.Estado;
import com.smartlogix.pedidos.model.EstadoPedidoActual;
import com.smartlogix.pedidos.model.Pedido;
import com.smartlogix.pedidos.repository.EstadoPedidoActualRepository;
import com.smartlogix.pedidos.repository.EstadoRepository;
import com.smartlogix.pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Patrón Singleton: Spring garantiza una única instancia de este bean (@Service).
 * Patrón Observer: notifica a todos los OrdenObserver registrados al cambiar el estado.
 *
 * Coordina las tres tablas del schema SL_PEDIDOS: PEDIDO, ESTADO y ESTADO_PEDIDO_ACTUAL.
 */
@Service
public class PedidoService {

    /** Estado inicial de un pedido recién creado (1 = Pendiente, catálogo ESTADO). */
    public static final Long ESTADO_INICIAL = 1L;

    private final PedidoRepository pedidoRepository;
    private final EstadoRepository estadoRepository;
    private final EstadoPedidoActualRepository estadoPedidoActualRepository;
    private final List<OrdenObserver> observadores;

    public PedidoService(PedidoRepository pedidoRepository,
                         EstadoRepository estadoRepository,
                         EstadoPedidoActualRepository estadoPedidoActualRepository,
                         List<OrdenObserver> observadores) {
        this.pedidoRepository = pedidoRepository;
        this.estadoRepository = estadoRepository;
        this.estadoPedidoActualRepository = estadoPedidoActualRepository;
        this.observadores = observadores;
    }

    /**
     * Crea un pedido, le asigna el estado inicial (Pendiente) y registra su
     * estado actual en ESTADO_PEDIDO_ACTUAL. Notifica a los observadores.
     */
    @Transactional
    public Pedido crearPedido(Integer cantidad, Long total, Long usuarioId, Long productoId) {
        Pedido guardado = pedidoRepository.save(new Pedido(cantidad, total, usuarioId, productoId));

        Estado inicial = estadoRepository.findById(ESTADO_INICIAL)
                .orElseThrow(() -> new NotFoundException("Estado inicial no configurado: " + ESTADO_INICIAL));

        estadoPedidoActualRepository.save(
                new EstadoPedidoActual(guardado.getId(), inicial.getId(), LocalDateTime.now()));

        notificar(guardado.getId(), inicial.getEstado());
        return guardado;
    }

    /**
     * Cambia el estado de un pedido existente: valida pedido y estado, actualiza
     * ESTADO_PEDIDO_ACTUAL y notifica a los observadores. Devuelve el nombre del
     * nuevo estado (lo usa la Saga).
     */
    @Transactional
    public String cambiarEstado(Long pedidoId, Long estadoId) {
        if (!pedidoRepository.existsById(pedidoId)) {
            throw new NotFoundException("Pedido no encontrado: " + pedidoId);
        }
        Estado estado = estadoRepository.findById(estadoId)
                .orElseThrow(() -> new NotFoundException("Estado no encontrado: " + estadoId));

        EstadoPedidoActual actual = estadoPedidoActualRepository.findById(pedidoId)
                .orElseGet(EstadoPedidoActual::new);
        actual.setPedidoId(pedidoId);
        actual.setEstadoId(estadoId);
        actual.setFecha(LocalDateTime.now());
        estadoPedidoActualRepository.save(actual);

        notificar(pedidoId, estado.getEstado());
        return estado.getEstado();
    }

    @Transactional(readOnly = true)
    public Pedido getPedido(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado: " + pedidoId));
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public EstadoPedidoActual obtenerEstadoActual(Long pedidoId) {
        return estadoPedidoActualRepository.findById(pedidoId)
                .orElseThrow(() -> new NotFoundException("Pedido sin estado registrado: " + pedidoId));
    }

    @Transactional
    public void eliminar(Long pedidoId) {
        if (!pedidoRepository.existsById(pedidoId)) {
            throw new NotFoundException("Pedido no encontrado: " + pedidoId);
        }
        // Limpia el estado actual asociado si existe (evita huérfanos en ESTADO_PEDIDO_ACTUAL).
        estadoPedidoActualRepository.findById(pedidoId)
                .ifPresent(estadoPedidoActualRepository::delete);
        pedidoRepository.deleteById(pedidoId);
    }

    // Patrón Observer: avisar a todos los suscritos del cambio de estado.
    private void notificar(Long pedidoId, String nombreEstado) {
        for (OrdenObserver obs : observadores) {
            obs.alCambiarEstado(pedidoId, nombreEstado);
        }
    }
}

// Service/PedidoService.java
//package com.smartlogix.pedidos.service;
//public class PedidoService {    
//} 

package com.smartlogix.pedidos.service;

import com.smartlogix.pedidos.model.Pedido;
import com.smartlogix.pedidos.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service // Patrón Singleton automático de Spring
public class PedidoService {

    @Autowired
    private PedidoRepository repository;

    // Lista donde viven los observadores suscritos
    private final List<OrdenObserver> observadores = new ArrayList<>();

    // Constructor: Aquí los suscribimos al revivir el servicio
    public PedidoService() {
        this.observadores.add(new NotificacionSistemaObserver());
        this.observadores.add(new InventarioObserver());
    }

    public void actualizarEstadoPedido(Long pedidoId, Integer estadoId, String nombreEstado) {
        // 1. Buscar si existe en la base de datos o preparar uno nuevo
        Pedido pedido = repository.findById(pedidoId).orElse(new Pedido());
        pedido.setId(pedidoId);
        pedido.setEstadoId(estadoId);
        pedido.setEstadoNombre(nombreEstado);
        pedido.setFechaActualizacion(LocalDateTime.now());
        
        // Guardar el cambio en la base de datos local
        repository.save(pedido);

        // 2. Ejecutar el Patrón Observer (Avisar a todos los suscritos)
        for (OrdenObserver obs : observadores) {
            obs.alCambiarEstado(pedidoId, nombreEstado);
        }
    }
}

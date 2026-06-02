package com.smartlogix.pedidos.controller;

import com.smartlogix.pedidos.facade.PedidoFacade;
import com.smartlogix.pedidos.model.EstadoPedidoActual;
import com.smartlogix.pedidos.model.Pedido;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * El gateway enruta /api/pedidos/** con StripPrefix=1, por lo que reenvía
 * /pedidos/** a este controlador. Por eso el mapping base es /pedidos.
 *
 * Las excepciones se traducen a códigos HTTP en {@code GlobalExceptionHandler}
 * (404 recurso inexistente, 503 circuito abierto).
 */
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoFacade facade;

    public PedidoController(PedidoFacade facade) {
        this.facade = facade;
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listar() {
        return ResponseEntity.ok(facade.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(facade.getPedido(id));
    }

    @GetMapping("/{id}/estado")
    public ResponseEntity<EstadoPedidoActual> estadoActual(@PathVariable Long id) {
        return ResponseEntity.ok(facade.obtenerEstadoActual(id));
    }

    @PostMapping
    public ResponseEntity<Pedido> crear(@RequestBody Pedido body) {
        Pedido creado = facade.crear(
                body.getCantidad(), body.getTotal(), body.getUsuarioId(), body.getProductoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam Long estadoId) {
        facade.cambiarEstado(id, estadoId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        facade.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

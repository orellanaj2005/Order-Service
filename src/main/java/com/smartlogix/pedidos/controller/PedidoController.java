package com.smartlogix.pedidos.controller;

import com.smartlogix.pedidos.facade.PedidoFacade;
import com.smartlogix.pedidos.model.Pedido;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping
    public ResponseEntity<Pedido> crear(@RequestBody Map<String, Object> body) {
        Integer estadoId     = (Integer) body.getOrDefault("estadoId", 1);
        String  estadoNombre = (String)  body.getOrDefault("estadoNombre", "PENDIENTE");
        return ResponseEntity.ok(facade.crear(estadoId, estadoNombre));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> actualizarEstado(
            @PathVariable Long id,
            @RequestParam Integer estadoId,
            @RequestParam String nombreEstado) {
        facade.actualizarEstado(id, estadoId, nombreEstado);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        facade.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
    return ResponseEntity.status(500).body(ex.getMessage());
    }
}

package com.smartlogix.pedidos.controller;

import com.smartlogix.pedidos.facade.PedidoFacade;
import com.smartlogix.pedidos.model.Pedido;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoFacade facade;

    public PedidoController(PedidoFacade facade) {
        this.facade = facade;
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> actualizarEstado(
            @PathVariable Long id,
            @RequestParam Integer estadoId,
            @RequestParam String nombreEstado) {
        facade.actualizarEstado(id, estadoId, nombreEstado);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(facade.getPedido(id));
    }
}

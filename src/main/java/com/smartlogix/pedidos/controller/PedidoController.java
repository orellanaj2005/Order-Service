package com.smartlogix.pedidos.controller;

import com.smartlogix.pedidos.facade.PedidoFacade;
import com.smartlogix.pedidos.model.EstadoPedidoActual;
import com.smartlogix.pedidos.model.Pedido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Pedidos", description = "Registro de pedidos y gestión de su estado. El JWT lo valida el Api-Gateway.")
public class PedidoController {

    private final PedidoFacade facade;

    public PedidoController(PedidoFacade facade) {
        this.facade = facade;
    }

    @Operation(summary = "Listar pedidos")
    @ApiResponse(responseCode = "200", description = "Listado de pedidos")
    @GetMapping
    public ResponseEntity<List<Pedido>> listar() {
        return ResponseEntity.ok(facade.listarTodos());
    }

    @Operation(summary = "Obtener pedido por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido no existe"),
            @ApiResponse(responseCode = "503", description = "Circuito abierto (dependencia no disponible)")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtener(
            @Parameter(description = "Id del pedido", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(facade.getPedido(id));
    }

    @Operation(summary = "Estado actual de un pedido",
            description = "Devuelve el registro ESTADO_PEDIDO_ACTUAL del pedido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actual encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido no existe")
    })
    @GetMapping("/{id}/estado")
    public ResponseEntity<EstadoPedidoActual> estadoActual(
            @Parameter(description = "Id del pedido", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(facade.obtenerEstadoActual(id));
    }

    @Operation(summary = "Crear pedido",
            description = "Registra un pedido. El id, la fecha y el estado inicial (Pendiente) los asigna el sistema.")
    @ApiResponse(responseCode = "201", description = "Pedido creado")
    @PostMapping
    public ResponseEntity<Pedido> crear(@RequestBody Pedido body) {
        Pedido creado = facade.crear(
                body.getCantidad(), body.getTotal(), body.getUsuarioId(), body.getProductoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Cambiar estado del pedido",
            description = "Transiciona el pedido al estado destino del catálogo ESTADO "
                    + "(1 Pendiente, 2 Confirmado, 3 Procesando, 4 Completado, 5 Cancelado).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "404", description = "Pedido no existe")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @Parameter(description = "Id del pedido", example = "1") @PathVariable Long id,
            @Parameter(description = "Id del estado destino", example = "2") @RequestParam Long estadoId) {
        facade.cambiarEstado(id, estadoId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Eliminar pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido eliminado"),
            @ApiResponse(responseCode = "404", description = "Pedido no existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Id del pedido", example = "1") @PathVariable Long id) {
        facade.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

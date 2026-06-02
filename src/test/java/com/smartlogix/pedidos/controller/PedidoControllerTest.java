package com.smartlogix.pedidos.controller;

import com.smartlogix.pedidos.exception.GlobalExceptionHandler;
import com.smartlogix.pedidos.exception.NotFoundException;
import com.smartlogix.pedidos.exception.ServiceUnavailableException;
import com.smartlogix.pedidos.facade.PedidoFacade;
import com.smartlogix.pedidos.model.EstadoPedidoActual;
import com.smartlogix.pedidos.model.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias de PedidoController (estructura AAA).
 *
 * Arrange común: PedidoFacade mockeado; MockMvc standalone con el manejador
 * global de excepciones (404 recurso inexistente, 503 circuito abierto).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoController – pruebas unitarias")
class PedidoControllerTest {

    @Mock private PedidoFacade facade;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PedidoController(facade))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Pedido pedidoConId(Long id) {
        Pedido p = new Pedido(2, 1000L, 10L, 20L);
        p.setId(id);
        return p;
    }

    @Test
    @DisplayName("GET /pedidos – hay pedidos: 200 con la lista")
    void listar_hayPedidos_200() throws Exception {
        // ARRANGE
        when(facade.listarTodos()).thenReturn(Arrays.asList(pedidoConId(1L), pedidoConId(2L)));

        // ACT + ASSERT
        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /pedidos/{id} – pedido existente: 200 con el pedido")
    void obtener_existente_200() throws Exception {
        // ARRANGE
        when(facade.getPedido(1L)).thenReturn(pedidoConId(1L));

        // ACT + ASSERT
        mockMvc.perform(get("/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /pedidos/{id} – pedido inexistente: 404 (no 500)")
    void obtener_inexistente_404() throws Exception {
        // ARRANGE
        when(facade.getPedido(99L)).thenThrow(new NotFoundException("Pedido no encontrado: 99"));

        // ACT + ASSERT
        mockMvc.perform(get("/pedidos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /pedidos/{id}/estado – pedido con estado: 200 con el estado actual")
    void estadoActual_conEstado_200() throws Exception {
        // ARRANGE
        when(facade.obtenerEstadoActual(1L))
                .thenReturn(new EstadoPedidoActual(1L, 3L, LocalDateTime.now()));

        // ACT + ASSERT
        mockMvc.perform(get("/pedidos/1/estado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoId").value(3));
    }

    @Test
    @DisplayName("POST /pedidos – cuerpo válido: 201 con el pedido creado")
    void crear_cuerpoValido_201() throws Exception {
        // ARRANGE
        when(facade.crear(anyInt(), anyLong(), anyLong(), anyLong())).thenReturn(pedidoConId(1L));

        // ACT + ASSERT
        mockMvc.perform(post("/pedidos")
                        .contentType("application/json")
                        .content("{\"cantidad\":2,\"total\":1000,\"usuarioId\":10,\"productoId\":20}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PATCH /pedidos/{id}/estado – cambio válido: 200")
    void cambiarEstado_valido_200() throws Exception {
        // ARRANGE
        doNothing().when(facade).cambiarEstado(1L, 3L);

        // ACT + ASSERT
        mockMvc.perform(patch("/pedidos/1/estado").param("estadoId", "3"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /pedidos/{id}/estado – pedido inexistente: 404")
    void cambiarEstado_inexistente_404() throws Exception {
        // ARRANGE
        doThrow(new NotFoundException("Pedido no encontrado: 99"))
                .when(facade).cambiarEstado(99L, 3L);

        // ACT + ASSERT
        mockMvc.perform(patch("/pedidos/99/estado").param("estadoId", "3"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /pedidos/{id}/estado – circuito abierto: 503 (no un falso 200)")
    void cambiarEstado_circuitoAbierto_503() throws Exception {
        // ARRANGE
        doThrow(new ServiceUnavailableException("Servicio de pedidos no disponible temporalmente"))
                .when(facade).cambiarEstado(1L, 3L);

        // ACT + ASSERT
        mockMvc.perform(patch("/pedidos/1/estado").param("estadoId", "3"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    @DisplayName("DELETE /pedidos/{id} – eliminar: 204 sin contenido")
    void eliminar_204() throws Exception {
        // ARRANGE
        doNothing().when(facade).eliminar(1L);

        // ACT + ASSERT
        mockMvc.perform(delete("/pedidos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /pedidos/{id} – pedido inexistente: 404")
    void eliminar_inexistente_404() throws Exception {
        // ARRANGE
        doThrow(new NotFoundException("Pedido no encontrado: 99")).when(facade).eliminar(99L);

        // ACT + ASSERT
        mockMvc.perform(delete("/pedidos/99"))
                .andExpect(status().isNotFound());
    }
}

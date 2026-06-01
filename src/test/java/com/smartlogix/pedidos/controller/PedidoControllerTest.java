package com.smartlogix.pedidos.controller;
 
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogix.pedidos.facade.PedidoFacade;
import com.smartlogix.pedidos.model.Pedido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
 
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
 
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 
@WebMvcTest(PedidoController.class)
class PedidoControllerTest {
 
    @Autowired
    private MockMvc mockMvc;
 
    @MockBean
    private PedidoFacade facade;
 
    @Autowired
    private ObjectMapper objectMapper;
 
    // ─────────────────────────────────────────────────────────────────────────
    // GET /pedidos
    // ─────────────────────────────────────────────────────────────────────────
 
    /**
     * Caso exitoso: listar pedidos cuando hay registros retorna 200 con lista.
     */
    @Test
    void listar_cuandoHayPedidos_retorna200ConLista() throws Exception {
        // Arrange
        Pedido p1 = buildPedido(1L, 1, "PENDIENTE");
        Pedido p2 = buildPedido(2L, 2, "EN_PROCESO");
        when(facade.listarTodos()).thenReturn(Arrays.asList(p1, p2));
 
        // Act & Assert
        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].estadoNombre").value("EN_PROCESO"));
    }
 
    /**
     * Caso alternativo: listar pedidos cuando no hay ninguno retorna 200 con lista vacía.
     */
    @Test
    void listar_cuandoNohayPedidos_retorna200ConListaVacia() throws Exception {
        // Arrange
        when(facade.listarTodos()).thenReturn(Collections.emptyList());
 
        // Act & Assert
        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    // GET /pedidos/{id}
    // ─────────────────────────────────────────────────────────────────────────
 
    /**
     * Caso exitoso: obtener un pedido existente por ID retorna 200 con el pedido.
     */
    @Test
    void obtener_cuandoExiste_retorna200ConPedido() throws Exception {
        // Arrange
        Pedido pedido = buildPedido(1L, 1, "PENDIENTE");
        when(facade.getPedido(1L)).thenReturn(pedido);
 
        // Act & Assert
        mockMvc.perform(get("/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estadoNombre").value("PENDIENTE"));
    }
 
    /**
     * Caso de error: obtener un pedido que no existe lanza excepción y retorna 500.
     */
    @Test
    void obtener_cuandoNoExiste_retorna500() throws Exception {
        // Arrange
        when(facade.getPedido(99L)).thenThrow(new RuntimeException("Pedido no encontrado: 99"));
 
        // Act & Assert
        mockMvc.perform(get("/pedidos/99"))
                .andExpect(status().isInternalServerError());
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    // POST /pedidos
    // ─────────────────────────────────────────────────────────────────────────
 
    /**
     * Caso exitoso: crear un pedido con cuerpo válido retorna 200 con el pedido creado.
     */
    @Test
    void crear_conCuerpoValido_retorna200ConPedidoCreado() throws Exception {
        // Arrange
        Map<String, Object> body = new HashMap<>();
        body.put("estadoId", 1);
        body.put("estadoNombre", "PENDIENTE");
        Pedido pedidoCreado = buildPedido(5L, 1, "PENDIENTE");
        when(facade.crear(1, "PENDIENTE")).thenReturn(pedidoCreado);
 
        // Act & Assert
        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.estadoNombre").value("PENDIENTE"));
    }
 
    /**
     * Caso alternativo: crear un pedido con cuerpo vacío usa valores por defecto.
     */
    @Test
    void crear_conCuerpoVacio_usaValoresPorDefecto() throws Exception {
        // Arrange
        Pedido pedidoDefault = buildPedido(6L, 1, "PENDIENTE");
        when(facade.crear(1, "PENDIENTE")).thenReturn(pedidoDefault);
 
        // Act & Assert
        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoNombre").value("PENDIENTE"));
 
        verify(facade, times(1)).crear(1, "PENDIENTE");
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    // PATCH /pedidos/{id}/estado
    // ─────────────────────────────────────────────────────────────────────────
 
    /**
     * Caso exitoso: actualizar el estado de un pedido existente retorna 200.
     */
    @Test
    void actualizarEstado_conParametrosValidos_retorna200() throws Exception {
        // Arrange
        doNothing().when(facade).actualizarEstado(1L, 2, "EN_PROCESO");
 
        // Act & Assert
        mockMvc.perform(patch("/pedidos/1/estado")
                        .param("estadoId", "2")
                        .param("nombreEstado", "EN_PROCESO"))
                .andExpect(status().isOk());
 
        verify(facade, times(1)).actualizarEstado(1L, 2, "EN_PROCESO");
    }
 
    /**
     * Caso de error: actualizar estado cuando el facade lanza excepción retorna 500.
     */
    @Test
    void actualizarEstado_cuandoFacadeFalla_retorna500() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Fallo interno")).when(facade)
                .actualizarEstado(eq(99L), anyInt(), anyString());
 
        // Act & Assert
        mockMvc.perform(patch("/pedidos/99/estado")
                        .param("estadoId", "3")
                        .param("nombreEstado", "CANCELADO"))
                .andExpect(status().isInternalServerError());
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /pedidos/{id}
    // ─────────────────────────────────────────────────────────────────────────
 
    /**
     * Caso exitoso: eliminar un pedido existente retorna 204 sin cuerpo.
     */
    @Test
    void eliminar_conIdValido_retorna204() throws Exception {
        // Arrange
        doNothing().when(facade).eliminar(1L);
 
        // Act & Assert
        mockMvc.perform(delete("/pedidos/1"))
                .andExpect(status().isNoContent());
 
        verify(facade, times(1)).eliminar(1L);
    }
 
    /**
     * Caso de error: eliminar un pedido inexistente cuando el facade lanza excepción retorna 500.
     */
    @Test
    void eliminar_cuandoNoExiste_retorna500() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Pedido no encontrado: 99")).when(facade).eliminar(99L);
 
        // Act & Assert
        mockMvc.perform(delete("/pedidos/99"))
                .andExpect(status().isInternalServerError());
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────────────────────────────────
 
    private Pedido buildPedido(Long id, Integer estadoId, String estadoNombre) {
        Pedido p = new Pedido();
        p.setId(id);
        p.setEstadoId(estadoId);
        p.setEstadoNombre(estadoNombre);
        p.setFechaActualizacion(LocalDateTime.now());
        return p;
    }
}
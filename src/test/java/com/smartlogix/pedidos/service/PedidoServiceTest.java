package com.smartlogix.pedidos.service;

import com.smartlogix.pedidos.exception.NotFoundException;
import com.smartlogix.pedidos.model.Estado;
import com.smartlogix.pedidos.model.EstadoPedidoActual;
import com.smartlogix.pedidos.model.Pedido;
import com.smartlogix.pedidos.repository.EstadoPedidoActualRepository;
import com.smartlogix.pedidos.repository.EstadoRepository;
import com.smartlogix.pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de PedidoService (estructura AAA).
 *
 * Arrange común: repositorios (PedidoRepository, EstadoRepository,
 * EstadoPedidoActualRepository) y dos observadores mockeados; el servicio se
 * construye con la lista de observadores. Las dependencias externas se aíslan
 * con mocks.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoService – pruebas unitarias")
class PedidoServiceTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private EstadoRepository estadoRepository;
    @Mock private EstadoPedidoActualRepository estadoPedidoActualRepository;
    @Mock private OrdenObserver observador1;
    @Mock private OrdenObserver observador2;

    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        pedidoService = new PedidoService(
                pedidoRepository,
                estadoRepository,
                estadoPedidoActualRepository,
                Arrays.asList(observador1, observador2));
    }

    private Pedido pedidoConId(Long id) {
        Pedido p = new Pedido(2, 1000L, 10L, 20L);
        p.setId(id);
        return p;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // crearPedido
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("crearPedido – persiste, registra estado Pendiente y notifica")
    void crearPedido_datosValidos_persisteRegistraEstadoYNotifica() {
        // ARRANGE
        Pedido guardado = pedidoConId(1L);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(guardado);
        when(estadoRepository.findById(PedidoService.ESTADO_INICIAL))
                .thenReturn(Optional.of(new Estado(1L, "Pendiente")));

        // ACT
        Pedido resultado = pedidoService.crearPedido(2, 1000L, 10L, 20L);

        // ASSERT
        assertThat(resultado).isSameAs(guardado);

        ArgumentCaptor<EstadoPedidoActual> captor = ArgumentCaptor.forClass(EstadoPedidoActual.class);
        verify(estadoPedidoActualRepository).save(captor.capture());
        assertThat(captor.getValue().getPedidoId()).isEqualTo(1L);
        assertThat(captor.getValue().getEstadoId()).isEqualTo(1L);
        assertThat(captor.getValue().getFecha()).isNotNull();

        verify(observador1).alCambiarEstado(1L, "Pendiente");
        verify(observador2).alCambiarEstado(1L, "Pendiente");
    }

    @Test
    @DisplayName("crearPedido – sin estado inicial en el catálogo lanza 404 y no registra ni notifica")
    void crearPedido_faltaEstadoInicial_lanzaExcepcion() {
        // ARRANGE
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoConId(1L));
        when(estadoRepository.findById(PedidoService.ESTADO_INICIAL)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThatThrownBy(() -> pedidoService.crearPedido(2, 1000L, 10L, 20L))
                .isInstanceOf(NotFoundException.class);

        verify(estadoPedidoActualRepository, never()).save(any());
        verifyNoInteractions(observador1, observador2);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // cambiarEstado
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("cambiarEstado – pedido y estado existen: actualiza, notifica y devuelve el nombre")
    void cambiarEstado_pedidoYEstadoExisten_actualizaYNotifica() {
        // ARRANGE
        Long pedidoId = 1L, estadoId = 3L;
        when(pedidoRepository.existsById(pedidoId)).thenReturn(true);
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(new Estado(3L, "Procesando")));
        when(estadoPedidoActualRepository.findById(pedidoId))
                .thenReturn(Optional.of(new EstadoPedidoActual(pedidoId, 1L, LocalDateTime.now())));

        // ACT
        String nuevoEstado = pedidoService.cambiarEstado(pedidoId, estadoId);

        // ASSERT
        assertThat(nuevoEstado).isEqualTo("Procesando");

        ArgumentCaptor<EstadoPedidoActual> captor = ArgumentCaptor.forClass(EstadoPedidoActual.class);
        verify(estadoPedidoActualRepository).save(captor.capture());
        assertThat(captor.getValue().getEstadoId()).isEqualTo(estadoId);

        verify(observador1).alCambiarEstado(pedidoId, "Procesando");
        verify(observador2).alCambiarEstado(pedidoId, "Procesando");
    }

    @Test
    @DisplayName("cambiarEstado – pedido inexistente lanza 404; no consulta estado, no guarda, no notifica")
    void cambiarEstado_pedidoInexistente_lanzaExcepcion() {
        // ARRANGE
        when(pedidoRepository.existsById(99L)).thenReturn(false);

        // ACT + ASSERT
        assertThatThrownBy(() -> pedidoService.cambiarEstado(99L, 3L))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(estadoRepository, estadoPedidoActualRepository, observador1, observador2);
    }

    @Test
    @DisplayName("cambiarEstado – estado inexistente lanza 404; no guarda ni notifica")
    void cambiarEstado_estadoInexistente_lanzaExcepcion() {
        // ARRANGE
        when(pedidoRepository.existsById(1L)).thenReturn(true);
        when(estadoRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThatThrownBy(() -> pedidoService.cambiarEstado(1L, 999L))
                .isInstanceOf(NotFoundException.class);

        verify(estadoPedidoActualRepository, never()).save(any());
        verifyNoInteractions(observador1, observador2);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // getPedido / listarTodos
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getPedido – pedido existente lo devuelve")
    void getPedido_existente_devuelvePedido() {
        // ARRANGE
        Pedido p = pedidoConId(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(p));

        // ACT + ASSERT
        assertThat(pedidoService.getPedido(1L)).isSameAs(p);
    }

    @Test
    @DisplayName("getPedido – pedido inexistente lanza 404")
    void getPedido_inexistente_lanzaExcepcion() {
        // ARRANGE
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThatThrownBy(() -> pedidoService.getPedido(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("listarTodos – devuelve la lista del repositorio")
    void listarTodos_hayPedidos_devuelveLista() {
        // ARRANGE
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedidoConId(1L), pedidoConId(2L)));

        // ACT + ASSERT
        assertThat(pedidoService.listarTodos()).hasSize(2);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // obtenerEstadoActual
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerEstadoActual – pedido con estado registrado lo devuelve")
    void obtenerEstadoActual_conEstado_devuelveEstado() {
        // ARRANGE
        EstadoPedidoActual actual = new EstadoPedidoActual(1L, 3L, LocalDateTime.now());
        when(estadoPedidoActualRepository.findById(1L)).thenReturn(Optional.of(actual));

        // ACT + ASSERT
        assertThat(pedidoService.obtenerEstadoActual(1L)).isSameAs(actual);
    }

    @Test
    @DisplayName("obtenerEstadoActual – pedido sin estado lanza 404")
    void obtenerEstadoActual_sinEstado_lanzaExcepcion() {
        // ARRANGE
        when(estadoPedidoActualRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThatThrownBy(() -> pedidoService.obtenerEstadoActual(1L))
                .isInstanceOf(NotFoundException.class);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // eliminar
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar – pedido existente borra el estado actual y el pedido")
    void eliminar_existente_borraEstadoYPedido() {
        // ARRANGE
        EstadoPedidoActual actual = new EstadoPedidoActual(1L, 3L, LocalDateTime.now());
        when(pedidoRepository.existsById(1L)).thenReturn(true);
        when(estadoPedidoActualRepository.findById(1L)).thenReturn(Optional.of(actual));

        // ACT
        pedidoService.eliminar(1L);

        // ASSERT
        verify(estadoPedidoActualRepository).delete(actual);
        verify(pedidoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar – pedido inexistente lanza 404; no borra")
    void eliminar_inexistente_lanzaExcepcion() {
        // ARRANGE
        when(pedidoRepository.existsById(99L)).thenReturn(false);

        // ACT + ASSERT
        assertThatThrownBy(() -> pedidoService.eliminar(99L))
                .isInstanceOf(NotFoundException.class);

        verify(pedidoRepository, never()).deleteById(any());
    }
}

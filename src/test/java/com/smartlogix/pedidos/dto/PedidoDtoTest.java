package com.smartlogix.pedidos.dto;

import com.smartlogix.pedidos.model.EstadoPedidoActual;
import com.smartlogix.pedidos.model.Pedido;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTOs de Pedidos – cobertura de records y factory methods")
class PedidoDtoTest {

    @Test
    @DisplayName("CambiarEstadoPedidoRequest – almacena el estadoId correctamente")
    void cambiarEstadoPedidoRequest_almacenaEstadoId() {
        CambiarEstadoPedidoRequest req = new CambiarEstadoPedidoRequest(3L);
        assertThat(req.estadoId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("CrearPedidoRequest – almacena todos los campos")
    void crearPedidoRequest_almacenaCampos() {
        CrearPedidoRequest req = new CrearPedidoRequest(5, 149950L, 1L, 2L);
        assertThat(req.cantidad()).isEqualTo(5);
        assertThat(req.total()).isEqualTo(149950L);
        assertThat(req.usuarioId()).isEqualTo(1L);
        assertThat(req.productoId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("PedidoDTO.from – mapea correctamente la entidad Pedido")
    void pedidoDTO_from_mapeaEntidad() {
        Pedido pedido = new Pedido(3, 75000L, 10L, 20L);
        pedido.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        pedido.setFecha(now);

        PedidoDTO dto = PedidoDTO.from(pedido);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.cantidad()).isEqualTo(3);
        assertThat(dto.total()).isEqualTo(75000L);
        assertThat(dto.usuarioId()).isEqualTo(10L);
        assertThat(dto.productoId()).isEqualTo(20L);
        assertThat(dto.fecha()).isEqualTo(now);
    }

    @Test
    @DisplayName("PedidoEventoDTO – almacena pedidoId, nuevoEstado y usuarioId")
    void pedidoEventoDTO_almacenaCampos() {
        PedidoEventoDTO dto = new PedidoEventoDTO(1L, "Confirmado", 5L);
        assertThat(dto.pedidoId()).isEqualTo(1L);
        assertThat(dto.nuevoEstado()).isEqualTo("Confirmado");
        assertThat(dto.usuarioId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("EstadoPedidoActualDTO.from – mapea entidad con nombre de estado")
    void estadoPedidoActualDTO_from_mapeaEntidad() {
        LocalDateTime now = LocalDateTime.now();
        EstadoPedidoActual actual = new EstadoPedidoActual(1L, 2L, now);

        EstadoPedidoActualDTO dto = EstadoPedidoActualDTO.from(actual, "Confirmado");

        assertThat(dto.pedidoId()).isEqualTo(1L);
        assertThat(dto.estadoId()).isEqualTo(2L);
        assertThat(dto.estadoNombre()).isEqualTo("Confirmado");
        assertThat(dto.fecha()).isEqualTo(now);
    }

    @Test
    @DisplayName("PedidosPorEstadoDTO – almacena estado y cantidad")
    void pedidosPorEstadoDTO_almacenaCampos() {
        PedidosPorEstadoDTO dto = new PedidosPorEstadoDTO("Pendiente", 12L);
        assertThat(dto.estado()).isEqualTo("Pendiente");
        assertThat(dto.cantidad()).isEqualTo(12L);
    }

    @Test
    @DisplayName("TotalPedidosDTO – almacena el total de pedidos")
    void totalPedidosDTO_almacenaTotalPedidos() {
        TotalPedidosDTO dto = new TotalPedidosDTO(57L);
        assertThat(dto.totalPedidos()).isEqualTo(57L);
    }

    @Test
    @DisplayName("Records – equals y hashCode son consistentes")
    void records_equalsHashCode_consistentes() {
        CambiarEstadoPedidoRequest r1 = new CambiarEstadoPedidoRequest(2L);
        CambiarEstadoPedidoRequest r2 = new CambiarEstadoPedidoRequest(2L);
        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }
}

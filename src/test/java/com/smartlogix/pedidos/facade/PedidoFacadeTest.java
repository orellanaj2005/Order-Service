package com.smartlogix.pedidos.facade;

import com.smartlogix.pedidos.exception.NotFoundException;
import com.smartlogix.pedidos.exception.ServiceUnavailableException;
import com.smartlogix.pedidos.model.Pedido;
import com.smartlogix.pedidos.saga.PedidoActualizadoEvent;
import com.smartlogix.pedidos.service.PedidoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de PedidoFacade (estructura AAA).
 *
 * Arrange común: PedidoService y el publicador de eventos mockeados. Verifica la
 * delegación al servicio, la publicación del evento de la Saga y que el fallback
 * del Circuit Breaker no oculte fallos (404 de negocio vs 503 de infraestructura).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoFacade – pruebas unitarias")
class PedidoFacadeTest {

    @Mock private PedidoService pedidoService;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private PedidoFacade pedidoFacade;

    @Test
    @DisplayName("crear – delega al servicio con los datos del pedido")
    void crear_delegaAlServicio() {
        // ARRANGE
        Pedido creado = new Pedido(2, 1000L, 10L, 20L);
        when(pedidoService.crearPedido(2, 1000L, 10L, 20L)).thenReturn(creado);

        // ACT
        Pedido resultado = pedidoFacade.crear(2, 1000L, 10L, 20L);

        // ASSERT
        assertThat(resultado).isSameAs(creado);
        verify(pedidoService).crearPedido(2, 1000L, 10L, 20L);
    }

    @Test
    @DisplayName("cambiarEstado – delega al servicio y publica el evento PedidoActualizado")
    void cambiarEstado_delegaYPublicaEvento() {
        // ARRANGE
        when(pedidoService.cambiarEstado(1L, 5L)).thenReturn("Cancelado");

        // ACT
        pedidoFacade.cambiarEstado(1L, 5L);

        // ASSERT
        verify(pedidoService).cambiarEstado(1L, 5L);

        ArgumentCaptor<PedidoActualizadoEvent> captor = ArgumentCaptor.forClass(PedidoActualizadoEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().getPedidoId()).isEqualTo(1L);
        assertThat(captor.getValue().getNuevoEstado()).isEqualTo("Cancelado");
    }

    @Test
    @DisplayName("fallbackCambiarEstado – error de negocio (404): re-lanza la excepción, no la enmascara")
    void fallbackCambiarEstado_errorNegocio_relanza404() {
        // ARRANGE
        NotFoundException negocio = new NotFoundException("Pedido no encontrado: 1");

        // ACT + ASSERT
        assertThatThrownBy(() -> pedidoFacade.fallbackCambiarEstado(1L, 5L, negocio))
                .isSameAs(negocio);
    }

    @Test
    @DisplayName("fallbackCambiarEstado – fallo de infraestructura: lanza 503, no un falso 200")
    void fallbackCambiarEstado_falloInfraestructura_lanza503() {
        // ARRANGE
        RuntimeException infra = new RuntimeException("circuito abierto");

        // ACT + ASSERT
        assertThatThrownBy(() -> pedidoFacade.fallbackCambiarEstado(1L, 5L, infra))
                .isInstanceOf(ServiceUnavailableException.class);
    }
}

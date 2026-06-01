package com.smartlogix.pedidos.service;
 
import com.smartlogix.pedidos.model.Pedido;
import com.smartlogix.pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
 
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
 
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {
 
    @Mock
    private PedidoRepository repository;
 
    @Mock
    private OrdenObserver observer1;
 
    @Mock
    private OrdenObserver observer2;
 
    private PedidoService service;
 
    @BeforeEach
    void setUp() {
        // Se construye manualmente para poder inyectar la lista de observers mockeados
        service = new PedidoService(repository, Arrays.asList(observer1, observer2));
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    // getPedido
    // ─────────────────────────────────────────────────────────────────────────
 
    /**
     * Caso exitoso: buscar un pedido que existe por su ID.
     */
    @Test
    void getPedido_cuandoExiste_retornaPedido() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstadoId(1);
        pedido.setEstadoNombre("PENDIENTE");
        when(repository.findById(1L)).thenReturn(Optional.of(pedido));
 
        // Act
        Pedido resultado = service.getPedido(1L);
 
        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("PENDIENTE", resultado.getEstadoNombre());
    }
 
    /**
     * Caso de error: buscar un pedido que NO existe debe lanzar RuntimeException.
     */
    @Test
    void getPedido_cuandoNoExiste_lanzaExcepcion() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());
 
        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getPedido(99L));
        assertTrue(ex.getMessage().contains("Pedido no encontrado: 99"));
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    // listarTodos
    // ─────────────────────────────────────────────────────────────────────────
 
    /**
     * Caso exitoso: listar todos los pedidos cuando hay registros.
     */
    @Test
    void listarTodos_cuandoHayPedidos_retornaLista() {
        // Arrange
        Pedido p1 = new Pedido();
        p1.setId(1L);
        Pedido p2 = new Pedido();
        p2.setId(2L);
        when(repository.findAll()).thenReturn(Arrays.asList(p1, p2));
 
        // Act
        List<Pedido> resultado = service.listarTodos();
 
        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }
 
    /**
     * Caso alternativo: listar pedidos cuando no hay ninguno retorna lista vacía.
     */
    @Test
    void listarTodos_cuandoNohayPedidos_retornaListaVacia() {
        // Arrange
        when(repository.findAll()).thenReturn(Collections.emptyList());
 
        // Act
        List<Pedido> resultado = service.listarTodos();
 
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    // crear
    // ─────────────────────────────────────────────────────────────────────────
 
    /**
     * Caso exitoso: crear un pedido con estado inicial válido.
     */
    @Test
    void crear_conDatosValidos_retornaPedidoGuardado() {
        // Arrange
        Pedido pedidoGuardado = new Pedido();
        pedidoGuardado.setId(10L);
        pedidoGuardado.setEstadoId(1);
        pedidoGuardado.setEstadoNombre("PENDIENTE");
        pedidoGuardado.setFechaActualizacion(LocalDateTime.now());
        when(repository.save(any(Pedido.class))).thenReturn(pedidoGuardado);
 
        // Act
        Pedido resultado = service.crear(1, "PENDIENTE");
 
        // Assert
        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals(1, resultado.getEstadoId());
        assertEquals("PENDIENTE", resultado.getEstadoNombre());
        verify(repository, times(1)).save(any(Pedido.class));
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    // actualizarEstadoPedido
    // ─────────────────────────────────────────────────────────────────────────
 
    /**
     * Caso exitoso: actualizar el estado de un pedido existente notifica a todos los observers.
     */
    @Test
    void actualizarEstadoPedido_cuandoExiste_guardaYNotificaObservers() {
        // Arrange
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(1L);
        pedidoExistente.setEstadoId(1);
        pedidoExistente.setEstadoNombre("PENDIENTE");
        when(repository.findById(1L)).thenReturn(Optional.of(pedidoExistente));
        when(repository.save(any(Pedido.class))).thenReturn(pedidoExistente);
 
        // Act
        service.actualizarEstadoPedido(1L, 2, "EN_PROCESO");
 
        // Assert
        verify(repository, times(1)).save(any(Pedido.class));
        verify(observer1, times(1)).alCambiarEstado(1L, "EN_PROCESO");
        verify(observer2, times(1)).alCambiarEstado(1L, "EN_PROCESO");
    }
 
    /**
     * Caso alternativo: actualizar el estado de un pedido que no existe
     * crea uno nuevo con el ID indicado (comportamiento de orElse(new Pedido())).
     */
    @Test
    void actualizarEstadoPedido_cuandoNoExiste_creaPedidoNuevo() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());
        when(repository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));
 
        // Act
        service.actualizarEstadoPedido(99L, 3, "CANCELADO");
 
        // Assert
        verify(repository, times(1)).save(argThat(p ->
                p.getId().equals(99L) && "CANCELADO".equals(p.getEstadoNombre())
        ));
        verify(observer1, times(1)).alCambiarEstado(99L, "CANCELADO");
        verify(observer2, times(1)).alCambiarEstado(99L, "CANCELADO");
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    // eliminar
    // ─────────────────────────────────────────────────────────────────────────
 
    /**
     * Caso exitoso: eliminar un pedido invoca deleteById en el repositorio.
     */
    @Test
    void eliminar_conIdValido_invocaDeleteById() {
        // Arrange
        doNothing().when(repository).deleteById(1L);
 
        // Act
        service.eliminar(1L);
 
        // Assert
        verify(repository, times(1)).deleteById(1L);
    }
}
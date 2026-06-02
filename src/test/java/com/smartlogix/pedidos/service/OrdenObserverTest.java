package com.smartlogix.pedidos.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.*;

/**
 * Pruebas unitarias de los observadores del pedido (patrón Observer).
 *
 * Arrange común: instancia del observador; se captura la salida estándar para
 * verificar la reacción al cambio de estado.
 */
@DisplayName("OrdenObserver – pruebas unitarias")
class OrdenObserverTest {

    private String capturarSalida(Runnable accion) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            accion.run();
        } finally {
            System.setOut(original);
        }
        return out.toString();
    }

    @Test
    @DisplayName("NotificacionSistemaObserver – registra el aviso del cambio del pedido")
    void notificacionObserver_cualquierCambio_registraAviso() {
        // ARRANGE
        OrdenObserver observer = new NotificacionSistemaObserver();

        // ACT
        String salida = capturarSalida(() -> observer.alCambiarEstado(1L, "Procesando"));

        // ASSERT
        assertThat(salida).contains("[Observer - Notificación]").contains("1").contains("Procesando");
    }

    @Test
    @DisplayName("InventarioObserver – estado 'Cancelado' dispara la reposición de stock")
    void inventarioObserver_cancelado_reponeStock() {
        // ARRANGE
        OrdenObserver observer = new InventarioObserver();

        // ACT
        String salida = capturarSalida(() -> observer.alCambiarEstado(1L, "Cancelado"));

        // ASSERT
        assertThat(salida).contains("[Observer - Inventario]");
    }

    @Test
    @DisplayName("InventarioObserver – estado distinto de 'Cancelado' no repone stock")
    void inventarioObserver_noCancelado_noReponeStock() {
        // ARRANGE
        OrdenObserver observer = new InventarioObserver();

        // ACT
        String salida = capturarSalida(() -> observer.alCambiarEstado(1L, "Procesando"));

        // ASSERT
        assertThat(salida).doesNotContain("[Observer - Inventario]");
    }
}

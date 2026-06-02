package com.smartlogix.pedidos.saga;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.*;

/**
 * Pruebas unitarias de PedidoSaga (patrón Saga – Choreography).
 *
 * Arrange común: instancia de la Saga y un evento de cambio de estado; se captura
 * la salida estándar para verificar si se ejecuta la compensación.
 */
@DisplayName("PedidoSaga – pruebas unitarias")
class PedidoSagaTest {

    private final PedidoSaga saga = new PedidoSaga();

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
    @DisplayName("onPedidoActualizado – estado 'Cancelado' ejecuta la compensación (liberar stock)")
    void onPedidoActualizado_cancelado_ejecutaCompensacion() {
        // ARRANGE
        PedidoActualizadoEvent evento = new PedidoActualizadoEvent(this, 1L, "Cancelado");

        // ACT
        String salida = capturarSalida(() -> saga.onPedidoActualizado(evento));

        // ASSERT
        assertThat(salida).contains("[Saga - Compensación]");
    }

    @Test
    @DisplayName("onPedidoActualizado – estado distinto de 'Cancelado' no ejecuta compensación")
    void onPedidoActualizado_noCancelado_noCompensa() {
        // ARRANGE
        PedidoActualizadoEvent evento = new PedidoActualizadoEvent(this, 1L, "Procesando");

        // ACT
        String salida = capturarSalida(() -> saga.onPedidoActualizado(evento));

        // ASSERT
        assertThat(salida).doesNotContain("[Saga - Compensación]");
    }
}

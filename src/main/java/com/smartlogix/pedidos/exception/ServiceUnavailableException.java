package com.smartlogix.pedidos.exception;

/**
 * Servicio temporalmente no disponible. La lanza el fallback del Circuit Breaker
 * cuando el circuito está abierto, para que el cliente reciba un 503 explícito en
 * lugar de un falso 200 OK. Se mapea a HTTP 503.
 */
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}

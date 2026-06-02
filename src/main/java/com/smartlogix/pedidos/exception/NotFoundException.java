package com.smartlogix.pedidos.exception;

/**
 * Recurso no encontrado (ej. pedido o estado inexistente). Se mapea a HTTP 404.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

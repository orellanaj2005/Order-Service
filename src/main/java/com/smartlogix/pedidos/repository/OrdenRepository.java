//repository/OrderRepository.java

//package com.smartlogix.ordenes.repository;

public class OrdenRepository {
    private static OrdenRepository instance;

    private OrdenRepository() {} // Constructor privado

    public static OrdenRepository getInstance() {
        if (instance == null) {
            instance = new OrdenRepository();
        }
        return instance;
    }

    public void registrarEstado(Long ordenId, Integer estadoId) {
        // Aquí iría el JDBC para insertar en la tabla ESTADO_PEDIDO_ACTUAL
        System.out.println("DB [Orden-Service]: Orden #" + ordenId + " actualizada al estado ID: " + estadoId);
    }
}
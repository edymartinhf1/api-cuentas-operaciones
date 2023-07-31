package com.bootcamp.bank.operaciones.model.monedero.p2p;

import lombok.Data;

@Data
public class OperacionP2PRequest {

    private String idClienteSolicitante; //Solicita
    private String numeroCelular;
    private String numeroCuenta;
    private String modoPago; // YANKI TRANSFERENCIA
    private Double monto;
}

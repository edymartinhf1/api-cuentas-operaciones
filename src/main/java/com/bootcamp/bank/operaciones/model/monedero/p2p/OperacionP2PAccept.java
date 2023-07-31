package com.bootcamp.bank.operaciones.model.monedero.p2p;

import lombok.Data;

@Data
public class OperacionP2PAccept {

    private String idClienteAceptante; // Aceptante
    private String numeroCelularAceptante;
}

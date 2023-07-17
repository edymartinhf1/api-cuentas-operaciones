package com.bootcamp.bank.operaciones.model;

import lombok.Data;

@Data
public class OperacionCtaPost {
    private String id;
    private String idCliente;
    private String tipoOperacion; // DEP = deposito , RET = RETIRO
    private String numeroCuenta;
    private Double importe;
}
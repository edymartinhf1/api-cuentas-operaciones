package com.bootcamp.bank.operaciones.model;

import lombok.Data;

@Data
public class Comision {
    private String idCliente;
    private String idOperacion;
    private String numeroCuenta;
    private Double importe;
    private Boolean afectoComision;
    private Double montoComision;
}

package com.bootcamp.bank.operaciones.model;

import lombok.Data;

import java.util.List;

@Data
public class ConsolidadoComisionesCuenta {
    private String idCliente;
    private Cliente cliente;
    private List<Cuenta> cuentas;

}

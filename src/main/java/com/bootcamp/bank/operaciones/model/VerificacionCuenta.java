package com.bootcamp.bank.operaciones.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VerificacionCuenta {
    private String numeroCuenta;
    private LocalDateTime fechaCreacion;
    private String fechaCreacionT;
    private String flgCuentaPrincipal;
    private Boolean flgSaldoCubierto;
    private Double saldoDisponible;
    private Double saldoConsumir;

}

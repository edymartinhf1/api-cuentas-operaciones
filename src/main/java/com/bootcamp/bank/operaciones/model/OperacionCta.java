package com.bootcamp.bank.operaciones.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperacionCta {
    private String id;
    private String idCliente;
    private String tipoOperacion; // DEP = deposito , RET = RETIRO
    private LocalDateTime fechaOperacion;
    private String numeroTarjetaDebito;
    private String medioPago; // EFE = Efectivo / TAR = Tarjeta Debito ,
    private String fechaOperacionT;
    private String numeroCuenta;
    private Boolean afectoComision;
    private Double importe;
    private Double comision;
    private String flgDistribucion;
    }

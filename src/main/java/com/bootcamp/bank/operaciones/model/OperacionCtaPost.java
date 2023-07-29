package com.bootcamp.bank.operaciones.model;

import lombok.Data;

@Data
public class OperacionCtaPost {
    private String id;
    private String idCliente;
    private String medioPago; // EFEC = Efectivo / TARD = Tarjeta Debito , // MONM = Monedero movil
    private String tipoOperacion; // DEP = deposito , RET = RETIRO
    private String numeroTarjetaDebito;
    private String numeroCuenta;
    private String numeroMonedero;
    private Double importe;
}
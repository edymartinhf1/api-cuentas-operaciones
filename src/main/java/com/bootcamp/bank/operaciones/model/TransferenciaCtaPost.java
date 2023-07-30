package com.bootcamp.bank.operaciones.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransferenciaCtaPost {
    private String id;
    private String idClienteEmisor;
    private String idClienteReceptor;
    private String cuentaEmisora;
    private String cuentaReceptora;
    private String numeroCelularEmisor;
    private String numeroCelularReceptor;
    private String numeroMonederoEmisor;
    private String numeroMonederoReceptor;
    private LocalDateTime fechaOperacion;
    private String tipoTransferencia; //  "PROPI"  "TERC" "INTB"  "MONM" = Monedero movil , MP2P = Monedero P2P
    private Double importeTransferido;
}

package com.bootcamp.bank.operaciones.model;

import lombok.Data;

@Data
public class TransferenciaCtaPost {
    private String idClienteEmisor;
    private String idClienteReceptor;
    private String cuentaEmisora;
    private String cuentaReceptora;
    private String tipoTransferencia; //  "PROPI"  "TERC" "INTB"
    private Double importeTransferido;
}

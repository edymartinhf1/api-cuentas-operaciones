package com.bootcamp.bank.operaciones.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransferenciaCta {

    private String idClienteEmisor;
    private String idClienteReceptor;
    private String cuentaEmisora;
    private String cuentaReceptora;
    private LocalDateTime fechaOperacion;
    private String tipoTransferencia; //  "PROPI"  "TERC" "INTB"
    private Double importeTransferido;


}

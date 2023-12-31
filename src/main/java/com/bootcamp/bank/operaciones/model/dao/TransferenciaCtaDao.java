package com.bootcamp.bank.operaciones.model.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Data
@Document("transferenciacuentas")
public class TransferenciaCtaDao {
    @Id
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
    private String tipoTransferencia; //  "PROP"  "TERC" "INTB" "MONM"
    private Double importeTransferido;
}

package com.bootcamp.bank.operaciones.model.dao;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Data
@Document("transferenciacuentas")
public class TransferenciaCtaDao {
    private String id;
    private String idClienteEmisor;
    private String idClienteReceptor;
    private String cuentaEmisora;
    private String cuentaReceptora;
    private LocalDateTime fechaOperacion;
    private String tipoTransferencia; //  "PROP"  "TERC" "INTB"
    private Double importeTransferido;
}

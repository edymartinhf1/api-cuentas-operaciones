package com.bootcamp.bank.operaciones.model.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("operacionescuenta")
public class OperacionCtaDao {
    @Id
    private String id;
    private String idCliente;
    private String tipoOperacion; // DEP = deposito , RET = RETIRO
    private LocalDateTime fechaOperacion;
    private String fechaOperacionT;
    private String numeroCuenta;
    private Boolean afectoComision;
    private Double importe;
    private Double comision;

}
package com.bootcamp.bank.operaciones.model.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Data
@Document("operacionestarjetadebito")
public class OperacionTarjetaDebitoDao {
    @Id
    private String id;
    private String idCliente;
    private String numeroTarjetaDebito;
    private String tipoOperacion;
    private LocalDateTime fechaOperacion;
    private String fechaOperacionT;
    private Double importe;



}

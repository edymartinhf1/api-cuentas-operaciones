package com.bootcamp.bank.operaciones.model;

import lombok.Data;

@Data
public class Cliente {
    private String id;
    private String tipoCli;
    private String tipoDocumento;
    private String nroDocumento;
    private String nombre;
    private String razonSocial;
    private Double limiteCredito;

}
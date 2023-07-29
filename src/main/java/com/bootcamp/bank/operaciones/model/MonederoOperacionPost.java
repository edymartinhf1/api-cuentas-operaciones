package com.bootcamp.bank.operaciones.model;

import lombok.Data;
@Data
public class MonederoOperacionPost {

    private String id;
    private String idCliente;
    private String tipoOperacion;
    private String numeroMonedero;
    private String numeroCelular;
    private String fechaOperacion;
    private Double importe;
}

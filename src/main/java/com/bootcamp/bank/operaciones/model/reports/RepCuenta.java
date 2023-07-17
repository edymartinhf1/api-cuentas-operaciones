package com.bootcamp.bank.operaciones.model.reports;

import com.bootcamp.bank.operaciones.model.Comision;
import lombok.Data;

import java.util.List;

@Data
public class RepCuenta {
    private String  id;
    private String  idCliente;
    private String  numeroCuenta;
    private List<Comision> comisiones;
}

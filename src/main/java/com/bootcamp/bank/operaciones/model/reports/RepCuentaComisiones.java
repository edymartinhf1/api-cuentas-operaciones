package com.bootcamp.bank.operaciones.model.reports;

import com.bootcamp.bank.operaciones.model.Cliente;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class RepCuentaComisiones {
    private String idCliente;
    private Cliente cliente;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private List<RepCuenta> cuentas;
}

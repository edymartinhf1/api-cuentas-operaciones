package com.bootcamp.bank.operaciones.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
public class Cuenta {
    @Id
    private String  id;
    private String  idCliente;
    private String  numeroCuenta;
    private LocalDateTime  fechaCreacion;
    private String  fechaCreacionT;
    private String  estado;
    private String  tipoCuenta; // AHO: ahorro  , CTE : cuenta corriente , PZF: plazo fijo
    private Boolean flgComisionMantenimiento;
    private Boolean flgLimiteMovMensual;
    private Integer numMaximoMovimientos;
    private Double  montoMinimoApertura;
    private Integer numeroMaximoTransaccionesLibres;

}
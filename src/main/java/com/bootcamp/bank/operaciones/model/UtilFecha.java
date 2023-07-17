package com.bootcamp.bank.operaciones.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UtilFecha {
    private LocalDateTime fechaInicial;
    private LocalDateTime fechaFinal;
}

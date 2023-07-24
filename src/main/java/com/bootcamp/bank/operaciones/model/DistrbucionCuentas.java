package com.bootcamp.bank.operaciones.model;

import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import lombok.Data;

import java.util.List;

@Data
public class DistrbucionCuentas {
    private Boolean consumoCubierto;
    private List<VerificacionCuenta> verificacionCuentaList;
    private List<OperacionCtaDao>  operacionCtaDaoList;

}

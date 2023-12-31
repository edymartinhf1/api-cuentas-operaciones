package com.bootcamp.bank.operaciones.service;

import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OperacionCuentaService {
    Mono<OperacionCtaDao> saveOperation(OperacionCtaDao operationCtaDao);
    Flux<OperacionCtaDao> findAll();
    Flux<OperacionCtaDao> findByNumeroCuenta(String numeroCuenta);
    Flux<OperacionCtaDao> findByNumeroCuentaAndTipoOperacion(String numeroCuenta,String tipoOperacion);
    Flux<OperacionCtaDao> findOperacionesByIdCliente(String idCliente);
    Flux<OperacionCtaDao> getOperationsByMonth(String numeroCuenta);
    Flux<OperacionCtaDao> findPagosByNumeroCuentaAndBetweenDates(String numeroCuenta,String fechaInicial,String fechaFinal);
    Flux<OperacionCtaDao> findmovsByIdClienteAndNumeroTarjetaDebito(String idCliente,String numeroTarjetaDebito);

}

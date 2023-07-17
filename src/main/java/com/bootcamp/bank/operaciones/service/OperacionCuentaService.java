package com.bootcamp.bank.operaciones.service;

import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import com.bootcamp.bank.operaciones.model.dao.TransferenciaCtaDao;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OperacionCuentaService {
    Mono<OperacionCtaDao> saveOperation(OperacionCtaDao operationCtaDao);
    Mono<TransferenciaCtaDao> saveTransferOperation(TransferenciaCtaDao transferenciaCtaDao);
    Flux<OperacionCtaDao> findAll();
    Flux<OperacionCtaDao> findByNumeroCuenta(String numeroCuenta);
    Flux<OperacionCtaDao> findByNumeroCuentaAndTipoOperacion(String numeroCuenta,String tipoOperacion);
    Flux<OperacionCtaDao> findOperacionesByIdCliente(String idCliente);
    Flux<OperacionCtaDao> getOperationsByMonth(String numeroCuenta);
    Flux<OperacionCtaDao> findPagosByNumeroCuentaAndBetweenDates(String numeroCuenta,String fechaInicial,String fechaFinal);

}

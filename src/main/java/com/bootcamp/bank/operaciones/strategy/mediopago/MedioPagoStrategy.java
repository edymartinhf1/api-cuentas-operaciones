package com.bootcamp.bank.operaciones.strategy.mediopago;

import com.bootcamp.bank.operaciones.clients.ClientApiCuentas;
import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionTarjetaDebitoRepository;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import reactor.core.publisher.Mono;

public interface MedioPagoStrategy {
    Mono<OperacionCtaDao> registrarOperacionctaBancaria(
            OperacionesCuentaRepository operacionesCuentaRepository,
            OperacionTarjetaDebitoRepository operacionTarjetaDebitoRepository,
            ClientApiCuentas clientApiCuentas,
            OperacionCtaDao operacionCtaDao
        );
}

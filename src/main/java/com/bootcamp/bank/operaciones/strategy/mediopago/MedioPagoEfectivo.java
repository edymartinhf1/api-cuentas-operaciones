package com.bootcamp.bank.operaciones.strategy.mediopago;

import com.bootcamp.bank.operaciones.clients.ClientApiCuentas;
import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionTarjetaDebitoRepository;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import reactor.core.publisher.Mono;

/**
 * Clase strategia Medio Pago Efectivo
 */
public class MedioPagoEfectivo implements MedioPagoStrategy {
    @Override
    public Mono<OperacionCtaDao> registrarOperacionctaBancaria(
            OperacionesCuentaRepository operacionesCuentaRepository,
            OperacionTarjetaDebitoRepository operacionTarjetaDebitoRepository,
            ClientApiCuentas clientApiCuentas,
            OperacionCtaDao operacionCtaDao
    ) {
        return operacionesCuentaRepository.save(operacionCtaDao);
    }
}

package com.bootcamp.bank.operaciones.strategy.mediopago;

import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionTarjetaDebitoRepository;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import reactor.core.publisher.Mono;

/**
 *
 */
public class MedioPagoEfectivo implements MedioPagoStrategy {
    @Override
    public Mono<OperacionCtaDao> registrarTransferencia(
            OperacionesCuentaRepository operacionesCuentaRepository,
            OperacionTarjetaDebitoRepository operacionTarjetaDebitoRepository,
            OperacionCtaDao operacionCtaDao
    ) {
        return operacionesCuentaRepository.save(operacionCtaDao);
    }
}

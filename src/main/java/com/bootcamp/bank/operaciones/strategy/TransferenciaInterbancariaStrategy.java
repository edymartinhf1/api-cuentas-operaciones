package com.bootcamp.bank.operaciones.strategy;

import com.bootcamp.bank.operaciones.clients.ClientApiClientes;
import com.bootcamp.bank.operaciones.model.dao.TransferenciaCtaDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import com.bootcamp.bank.operaciones.model.dao.repository.TransferenciaCuentaRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Clase Transferencia InterBancarias
 */
@Slf4j
public class TransferenciaInterbancariaStrategy implements TransferenciaStrategy{

    /**
     * Por Implementar
     * @param transferenciaCuentaRepository
     * @param operacionesCuentaRepository
     * @param clientApiClientes
     * @param transferenciaCtaDao
     * @return
     */
    @Override
    public Mono<TransferenciaCtaDao> registrarTransferencia(TransferenciaCuentaRepository transferenciaCuentaRepository, OperacionesCuentaRepository operacionesCuentaRepository, ClientApiClientes clientApiClientes, TransferenciaCtaDao transferenciaCtaDao) {

        return Mono.just(new TransferenciaCtaDao());
    }
}

package com.bootcamp.bank.operaciones.strategy;

import com.bootcamp.bank.operaciones.clients.ClientApiClientes;
import com.bootcamp.bank.operaciones.model.dao.TransferenciaCtaDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import com.bootcamp.bank.operaciones.model.dao.repository.TransferenciaCuentaRepository;
import reactor.core.publisher.Mono;

public interface TransferenciaStrategy {
    Mono<TransferenciaCtaDao> registrarTransferencia(
                                                    TransferenciaCuentaRepository transferenciaCuentaRepository,
                                                    OperacionesCuentaRepository operacionesCuentaRepository,
                                                    ClientApiClientes clientApiClientes,
                                                    TransferenciaCtaDao transferenciaCtaDao);
}

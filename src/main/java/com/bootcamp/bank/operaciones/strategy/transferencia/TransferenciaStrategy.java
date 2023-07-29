package com.bootcamp.bank.operaciones.strategy.transferencia;

import com.bootcamp.bank.operaciones.clients.ClientApiClientes;
import com.bootcamp.bank.operaciones.model.dao.TransferenciaCtaDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import com.bootcamp.bank.operaciones.model.dao.repository.TransferenciaCuentaRepository;
import com.bootcamp.bank.operaciones.producer.KafkaMonederoMessageSender;
import reactor.core.publisher.Mono;

public interface TransferenciaStrategy {
    Mono<TransferenciaCtaDao> registrarTransferencia(
                                                    TransferenciaCuentaRepository transferenciaCuentaRepository,
                                                    OperacionesCuentaRepository operacionesCuentaRepository,
                                                    ClientApiClientes clientApiClientes,
                                                    KafkaMonederoMessageSender kafkaMessageSender,
                                                    TransferenciaCtaDao transferenciaCtaDao
                                                    );
}

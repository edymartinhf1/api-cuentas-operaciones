package com.bootcamp.bank.operaciones.strategy.transferencia;

import com.bootcamp.bank.operaciones.clients.ClientApiClientes;
import com.bootcamp.bank.operaciones.model.dao.TransferenciaCtaDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import com.bootcamp.bank.operaciones.model.dao.repository.TransferenciaCuentaRepository;
import com.bootcamp.bank.operaciones.producer.KafkaMonederoMessageSender;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

/**
 * Clase Transferencia InterBancarias
 */
@Log4j2
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
    public Mono<TransferenciaCtaDao> registrarTransferencia(
            TransferenciaCuentaRepository transferenciaCuentaRepository,
            OperacionesCuentaRepository operacionesCuentaRepository,
            ClientApiClientes clientApiClientes,
            KafkaMonederoMessageSender kafkaMessageSender,
            TransferenciaCtaDao transferenciaCtaDao

    ) {

        return Mono.just(new TransferenciaCtaDao());
    }
}

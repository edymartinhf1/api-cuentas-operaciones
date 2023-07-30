package com.bootcamp.bank.operaciones.strategy.transferencia;

import com.bootcamp.bank.operaciones.clients.ClientApiClientes;
import com.bootcamp.bank.operaciones.exception.BusinessException;
import com.bootcamp.bank.operaciones.model.MonederoOperacionPost;
import com.bootcamp.bank.operaciones.model.Response;
import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import com.bootcamp.bank.operaciones.model.dao.TransferenciaCtaDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import com.bootcamp.bank.operaciones.model.dao.repository.TransferenciaCuentaRepository;
import com.bootcamp.bank.operaciones.producer.KafkaMonederoMessageSender;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
@Component
@Log4j2
public class TransferenciaMonederoP2PStrategy implements TransferenciaStrategy{
    @Override
    public Mono<TransferenciaCtaDao> registrarTransferencia(
            TransferenciaCuentaRepository transferenciaCuentaRepository,
            OperacionesCuentaRepository operacionesCuentaRepository,
            ClientApiClientes clientApiClientes,
            KafkaMonederoMessageSender kafkaMessageSender,
            TransferenciaCtaDao transferenciaCtaDao
    ) {
        return clientApiClientes.getClienteByNumeroCelular(transferenciaCtaDao.getNumeroCelularEmisor())
                .switchIfEmpty(Mono.error(()->new BusinessException("No existe cliente emisor con el id "+transferenciaCtaDao.getIdClienteEmisor())))
                .flatMap(c-> {
                    return clientApiClientes.getClienteByNumeroCelular(transferenciaCtaDao.getNumeroCelularReceptor())
                            .switchIfEmpty(Mono.error(()->new BusinessException("No existe cliente receptor con el id "+transferenciaCtaDao.getIdClienteReceptor())))
                            .flatMap(d->{
                                OperacionCtaDao operacionCtaCargo=new OperacionCtaDao();
                                operacionCtaCargo.setTipoOperacion("RET");
                                operacionCtaCargo.setNumeroCuenta(transferenciaCtaDao.getCuentaEmisora());
                                operacionCtaCargo.setIdCliente(transferenciaCtaDao.getIdClienteEmisor());
                                operacionCtaCargo.setImporte(transferenciaCtaDao.getImporteTransferido());
                                return operacionesCuentaRepository.save(operacionCtaCargo)
                                        .flatMap(o -> {
                                            OperacionCtaDao operacionCtaAbono = new OperacionCtaDao();
                                            operacionCtaAbono.setTipoOperacion("DEP");
                                            operacionCtaAbono.setIdCliente(transferenciaCtaDao.getIdClienteReceptor());
                                            operacionCtaAbono.setNumeroCuenta(transferenciaCtaDao.getCuentaReceptora());
                                            operacionCtaAbono.setImporte(transferenciaCtaDao.getImporteTransferido());
                                            return operacionesCuentaRepository.save(operacionCtaAbono)
                                                    .flatMap(t -> {
                                                        // Mensajeria KAFKA inicio
                                                        MonederoOperacionPost monederoOperacionRetiro=new MonederoOperacionPost();
                                                        monederoOperacionRetiro.setTipoOperacion("RET");
                                                        monederoOperacionRetiro.setNumeroMonedero(transferenciaCtaDao.getNumeroMonederoEmisor());
                                                        monederoOperacionRetiro.setNumeroCelular(transferenciaCtaDao.getNumeroCelularEmisor());
                                                        monederoOperacionRetiro.setImporte(transferenciaCtaDao.getImporteTransferido());
                                                        Response responseR=kafkaMessageSender.sendOperacionMonedero(monederoOperacionRetiro);
                                                        log.info("retiro "+monederoOperacionRetiro.toString());
                                                        MonederoOperacionPost monederoOperacionDeposito=new MonederoOperacionPost();
                                                        monederoOperacionDeposito.setNumeroMonedero(transferenciaCtaDao.getNumeroMonederoReceptor());
                                                        monederoOperacionDeposito.setNumeroCelular(transferenciaCtaDao.getNumeroCelularReceptor());
                                                        monederoOperacionDeposito.setTipoOperacion("DEP");
                                                        monederoOperacionDeposito.setImporte(transferenciaCtaDao.getImporteTransferido());
                                                        log.info("deposito "+monederoOperacionDeposito.toString());
                                                        Response responseD=kafkaMessageSender.sendOperacionMonedero(monederoOperacionDeposito);

                                                        // Mensajeria KAFKA final
                                                        return transferenciaCuentaRepository.save(transferenciaCtaDao);


                                                    });
                                        });
                            });
                });
    }
}

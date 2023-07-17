package com.bootcamp.bank.operaciones.strategy;

import com.bootcamp.bank.operaciones.clients.ClientApiClientes;
import com.bootcamp.bank.operaciones.exception.BusinessException;
import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import com.bootcamp.bank.operaciones.model.dao.TransferenciaCtaDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import com.bootcamp.bank.operaciones.model.dao.repository.TransferenciaCuentaRepository;
import reactor.core.publisher.Mono;

/**
 * Clase Transferencia Terceros
 */
public class TransferenciaTercerrosStrategy implements TransferenciaStrategy{

    @Override
    public Mono<TransferenciaCtaDao> registrarTransferencia(
            TransferenciaCuentaRepository transferenciaCuentaRepository,
            OperacionesCuentaRepository operacionesCuentaRepository,
            ClientApiClientes clientApiClientes,
            TransferenciaCtaDao transferenciaCtaDao) {

        // verifica cliente emisor
        // verifica cliente receptor
        // registra cargo a emisor
        // registra abono a receptor
        // registra transferencia

        return clientApiClientes.getClientes(transferenciaCtaDao.getIdClienteEmisor())
                .switchIfEmpty(Mono.error(()->new BusinessException("No existe cliente emisor con el id "+transferenciaCtaDao.getIdClienteEmisor())))
                .flatMap(c-> {
                    return clientApiClientes.getClientes(transferenciaCtaDao.getIdClienteReceptor())
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
                                                        return transferenciaCuentaRepository.save(transferenciaCtaDao);
                                                    });
                                        });
                    });
                });
    }
}

package com.bootcamp.bank.operaciones.strategy.transferencia;

import com.bootcamp.bank.operaciones.clients.ClientApiClientes;
import com.bootcamp.bank.operaciones.exception.BusinessException;
import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import com.bootcamp.bank.operaciones.model.dao.TransferenciaCtaDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import com.bootcamp.bank.operaciones.model.dao.repository.TransferenciaCuentaRepository;
import com.bootcamp.bank.operaciones.util.Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


/**
 * Clase Transferencias Propias
 */
@Component
@Log4j2
public class TransferenciaPropiasStrategy implements TransferenciaStrategy{

    @Override
    public Mono<TransferenciaCtaDao> registrarTransferencia(
            TransferenciaCuentaRepository transferenciaCuentaRepository,
            OperacionesCuentaRepository operacionesCuentaRepository,
            ClientApiClientes clientApiClientes,
            TransferenciaCtaDao transferenciaCtaDao
    ) {
        log.info("TransferenciaPropiasStrategy ");
        log.info("TransferenciaPropiasStrategy  type       = "+transferenciaCtaDao.getTipoTransferencia());
        log.info("TransferenciaPropiasStrategy  id emisor  = "+transferenciaCtaDao.getIdClienteEmisor());
        // verifica cliente emisor
        // registra cargo a emisor
        // registra abono a receptor (el mismo)
        // registra transferencia

        OperacionCtaDao operacionCtaCargo=new OperacionCtaDao();
        operacionCtaCargo.setTipoOperacion("RET");
        operacionCtaCargo.setFechaOperacion(Util.getCurrentLocalDate());
        operacionCtaCargo.setNumeroCuenta(transferenciaCtaDao.getCuentaEmisora());
        operacionCtaCargo.setIdCliente(transferenciaCtaDao.getIdClienteEmisor());
        operacionCtaCargo.setImporte(transferenciaCtaDao.getImporteTransferido());

        return clientApiClientes.getClientes(transferenciaCtaDao.getIdClienteEmisor())
                .switchIfEmpty(Mono.error(()->new BusinessException("No existe cliente con el id "+transferenciaCtaDao.getIdClienteEmisor())))
                .flatMap(c-> {
                    return operacionesCuentaRepository.save(operacionCtaCargo)
                        .flatMap(o -> {
                            OperacionCtaDao operacionCtaAbono = new OperacionCtaDao();
                            operacionCtaAbono.setTipoOperacion("DEP");
                            operacionCtaAbono.setFechaOperacion(Util.getCurrentLocalDate());
                            operacionCtaAbono.setIdCliente(transferenciaCtaDao.getIdClienteReceptor());
                            operacionCtaAbono.setNumeroCuenta(transferenciaCtaDao.getCuentaReceptora());
                            operacionCtaAbono.setImporte(transferenciaCtaDao.getImporteTransferido());
                            return operacionesCuentaRepository.save(operacionCtaAbono)
                                    .flatMap(t -> {
                                        return transferenciaCuentaRepository.save(transferenciaCtaDao);
                                    });
                        });
                 });



    }


}

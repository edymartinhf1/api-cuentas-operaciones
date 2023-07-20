package com.bootcamp.bank.operaciones.strategy.mediopago;

import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import com.bootcamp.bank.operaciones.model.dao.OperacionTarjetaDebitoDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionTarjetaDebitoRepository;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import com.bootcamp.bank.operaciones.util.Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Clase Medio Pago Tarjeta Debito
 */
@Component
@Log4j2
public class MedioPagoTarjetaDebitoStrategy implements MedioPagoStrategy{
    /**
     * Registrar operacion cta
     * Registrar
     * @param operacionesCuentaRepository
     * @param operacionTarjetaDebitoRepository
     * @param operacionCtaDao
     * @return
     */
    @Override
    public Mono<OperacionCtaDao> registrarTransferencia(
            OperacionesCuentaRepository operacionesCuentaRepository,
            OperacionTarjetaDebitoRepository operacionTarjetaDebitoRepository,
            OperacionCtaDao operacionCtaDao
    ) {

        return operacionesCuentaRepository.save(operacionCtaDao)
                .flatMap( operacionCta -> {
                    OperacionTarjetaDebitoDao operacionTarjetaDebitoDao=new OperacionTarjetaDebitoDao();
                    operacionTarjetaDebitoDao.setFechaOperacion(Util.getCurrentLocalDate());
                    operacionTarjetaDebitoDao.setTipoOperacion(operacionCta.getTipoOperacion());
                    operacionTarjetaDebitoDao.setImporte(operacionCta.getImporte());
                    operacionTarjetaDebitoDao.setNumeroTarjetaDebito(operacionCta.getNumeroTarjetaDebito());
                    return operacionTarjetaDebitoRepository.save(operacionTarjetaDebitoDao)
                            .map(n->{
                                log.info(" operacion "+operacionCta.toString());
                                return  operacionCta;
                            });
                });
    }
}

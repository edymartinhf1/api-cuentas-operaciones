package com.bootcamp.bank.operaciones.service.impl;

import com.bootcamp.bank.operaciones.clients.ClientApiClientes;
import com.bootcamp.bank.operaciones.clients.ClientApiCuentas;
import com.bootcamp.bank.operaciones.exception.BusinessException;
import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionTarjetaDebitoRepository;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import com.bootcamp.bank.operaciones.model.enums.MedioPagoType;
import com.bootcamp.bank.operaciones.service.OperacionCuentaService;
import com.bootcamp.bank.operaciones.strategy.mediopago.MedioPagoStrategy;
import com.bootcamp.bank.operaciones.strategy.mediopago.MedioPagoStrategyFactory;
import com.bootcamp.bank.operaciones.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
@Log4j2
public class OperacionCuentaServiceImpl implements OperacionCuentaService {

    private final OperacionesCuentaRepository operacionesCuentaRepository;

    private final OperacionTarjetaDebitoRepository operacionTarjetaDebitoRepository;

    private final ClientApiClientes clientApiClientes;

    private final ClientApiCuentas clientApiCuentas;

    private final MedioPagoStrategyFactory medioPagoStrategyFactory;

    @Override
    public Mono<OperacionCtaDao> saveOperation(OperacionCtaDao operationCtaDao) {
        operationCtaDao = operacionCta.apply(operationCtaDao);
        OperacionCtaDao finalOperationCtaDao = operationCtaDao;
        return clientApiClientes.getClientes(operationCtaDao.getIdCliente())
                .switchIfEmpty(Mono.error(()->new BusinessException("No existe cliente con el id "+finalOperationCtaDao.getIdCliente())))
                .flatMap(cliente->{
                    log.info("cliente = "+cliente.toString());
                    return clientApiCuentas.getCuenta(finalOperationCtaDao.getNumeroCuenta())
                            .switchIfEmpty(Mono.error(()->new BusinessException("No existe cuenta con el numero "+finalOperationCtaDao.getNumeroCuenta())))
                            .flatMap(cuenta->{
                                log.info("cuenta = "+cuenta.toString());
                                Integer numeroTransaccionesLibres=cuenta.getNumeroMaximoTransaccionesLibres();
                                return this.getOperationsByMonth(cuenta.getNumeroCuenta())
                                        .collectList()
                                        .flatMap(lista->{
                                            if (lista.size()>numeroTransaccionesLibres) {
                                                finalOperationCtaDao.setAfectoComision(true);
                                                finalOperationCtaDao.setComision(20.00);
                                            } else {
                                                finalOperationCtaDao.setAfectoComision(false);
                                                finalOperationCtaDao.setComision(0.00);
                                            }
                                            MedioPagoType medioPagoType= setTipoPago.apply(finalOperationCtaDao.getMedioPago());
                                            MedioPagoStrategy strategy= medioPagoStrategyFactory.getStrategy(medioPagoType);
                                            return strategy.registrarOperacionctaBancaria(
                                                    operacionesCuentaRepository,
                                                    operacionTarjetaDebitoRepository,
                                                    finalOperationCtaDao);

                                        });
                            });

                });
    }

    @Override
    public Flux<OperacionCtaDao> findAll() {
        return operacionesCuentaRepository.findAll();
    }

    @Override
    public Flux<OperacionCtaDao> findByNumeroCuenta(String numeroCuenta) {
        return operacionesCuentaRepository.findByNumeroCuenta(numeroCuenta);
    }

    @Override
    public Flux<OperacionCtaDao> findByNumeroCuentaAndTipoOperacion(String numeroCuenta, String tipoOperacion) {
        return operacionesCuentaRepository.findByNumeroCuentaAndTipoOperacion(numeroCuenta,tipoOperacion);
    }

    @Override
    public Flux<OperacionCtaDao> findOperacionesByIdCliente(String idCliente) {
        return operacionesCuentaRepository.findByIdCliente(idCliente);
    }

    @Override
    public Flux<OperacionCtaDao> getOperationsByMonth(String numeroCuenta) {
        LocalDateTime fecInicial = LocalDateTime.now().with(
                TemporalAdjusters.firstDayOfMonth());
        LocalDateTime fecFinal = LocalDateTime.now().with(
                TemporalAdjusters.lastDayOfMonth());

        return operacionesCuentaRepository.findByNumeroCuentaAndFechaOperacionBetween(numeroCuenta,fecInicial,fecFinal);
    }

    @Override
    public Flux<OperacionCtaDao> findPagosByNumeroCuentaAndBetweenDates(String numeroCuenta, String fechaInicial, String fechaFinal) {
        LocalDateTime fecInicial = Util.getLocalDatefromString(fechaInicial);
        LocalDateTime fecFinal = Util.getLocalDatefromString(fechaFinal);
        return operacionesCuentaRepository.findByNumeroCuentaAndFechaOperacionBetween(numeroCuenta,fecInicial,fecFinal);
    }

    UnaryOperator<OperacionCtaDao> operacionCta = cta -> {
        LocalDateTime fecha = LocalDateTime.now();
        cta.setFechaOperacionT(Util.getCurrentDateAsString("dd/MM/yyyy"));
        cta.setFechaOperacion(fecha);
        return cta;
    };


    Function<String, MedioPagoType> setTipoPago = tipoCuenta  -> {
        MedioPagoType medioPagoType= null;
        switch (tipoCuenta) {
            case "EFEC" -> medioPagoType= MedioPagoType.EFECTIVO;

            case "TARD" -> medioPagoType= MedioPagoType.TARJETA_DEBITO;

            default -> medioPagoType =MedioPagoType.INVALIDO;

        }
        return medioPagoType;
    };

}

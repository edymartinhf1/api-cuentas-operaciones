package com.bootcamp.bank.operaciones.service.impl;

import com.bootcamp.bank.operaciones.clients.ClientApiClientes;
import com.bootcamp.bank.operaciones.clients.ClientApiCuentas;
import com.bootcamp.bank.operaciones.exception.BusinessException;
import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import com.bootcamp.bank.operaciones.model.dao.TransferenciaCtaDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import com.bootcamp.bank.operaciones.model.dao.repository.TransferenciaCuentaRepository;
import com.bootcamp.bank.operaciones.model.enums.CuentasType;
import com.bootcamp.bank.operaciones.service.OperacionCuentaService;
import com.bootcamp.bank.operaciones.strategy.TransferenciaStrategy;
import com.bootcamp.bank.operaciones.strategy.TransferenciaStrategyFactory;
import com.bootcamp.bank.operaciones.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperacionCuentaServiceImpl implements OperacionCuentaService {

    private final OperacionesCuentaRepository operacionesCuentaRepository;

    private final TransferenciaCuentaRepository transferenciaCuentaRepository;

    private final TransferenciaStrategyFactory transferenciaStrategyFactory;

    private final ClientApiClientes clientApiClientes;

    private final ClientApiCuentas clientApiCuentas;

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
                                            return operacionesCuentaRepository.save(finalOperationCtaDao);
                                        });
                            });

                });
    }

    /**
     * Registro de Transferencias
     * @param transferenciaCtaDao
     * @return
     */
    @Override
    public Mono<TransferenciaCtaDao> saveTransferOperation(TransferenciaCtaDao transferenciaCtaDao) {
        transferenciaCtaDao = operacionTransferCta.apply(transferenciaCtaDao);
        CuentasType cuentasType= setTipoCuenta.apply(transferenciaCtaDao.getTipoTransferencia());
        TransferenciaStrategy strategy = transferenciaStrategyFactory.getStrategy(cuentasType);
        return strategy.registrarTransferencia(
                transferenciaCuentaRepository,
                operacionesCuentaRepository,
                clientApiClientes,
                transferenciaCtaDao
        );

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

        log.info("localdate ini "+fecInicial);
        log.info("localdate fin "+fecFinal);

        return operacionesCuentaRepository.findByNumeroCuentaAndFechaOperacionBetween(numeroCuenta,fecInicial,fecFinal);
    }

    @Override
    public Flux<OperacionCtaDao> findPagosByNumeroCuentaAndBetweenDates(String numeroCuenta, String fechaInicial, String fechaFinal) {
        LocalDateTime fecInicial = Util.getLocalDatefromString(fechaInicial);
        LocalDateTime fecFinal = Util.getLocalDatefromString(fechaFinal);
        return operacionesCuentaRepository.findByNumeroCuentaAndFechaOperacionBetween(numeroCuenta,fecInicial,fecFinal);
    }

    Function<OperacionCtaDao,OperacionCtaDao> operacionCta = cta -> {
        LocalDateTime fecha = LocalDateTime.now();
        cta.setFechaOperacionT(Util.getCurrentDateAsString("dd/MM/yyyy"));
        cta.setFechaOperacion(fecha);
        return cta;
    };

    Function<TransferenciaCtaDao,TransferenciaCtaDao> operacionTransferCta = cta -> {
        LocalDateTime fecha = LocalDateTime.now();
        cta.setFechaOperacion(fecha);
        return cta;
    };

    Function<String,CuentasType> setTipoCuenta = tipoCuenta  -> {
        CuentasType cuentasType= null;
        switch (tipoCuenta) {
            case "PROP" -> {
                cuentasType= CuentasType.PROPIA;
            }
            case "TERC" -> {
                cuentasType= CuentasType.TERCEROS;
            }
            case "INTB" -> {
                cuentasType= CuentasType.INTERBANCARIA;
            }
            default -> {
            }
        }
        return cuentasType;
    };
}

package com.bootcamp.bank.operaciones.strategy.mediopago;

import com.bootcamp.bank.operaciones.clients.ClientApiCuentas;
import com.bootcamp.bank.operaciones.exception.BusinessException;
import com.bootcamp.bank.operaciones.model.CuentasSaldo;
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
    public Mono<OperacionCtaDao> registrarOperacionctaBancaria(
            OperacionesCuentaRepository operacionesCuentaRepository,
            OperacionTarjetaDebitoRepository operacionTarjetaDebitoRepository,
            ClientApiCuentas clientApiCuentas,
            OperacionCtaDao operacionCtaDao
    ) {
        if (operacionCtaDao.getMedioPago().equals("TARD")){
            if (operacionCtaDao.getNumeroTarjetaDebito()==null){
                Mono.just(Mono.error(()->new BusinessException("El numero de tarjeta de debito debe de ingresarse..")));
            }
        }

        return this.verificacionSaldoCuentasVinculadas(operacionesCuentaRepository,operacionTarjetaDebitoRepository,clientApiCuentas,operacionCtaDao)
                .flatMap(resultadovalidacion->{
                    log.info(" resultadovalidacion "+resultadovalidacion);
                    if (resultadovalidacion) {
                        return operacionesCuentaRepository.save(operacionCtaDao)
                                .flatMap(operacionCta -> {
                                    OperacionTarjetaDebitoDao operacionTarjetaDebitoDao = new OperacionTarjetaDebitoDao();
                                    operacionTarjetaDebitoDao.setFechaOperacion(Util.getCurrentLocalDate());
                                    operacionTarjetaDebitoDao.setTipoOperacion(operacionCta.getTipoOperacion());
                                    operacionTarjetaDebitoDao.setImporte(operacionCta.getImporte());
                                    operacionTarjetaDebitoDao.setNumeroTarjetaDebito(operacionCta.getNumeroTarjetaDebito());
                                    return operacionTarjetaDebitoRepository.save(operacionTarjetaDebitoDao)
                                            .map(n -> {
                                                log.info(" operacion " + operacionCta.toString());
                                                return operacionCta;
                                            });
                                });
                    } else {
                        return Mono.error(()->new BusinessException("La operacion no pudo completarse"));
                    }
                });



    }

    public Mono<Boolean> verificacionSaldoCuentasVinculadas(
            OperacionesCuentaRepository operacionesCuentaRepository,
            OperacionTarjetaDebitoRepository operacionTarjetaDebitoRepository,
            ClientApiCuentas clientApiCuentas,
            OperacionCtaDao operacionCtaDao
    ){
        // verificar saldo en cuenta bancaria principal
        return clientApiCuentas.getTarjetaDebitoPorNumero(operacionCtaDao.getNumeroTarjetaDebito())
                .flatMap(tarjetaDebito -> {
                    log.info(" tarjeta debito "+tarjetaDebito.toString());
                    return this.validarSaldoCuentaBancaria(tarjetaDebito.getNumeroCuentaPrincipal(),operacionCtaDao.getImporte(),operacionesCuentaRepository)
                            .flatMap( saldoCubierto->{
                                log.info("verificacion de cuentas principal "+saldoCubierto);
                                if (saldoCubierto){
                                    return Mono.just(Boolean.TRUE);
                                } else {
                                    // verificar saldo en cuentas asociadas
                                    log.info("iniciando validacion cuentas asociadas");
                                    return this.validarCuentasAsociadas(clientApiCuentas,operacionCtaDao,operacionesCuentaRepository)
                                            .flatMap(saldocubiertocuentas->{
                                                log.info("verificacion de cuentas asociadas "+saldocubiertocuentas);
                                                if (saldocubiertocuentas){
                                                    return Mono.just(Boolean.TRUE);
                                                } else {
                                                    return Mono.just(Boolean.FALSE);
                                                }
                                            });
                                }
                            });
                });
    }


    public Mono<Double> getOperacionesPorTipo(
            String numeroCuenta,
            String tipo,
            OperacionesCuentaRepository operacionesCuentaRepository
    ) {
        return operacionesCuentaRepository.findByNumeroCuentaAndTipoOperacion(numeroCuenta,tipo)
                .reduce(0.00, (acum,e)->acum+e.getImporte());
    }


    public Mono<Boolean> validarSaldoCuentaBancaria(
            String numeroCuenta,
            Double importeOperacion,
            OperacionesCuentaRepository operacionesCuentaRepository
    ){
        return Mono.zip(
                getOperacionesPorTipo(numeroCuenta,"DEP",operacionesCuentaRepository),
                getOperacionesPorTipo(numeroCuenta,"RET",operacionesCuentaRepository),
                (deposito,retiro)->{
                    log.info("total depositos ="+deposito+" numero cuenta="+numeroCuenta);
                    log.info("total retiros ="+retiro+" numero cuenta="+numeroCuenta);
                    Double saldocuenta = deposito-retiro;
                    log.info("saldo cuenta "+numeroCuenta+" ="+saldocuenta);
                    return saldocuenta > importeOperacion ? Boolean.TRUE : Boolean.FALSE;
                });
    }


    public Mono<Boolean> validarCuentasAsociadas(
            ClientApiCuentas clientApiCuentas,
            OperacionCtaDao operacionCtaDao,
            OperacionesCuentaRepository operacionesCuentaRepository
    ){
        return clientApiCuentas.getCuentasPorTarjetaDebito(operacionCtaDao.getNumeroTarjetaDebito())
                .flatMap(cuenta->{
                    log.info(" validarCuentasAsociadas -> cuenta "+cuenta.toString());

                    return Mono.zip(
                            getOperacionesPorTipo(cuenta.getNumeroCuenta(),"DEP",operacionesCuentaRepository),
                            getOperacionesPorTipo(cuenta.getNumeroCuenta(),"RET",operacionesCuentaRepository),
                            (deposito,retiro)->{
                                log.info("total depositos ="+deposito+" numero cuenta="+cuenta.getNumeroCuenta());
                                log.info("total retiros ="+retiro+" numero cuenta="+cuenta.getNumeroCuenta());
                                Double saldocuenta = deposito+(retiro*-1);
                                log.info("saldo cuenta "+cuenta.getNumeroCuenta()+" ="+saldocuenta);
                                CuentasSaldo cuentasSaldo=new CuentasSaldo();
                                cuentasSaldo.setNumeroCuenta(cuenta.getNumeroCuenta());
                                cuentasSaldo.setFlagSaldoCubierto(saldocuenta > operacionCtaDao.getImporte() ?Boolean.TRUE : Boolean.FALSE);
                                return cuentasSaldo;
                            })
                            .map(cuentasaldo -> {
                                log.info("validacion de cuenta asociada cuenta="+cuenta.getNumeroCuenta()+" saldovalido="+cuentasaldo.getNumeroCuenta());
                                CuentasSaldo cuentasSaldo=new CuentasSaldo();
                                cuentasSaldo.setNumeroCuenta(cuentasaldo.getNumeroCuenta());
                                cuentasSaldo.setFlagSaldoCubierto(cuentasaldo.getFlagSaldoCubierto());
                                return cuentasSaldo;
                            });
                }).collectList()
                .flatMap(lista->{
                    log.info("lista cuentas asociadas "+lista.toString());
                    Boolean cuentasAsociadasCubrensaldo= lista.stream().anyMatch(saldo->saldo.getFlagSaldoCubierto()==true);
                    log.info("validacion de cuentas asociadas"+cuentasAsociadasCubrensaldo);
                    //return cuentasAsociadasCubrensaldo?Mono.just(Boolean.TRUE):Mono.just(Boolean.FALSE);
                    return Mono.just(Boolean.FALSE);
                });



    }


}

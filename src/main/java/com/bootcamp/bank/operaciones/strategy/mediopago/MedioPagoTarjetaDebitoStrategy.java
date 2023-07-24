package com.bootcamp.bank.operaciones.strategy.mediopago;

import com.bootcamp.bank.operaciones.clients.ClientApiCuentas;
import com.bootcamp.bank.operaciones.exception.BusinessException;
import com.bootcamp.bank.operaciones.model.DistrbucionCuentas;
import com.bootcamp.bank.operaciones.model.VerificacionCuenta;
import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import com.bootcamp.bank.operaciones.model.dao.OperacionTarjetaDebitoDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionTarjetaDebitoRepository;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import com.bootcamp.bank.operaciones.util.Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase strategia Medio Pago Tarjeta Debito
 * Entregable 3
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
                .flatMap(distrbucionCuentas -> {
                    if (distrbucionCuentas.getConsumoCubierto()) {
                        List<OperacionCtaDao> operaciones = distrbucionCuentas.getOperacionCtaDaoList();
                        log.info(" operaciones a registrar "+operaciones.toString());
                        Flux<OperacionCtaDao> operacionesCtasDaos = Flux.fromIterable(operaciones);

                        return operacionesCtasDaos.flatMap(operacionCtaD -> {
                                    log.info(" operacion = " + operacionCtaD.toString());
                                    return registroOperacion(operacionesCuentaRepository, operacionTarjetaDebitoRepository, operacionCtaD);
                                })
                                .collectList()
                                .flatMap(list -> {
                                    if (list.size() == 1) {
                                        OperacionCtaDao operacion = list.get(0);
                                        return Mono.just(operacion);
                                    } else {
                                        OperacionCtaDao operacionCtaDaoResponse =  list.stream().filter(operacion-> operacion.getNumeroCuenta().equals(operacionCtaDao.getNumeroCuenta())).limit(1)
                                                .reduce((a,b) -> { throw new RuntimeException("Too many values present"); })
                                                .orElseThrow(() -> { throw new RuntimeException("No value present"); });

                                        return Mono.just(operacionCtaDaoResponse);
                                    }

                                });
                    } else {
                        return Mono.error(()->new BusinessException("La operacion no pudo completarse , saldo insuficiente "));
                    }
                });

    }


    /**
     * Registro de operacion
     * @param operacionesCuentaRepository
     * @param operacionTarjetaDebitoRepository
     * @param operacionCtaDao
     * @return
     */
    private Mono<OperacionCtaDao> registroOperacion(OperacionesCuentaRepository operacionesCuentaRepository,OperacionTarjetaDebitoRepository operacionTarjetaDebitoRepository, OperacionCtaDao operacionCtaDao){
        log.info(" registro operacion "+operacionCtaDao.toString() );
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
    }


    /**
     * @param operacionesCuentaRepository
     * @param operacionTarjetaDebitoRepository
     * @param clientApiCuentas
     * @param operacionCtaDao
     * @return
     */
    private Mono<DistrbucionCuentas> verificacionSaldoCuentasVinculadas(
            OperacionesCuentaRepository operacionesCuentaRepository,
            OperacionTarjetaDebitoRepository operacionTarjetaDebitoRepository,
            ClientApiCuentas clientApiCuentas,
            OperacionCtaDao operacionCtaDao
    ){
        // validar numero de tarjeta de debito
        return clientApiCuentas.getTarjetaDebitoPorNumero(operacionCtaDao.getNumeroTarjetaDebito())
                .flatMap(tarjetaDebito -> {
                    log.info(" tarjeta debito "+tarjetaDebito.toString());

                    // verificar saldo en cuenta bancaria principal
                    return this.validarSaldoCuentaBancaria(tarjetaDebito.getNumeroCuentaPrincipal(),operacionCtaDao.getImporte(),operacionesCuentaRepository)
                            .flatMap( infoCuentaPrincipalsaldo->{
                                log.info("verificacion de saldo cuentas principal "+infoCuentaPrincipalsaldo.getFlgSaldoCubierto());
                                //Si la cuenta principal cubre el consumo
                                if (infoCuentaPrincipalsaldo.getFlgSaldoCubierto()) {
                                    DistrbucionCuentas distrbucionCuentas = new DistrbucionCuentas();
                                    operacionCtaDao.setFlgDistribucion("N"); // flag distrbucion
                                    distrbucionCuentas.setOperacionCtaDaoList(List.of(operacionCtaDao));
                                    distrbucionCuentas.setConsumoCubierto(true);
                                    return Mono.just(distrbucionCuentas);

                                } else {
                                    log.info(" la cuenta principal NO cubre el consumo,  iniciando validacion cuentas asociadas");
                                    return this.validarCuentasAsociadas(clientApiCuentas,operacionCtaDao,operacionesCuentaRepository,infoCuentaPrincipalsaldo);
                                }
                            });
                });
    }

    /**
     * Permite verificar informacion saldo disponible por numero de cuenta
     * @param numeroCuenta
     * @param importeOperacion
     * @param operacionesCuentaRepository
     * @return
     */
    private Mono<VerificacionCuenta> validarSaldoCuentaBancaria(
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
                    VerificacionCuenta verificacionCuenta=new VerificacionCuenta();
                    Double saldocuenta = deposito-retiro;
                    verificacionCuenta.setNumeroCuenta(numeroCuenta);
                    verificacionCuenta.setFlgSaldoCubierto(saldocuenta >= importeOperacion ? Boolean.TRUE : Boolean.FALSE);
                    verificacionCuenta.setSaldoDisponible(saldocuenta);
                    log.info("saldo cuenta "+numeroCuenta+" ="+saldocuenta);
                    return verificacionCuenta;
                });
    }

    private Mono<Double> getOperacionesPorTipo(
            String numeroCuenta,
            String tipo,
            OperacionesCuentaRepository operacionesCuentaRepository
    ) {
        return operacionesCuentaRepository.findByNumeroCuentaAndTipoOperacion(numeroCuenta,tipo)
                .reduce(0.00, (acum,e)->acum+e.getImporte());
    }



    private Mono<DistrbucionCuentas> validarCuentasAsociadas(
            ClientApiCuentas clientApiCuentas,
            OperacionCtaDao operacionCtaDao,
            OperacionesCuentaRepository operacionesCuentaRepository,
            VerificacionCuenta verificacionCuentaPrincipal
    ){
        // procesar lista de tarjetas de debito
        return clientApiCuentas.getCuentasPorTarjetaDebito(operacionCtaDao.getNumeroTarjetaDebito())
                //.filter(cuenta ->  !cuenta.getNumeroCuenta().equals(verificacionCuentaPrincipal.getNumeroCuenta()))
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

                                        VerificacionCuenta verificacionCuenta=new VerificacionCuenta();
                                        verificacionCuenta.setFechaCreacion(cuenta.getFechaCreacion());
                                        verificacionCuenta.setNumeroCuenta(cuenta.getNumeroCuenta());
                                        verificacionCuenta.setFlgSaldoCubierto(saldocuenta > operacionCtaDao.getImporte() ?Boolean.TRUE : Boolean.FALSE);
                                        verificacionCuenta.setSaldoDisponible(saldocuenta);
                                        return verificacionCuenta;
                                    });

                })
                .collectList()
                .flatMap(lista->{
                    lista.sort((a,b)->a.getFechaCreacion().compareTo(b.getFechaCreacion()));
                    verificacionCuentaPrincipal.setFlgCuentaPrincipal("1");
                    //lista.add(verificacionCuentaPrincipal);
                    Flux<VerificacionCuenta> cuentasList= Flux.fromIterable(lista);
                    Double sumatoriaTotalcuentas = cuentasList.reduce(0.00, (acum,e)-> acum + e.getSaldoDisponible()).block();
                    log.info("sumatoria total cuentas ="+sumatoriaTotalcuentas+" importe "+operacionCtaDao.getImporte());

                    if (sumatoriaTotalcuentas>= operacionCtaDao.getImporte()){
                        log.info("cuentas si cubren consumo");
                        DistrbucionCuentas distrbucionCuentas=saldoComprometido(lista,operacionCtaDao);
                        log.info(" "+distrbucionCuentas.toString());
                        return Mono.just(distrbucionCuentas);


                    } else {
                        log.info("cuentas no cubren consumo");
                        // el monto de las cuentas bancarias no cubren el monto necesitado para el consumo
                        DistrbucionCuentas distrbucionCuentas=new DistrbucionCuentas();
                        distrbucionCuentas.setConsumoCubierto(false);
                        return Mono.just(distrbucionCuentas);
                    }



                });



    }

    /**
     * Permite calcular la distrbucion y saldo comprometido de las cuentas
     * @param listaSaldosCuenta
     * @param operacionCtaDao
     * @return
     */
    private DistrbucionCuentas saldoComprometido(List<VerificacionCuenta> listaSaldosCuenta,OperacionCtaDao operacionCtaDao){

        List<OperacionCtaDao> operacionCtaDaoList=new ArrayList<>();
        DistrbucionCuentas distrbucionCuentas=new DistrbucionCuentas();

        Double saldoPagar = operacionCtaDao.getImporte();
        Double montoAcumulado =0.00;

        Double deudaRestante = operacionCtaDao.getImporte();

        log.info(" saldos cuenta "+listaSaldosCuenta.toString() );

        if (!listaSaldosCuenta.isEmpty()) {
            for (VerificacionCuenta cuenta : listaSaldosCuenta) {
                log.info(" numero cuenta " + cuenta.getNumeroCuenta() + " saldo disponible " + cuenta.getSaldoDisponible()+" cuenta principal "+cuenta.getFlgCuentaPrincipal()+" fecha "+cuenta.getFechaCreacion());

                if (cuenta.getSaldoDisponible() <= deudaRestante){
                    // consume todo el saldo de la cuenta
                    log.info(" saldo disponible "+cuenta.getSaldoDisponible());
                    log.info(" deuda restante  "+deudaRestante);
                    OperacionCtaDao operacion = new OperacionCtaDao();
                    Double saldoComprometido = cuenta.getSaldoDisponible();
                    operacion.setNumeroTarjetaDebito(operacionCtaDao.getNumeroTarjetaDebito());
                    operacion.setNumeroCuenta(cuenta.getNumeroCuenta());
                    operacion.setTipoOperacion("RET");
                    operacion.setFechaOperacion(Util.getCurrentLocalDate());
                    operacion.setFechaOperacionT(Util.getCurrentDateAsString("yyyyy-MM-dd"));
                    operacion.setIdCliente(operacionCtaDao.getIdCliente());
                    operacion.setAfectoComision(operacionCtaDao.getAfectoComision());
                    operacion.setMedioPago("TARD");
                    operacion.setImporte(saldoComprometido);
                    operacion.setFlgDistribucion("S"); // flag distribucion
                    operacionCtaDaoList.add(operacion);
                    deudaRestante= deudaRestante - cuenta.getSaldoDisponible();

                } else {
                    //
                    if (cuenta.getSaldoDisponible() > deudaRestante){
                        // consume el saldo restante de la cuenta
                        log.info(" saldo disponible "+cuenta.getSaldoDisponible());
                        log.info(" deuda restante  "+deudaRestante);

                        OperacionCtaDao operacion = new OperacionCtaDao();
                        Double saldoComprometido = deudaRestante;
                        operacion.setNumeroTarjetaDebito(operacionCtaDao.getNumeroTarjetaDebito());
                        operacion.setNumeroCuenta(cuenta.getNumeroCuenta());
                        operacion.setTipoOperacion("RET");
                        operacion.setFechaOperacion(Util.getCurrentLocalDate());
                        operacion.setFechaOperacionT(Util.getCurrentDateAsString("yyyyy-MM-dd"));
                        operacion.setIdCliente(operacionCtaDao.getIdCliente());
                        operacion.setAfectoComision(operacionCtaDao.getAfectoComision());
                        operacion.setMedioPago("TARD");
                        operacion.setImporte(saldoComprometido);
                        operacion.setFlgDistribucion("S"); // flag distribucion
                        operacionCtaDaoList.add(operacion);
                        deudaRestante= deudaRestante - cuenta.getSaldoDisponible();

                    }
                }
            }
        }

        distrbucionCuentas.setConsumoCubierto(true);
        distrbucionCuentas.setOperacionCtaDaoList(operacionCtaDaoList);
        log.info(" lista generada "+operacionCtaDaoList.toString() );
        return distrbucionCuentas;

    }






}

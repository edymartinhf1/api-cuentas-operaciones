package com.bootcamp.bank.operaciones.service.impl;

import com.bootcamp.bank.operaciones.clients.ClientApiClientes;
import com.bootcamp.bank.operaciones.clients.ClientApiCuentas;
import com.bootcamp.bank.operaciones.exception.BusinessException;
import com.bootcamp.bank.operaciones.model.Comision;
import com.bootcamp.bank.operaciones.model.UtilFecha;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import com.bootcamp.bank.operaciones.model.reports.RepCuenta;
import com.bootcamp.bank.operaciones.model.reports.RepCuentaComisiones;
import com.bootcamp.bank.operaciones.service.ComisionesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Supplier;

@Service
@Log4j2
@RequiredArgsConstructor
public class ComisionesServiceImpl implements ComisionesService {

    private final OperacionesCuentaRepository operacionesCuentaRepository;

    private final ClientApiCuentas clientApiCuentas;

    private final ClientApiClientes clientApiClientes;

    @Override
    public Flux<Comision> getComisions() {
        return operacionesCuentaRepository.findAll()
                .filter(operacion-> operacion.getAfectoComision()!=null)
                .filter(operacion-> operacion.getAfectoComision() && operacion.getComision()>0)
                .map(operacion -> {
                    Comision com =new Comision();
                    com.setIdOperacion(operacion.getId());
                    com.setIdCliente(operacion.getIdCliente());
                    com.setNumeroCuenta(operacion.getNumeroCuenta());
                    com.setImporte(operacion.getImporte());
                    com.setAfectoComision(operacion.getAfectoComision());
                    com.setMontoComision(operacion.getComision());
                    return com;
                });

    }

    @Override
    public Mono<RepCuentaComisiones> getComisionsCharged(String idCliente) {

        return clientApiClientes.getClientes(idCliente)
                        .switchIfEmpty(Mono.error(()->new BusinessException("No existe cliente con el id "+idCliente)))
                        .flatMap(cliente-> {
                                    return clientApiCuentas.getCuentasPorIdCliente(idCliente)
                                            .switchIfEmpty(Mono.error(()->new BusinessException("No existen productos cuentas vinculadas al id "+idCliente)))
                                            .map(cuenta -> {
                                                RepCuenta repCuenta = new RepCuenta();
                                                repCuenta.setId(cuenta.getId());
                                                repCuenta.setNumeroCuenta(cuenta.getNumeroCuenta());
                                                repCuenta.setIdCliente(cuenta.getIdCliente());
                                                return repCuenta;
                                            })
                                            .flatMap(repCuenta -> {
                                                return getComisionsChargedByAccount(repCuenta.getNumeroCuenta())
                                                        .collectList()
                                                        .map(comisiones -> {
                                                            repCuenta.setComisiones(comisiones);
                                                            return repCuenta;

                                                        });

                                            }).collectList()
                                            .map(rep -> {
                                                RepCuentaComisiones repCuentaComisiones=new RepCuentaComisiones();
                                                repCuentaComisiones.setFechaInicio(obtenerFechasInicioFinMes.get().getFechaInicial());
                                                repCuentaComisiones.setFechaFin(obtenerFechasInicioFinMes.get().getFechaFinal());
                                                repCuentaComisiones.setIdCliente(cliente.getId());
                                                repCuentaComisiones.setCliente(cliente);
                                                repCuentaComisiones.setCuentas(rep);
                                                return repCuentaComisiones;
                                            });
                        });



    }

    @Override
    public Flux<Comision> getComisionsChargedByAccount(String numeroCuenta) {

        UtilFecha fechas =obtenerFechasInicioFinMes.get();

        return operacionesCuentaRepository.findByNumeroCuentaAndFechaOperacionBetween(numeroCuenta,fechas.getFechaInicial(),fechas.getFechaFinal())
                .filter(operacion-> operacion.getAfectoComision()!=null)
                .filter(operacion-> operacion.getAfectoComision() && operacion.getComision()>0)
                .map(operacion-> {
                    log.info("operacion = "+operacion.toString());
                    Comision com =new Comision();
                    com.setIdOperacion(operacion.getId());
                    com.setIdCliente(operacion.getIdCliente());
                    com.setNumeroCuenta(operacion.getNumeroCuenta());
                    com.setImporte(operacion.getImporte());
                    com.setAfectoComision(operacion.getAfectoComision());
                    com.setMontoComision(operacion.getComision());
                    return com;
                });

    }

    Supplier<UtilFecha> obtenerFechasInicioFinMes = ()->{
        UtilFecha fechas=new UtilFecha();
        LocalDateTime fecInicial = LocalDateTime.now().with(
                TemporalAdjusters.firstDayOfMonth());
        LocalDateTime fecFinal = LocalDateTime.now().with(
                TemporalAdjusters.lastDayOfMonth());
        fechas.setFechaInicial(fecInicial);
        fechas.setFechaFinal(fecFinal);
        return fechas;
    };
}

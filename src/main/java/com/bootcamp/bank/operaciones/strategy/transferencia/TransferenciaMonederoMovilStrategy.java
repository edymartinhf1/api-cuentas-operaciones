package com.bootcamp.bank.operaciones.strategy.transferencia;

import com.bootcamp.bank.operaciones.clients.ClientApiClientes;
import com.bootcamp.bank.operaciones.clients.ClientApiCuentas;
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

/**
 * Permite registrar transferencias de monederoMovil entre clientes del mismo banco;
 */
@Component
@Log4j2
public class TransferenciaMonederoMovilStrategy implements TransferenciaStrategy{




    /**
     * Metodo de transferencia de un cliente a otro por monedero movil del mismo banco
     *
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
            ClientApiCuentas clientApiCuentas,
            KafkaMonederoMessageSender kafkaMessageSender,
            TransferenciaCtaDao transferenciaCtaDao
    ) {
        // busqueda usuario emisor monedero
        return clientApiClientes.getClienteByNumeroCelular(transferenciaCtaDao.getNumeroCelularEmisor())
                .flatMap(clienteEmisor -> {
                    log.info(" cliente emisor "+clienteEmisor.toString());
                    // busqueda cuenta emisor
                    return clientApiCuentas.getCuentasPorIdCliente(clienteEmisor.getId())
                            .filter(cuentaEmi -> cuentaEmi.getTipoCuenta().equals("AHO"))
                            .elementAt(0)
                            .flatMap(cuentaEmisora -> {
                                log.info("cuenta emisora "+cuentaEmisora.toString());
                                // busqueda usuario receptor monedero
                                return clientApiClientes.getClienteByNumeroCelular(transferenciaCtaDao.getNumeroCelularReceptor())
                                        .switchIfEmpty(Mono.error(() -> new BusinessException("No existe cliente con el numero celular " + transferenciaCtaDao.getNumeroCelularReceptor())))
                                        .flatMap(clienteReceptor -> {
                                            log.info("cliente receptor "+clienteReceptor.toString());
                                            // busqueda cuenta receptor
                                            return clientApiCuentas.getCuentasPorIdCliente(clienteReceptor.getId())
                                                    .filter(cuentaRecep -> cuentaRecep.getTipoCuenta().equals("AHO"))
                                                    .elementAt(0)
                                                    .flatMap(cuentaReceptora -> {

                                                        OperacionCtaDao operacionCtaCargo = new OperacionCtaDao();
                                                        operacionCtaCargo.setTipoOperacion("RET");
                                                        operacionCtaCargo.setNumeroCuenta(cuentaEmisora.getNumeroCuenta());
                                                        operacionCtaCargo.setIdCliente(clienteEmisor.getId());

                                                        operacionCtaCargo.setImporte(transferenciaCtaDao.getImporteTransferido());
                                                        return operacionesCuentaRepository.save(operacionCtaCargo)
                                                                .flatMap(o -> {
                                                                    OperacionCtaDao operacionCtaAbono = new OperacionCtaDao();
                                                                    operacionCtaAbono.setTipoOperacion("DEP");
                                                                    operacionCtaAbono.setNumeroCuenta(cuentaReceptora.getNumeroCuenta());
                                                                    operacionCtaAbono.setIdCliente(clienteReceptor.getId());
                                                                    operacionCtaAbono.setImporte(transferenciaCtaDao.getImporteTransferido());
                                                                    return operacionesCuentaRepository.save(operacionCtaAbono)
                                                                            .flatMap(t -> {
                                                                                // Mensajeria KAFKA inicio
                                                                                MonederoOperacionPost monederoOperacionRetiro = new MonederoOperacionPost();
                                                                                monederoOperacionRetiro.setTipoOperacion("RET");
                                                                                monederoOperacionRetiro.setIdCliente(operacionCtaCargo.getIdCliente());
                                                                                monederoOperacionRetiro.setNumeroCelular(transferenciaCtaDao.getNumeroCelularEmisor());
                                                                                //monederoOperacionRetiro.setNumeroMonedero(transferenciaCtaDao.getNumeroMonederoEmisor());
                                                                                //monederoOperacionRetiro.setNumeroCelular(transferenciaCtaDao.getNumeroCelularEmisor());
                                                                                monederoOperacionRetiro.setImporte(transferenciaCtaDao.getImporteTransferido());
                                                                                Response responseR = kafkaMessageSender.sendOperacionMonedero(monederoOperacionRetiro);
                                                                                log.info("retiro " + monederoOperacionRetiro.toString());
                                                                                MonederoOperacionPost monederoOperacionDeposito = new MonederoOperacionPost();
                                                                                monederoOperacionDeposito.setIdCliente(operacionCtaAbono.getIdCliente());
                                                                                monederoOperacionDeposito.setNumeroCelular(transferenciaCtaDao.getNumeroCelularReceptor());
                                                                                //monederoOperacionDeposito.setNumeroMonedero(transferenciaCtaDao.getNumeroMonederoReceptor());
                                                                                //monederoOperacionDeposito.setNumeroCelular(transferenciaCtaDao.getNumeroCelularReceptor());
                                                                                monederoOperacionDeposito.setTipoOperacion("DEP");
                                                                                monederoOperacionDeposito.setImporte(transferenciaCtaDao.getImporteTransferido());
                                                                                log.info("deposito " + monederoOperacionDeposito.toString());
                                                                                Response responseD = kafkaMessageSender.sendOperacionMonedero(monederoOperacionDeposito);

                                                                                // Mensajeria KAFKA final
                                                                                return transferenciaCuentaRepository.save(transferenciaCtaDao).map(transferencia->{
                                                                                    transferencia.setIdClienteEmisor(operacionCtaCargo.getIdCliente());
                                                                                    transferencia.setCuentaEmisora(operacionCtaCargo.getNumeroCuenta());
                                                                                    transferencia.setIdClienteReceptor(operacionCtaAbono.getIdCliente());
                                                                                    transferencia.setCuentaReceptora(operacionCtaAbono.getNumeroCuenta());
                                                                                    return transferencia;
                                                                                });


                                                                            });
                                                                });

                                                    });


                                        });

                            });

                });
                /*
                clientApiClientes.getClienteByNumeroCelular(transferenciaCtaDao.getNumeroCelularEmisor())
                .switchIfEmpty(Mono.error(()->new BusinessException("No existe cliente emisor con el numero celular "+transferenciaCtaDao.getNumeroCelularEmisor()))
                .flatMap(cliente-> {

                    return Mono.just(new TransferenciaCtaDao());
                });
                 */
    }

/*
                            //log.info("cliente emisor "+clienteEmisor.toString());
                            return  Mono.just(new TransferenciaCtaDao());
                            /*
                            // busqueda cuenta emisor
                            return clientApiCuentas.getCuentasPorIdCliente("")
                                    .filter(cuentaEmi -> cuentaEmi.getTipoCuenta().equals("AHO"))
                                    .next()
                                    .flatMap(cuentaEmisora -> {
                                        // busqueda usuario receptor monedero
                                        return clientApiClientes.getClienteByNumeroCelular(transferenciaCtaDao.getNumeroCelularReceptor())
                                                .switchIfEmpty(Mono.error(() -> new BusinessException("No existe cliente con el numero celular " + transferenciaCtaDao.getNumeroCelularReceptor())))
                                                .flatMap(clienteReceptor -> {
                                                    log.info("cliente receptor "+clienteReceptor.toString());
                                                    // busqueda cuenta receptor
                                                    return clientApiCuentas.getCuentasPorIdCliente(clienteReceptor.getId())
                                                            .filter(cuentaRecep -> cuentaRecep.getTipoCuenta().equals("AHO"))
                                                            .next()
                                                            .flatMap(cuentaReceptora -> {

                                                                OperacionCtaDao operacionCtaCargo = new OperacionCtaDao();
                                                                operacionCtaCargo.setTipoOperacion("RET");
                                                                operacionCtaCargo.setNumeroCuenta(cuentaEmisora.getNumeroCuenta());
                                                                //operacionCtaCargo.setIdCliente(clienteEmisor.getId());
                                                                operacionCtaCargo.setIdCliente("");

                                                                operacionCtaCargo.setImporte(transferenciaCtaDao.getImporteTransferido());
                                                                return operacionesCuentaRepository.save(operacionCtaCargo)
                                                                        .flatMap(o -> {
                                                                            OperacionCtaDao operacionCtaAbono = new OperacionCtaDao();
                                                                            operacionCtaAbono.setTipoOperacion("DEP");
                                                                            operacionCtaAbono.setNumeroCuenta(cuentaReceptora.getNumeroCuenta());
                                                                            operacionCtaAbono.setIdCliente(clienteReceptor.getId());
                                                                            operacionCtaAbono.setImporte(transferenciaCtaDao.getImporteTransferido());
                                                                            return operacionesCuentaRepository.save(operacionCtaAbono)
                                                                                    .flatMap(t -> {
                                                                                        // Mensajeria KAFKA inicio
                                                                                        MonederoOperacionPost monederoOperacionRetiro = new MonederoOperacionPost();
                                                                                        monederoOperacionRetiro.setTipoOperacion("RET");
                                                                                        monederoOperacionRetiro.setIdCliente(operacionCtaCargo.getIdCliente());
                                                                                        monederoOperacionRetiro.setNumeroCelular(transferenciaCtaDao.getNumeroCelularEmisor());
                                                                                        //monederoOperacionRetiro.setNumeroMonedero(transferenciaCtaDao.getNumeroMonederoEmisor());
                                                                                        //monederoOperacionRetiro.setNumeroCelular(transferenciaCtaDao.getNumeroCelularEmisor());
                                                                                        monederoOperacionRetiro.setImporte(transferenciaCtaDao.getImporteTransferido());
                                                                                        Response responseR = kafkaMessageSender.sendOperacionMonedero(monederoOperacionRetiro);
                                                                                        log.info("retiro " + monederoOperacionRetiro.toString());
                                                                                        MonederoOperacionPost monederoOperacionDeposito = new MonederoOperacionPost();
                                                                                        monederoOperacionDeposito.setIdCliente(operacionCtaAbono.getIdCliente());
                                                                                        monederoOperacionDeposito.setNumeroCelular(transferenciaCtaDao.getNumeroCelularReceptor());
                                                                                        //monederoOperacionDeposito.setNumeroMonedero(transferenciaCtaDao.getNumeroMonederoReceptor());
                                                                                        //monederoOperacionDeposito.setNumeroCelular(transferenciaCtaDao.getNumeroCelularReceptor());
                                                                                        monederoOperacionDeposito.setTipoOperacion("DEP");
                                                                                        monederoOperacionDeposito.setImporte(transferenciaCtaDao.getImporteTransferido());
                                                                                        log.info("deposito " + monederoOperacionDeposito.toString());
                                                                                        Response responseD = kafkaMessageSender.sendOperacionMonedero(monederoOperacionDeposito);

                                                                                        // Mensajeria KAFKA final
                                                                                        return transferenciaCuentaRepository.save(transferenciaCtaDao);


                                                                                    });
                                                                        });

                                                            });


                                                });

                                    });

                             */

//                        });


    //}


}

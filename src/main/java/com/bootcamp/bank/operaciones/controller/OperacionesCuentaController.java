package com.bootcamp.bank.operaciones.controller;

import com.bootcamp.bank.operaciones.model.OperacionCta;
import com.bootcamp.bank.operaciones.model.OperacionCtaPost;
import com.bootcamp.bank.operaciones.model.TransferenciaCta;
import com.bootcamp.bank.operaciones.model.TransferenciaCtaPost;
import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import com.bootcamp.bank.operaciones.model.dao.TransferenciaCtaDao;
import com.bootcamp.bank.operaciones.service.OperacionCuentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Clase Controller Operaciones depositos y retiros de cuentas
 */
@RestController
@RequestMapping("/operaciones/cuentas")
@RequiredArgsConstructor
@Log4j2
public class OperacionesCuentaController {

    private final OperacionCuentaService operacionCuentaService;

    /**
     * Permite registrar depositos y retiros
     *
     *
     * @param operationCtaPost
     * @return
     */
    @PostMapping("/save")
    public Mono<OperacionCta> saveOperation(@RequestBody OperacionCtaPost operationCtaPost){
        return operacionCuentaService.saveOperation(this.fromOperacionPostToOperacionDao(operationCtaPost)).
                map(this::fromOperacionClienteDaoToOperacionDto);
    }



    /**
     * Permite registrar transferencias
     * Implementar las transferencias bancarias entre cuentas del mismo cliente y cuentas a terceros del mismo banco.
     * @param transferenciaCtaPost
     * @return
     */
    @PostMapping("/transfer")
    public Mono<TransferenciaCta> saveTransferOperation(@RequestBody TransferenciaCtaPost transferenciaCtaPost){
        return operacionCuentaService.saveTransferOperation(this.fromTransferenciaCtaPostToTransferenciaCtaDao(transferenciaCtaPost)).
                map(this::fromTransferenciaCtaDaoToTransferenciaCta);
    }


    /**
     * Permite visualizar depositos y retiros
     * @return
     */
    @GetMapping
    public Flux<OperacionCta> findAll(){
        return operacionCuentaService.findAll().
                map(this::fromOperacionClienteDaoToOperacionDto);
    }

    /**
     * Permite obtener operaciones deposito y retiro por id cliente
     * @param idCliente
     * @return
     */
    @GetMapping("/cliente/{idCliente}")
    public Flux<OperacionCta> findOperacionesByIdCliente(@PathVariable(name = "idCliente") String idCliente) {
        return operacionCuentaService.findOperacionesByIdCliente(idCliente).map(this::fromOperacionClienteDaoToOperacionDto);

    }

    /**
     * Permite obtener operaciones por numero cuenta
     * @param numeroCuenta
     * @return
     */
    @GetMapping("/numero-cuenta/{numeroCuenta}")
    public Flux<OperacionCta> findByNumeroCuenta(@PathVariable(name = "numeroCuenta") String numeroCuenta) {
        return operacionCuentaService.findByNumeroCuenta(numeroCuenta)
                .map(this::fromOperacionClienteDaoToOperacionDto);

    }

    /**
     * Permite Obtener tipos por numero de cuenta y tipo
     * @param numeroCuenta
     * @param tipoOperacion
     * @return
     */
    @GetMapping("/cuenta/{numeroCuenta}/tipo/{tipoOperacion}")
    public Flux<OperacionCta> findByNumeroCuentaTipo(
            @PathVariable(name = "numeroCuenta") String numeroCuenta,
            @PathVariable(name = "tipoOperacion") String tipoOperacion
    ) {
        log.info("peticion numeroCuenta:"+numeroCuenta+" tipoOperacion:"+tipoOperacion);
        return operacionCuentaService.findByNumeroCuentaAndTipoOperacion(numeroCuenta,tipoOperacion)
                .map(this::fromOperacionClienteDaoToOperacionDto);

    }

    @GetMapping("/numerocuenta/{numeroCuenta}/fechainicio/{fechaInicial}/fechafin/{fechaFinal}")
    public Flux<OperacionCta> findPagosByNumeroCreditoAndBetweenDates(
            @PathVariable(name = "numeroCuenta") String numeroCuenta,
            @PathVariable(name = "fechaInicial") String fechaInicial,
            @PathVariable(name = "fechaFinal") String fechaFinal
    ) {
        if (log.isDebugEnabled()) {
            log.info("numero credito " + numeroCuenta);
            log.info("fecha inicial " + fechaInicial);
            log.info("fecha final " + fechaFinal);
        }
        return operacionCuentaService.findPagosByNumeroCuentaAndBetweenDates(numeroCuenta, fechaInicial, fechaFinal)
                .map(this::fromOperacionClienteDaoToOperacionDto);

    }

    @GetMapping("/test-comision/{numeroCuenta}")
    public Flux<OperacionCta> getOperationsByMonth(@PathVariable String numeroCuenta){
        return operacionCuentaService.getOperationsByMonth(numeroCuenta).
                map(this::fromOperacionClienteDaoToOperacionDto);
    }

    private OperacionCta fromOperacionClienteDaoToOperacionDto(OperacionCtaDao operacionCtaDao) {
        OperacionCta operacionCta = new OperacionCta();
        BeanUtils.copyProperties(operacionCtaDao,operacionCta);
        return operacionCta;
    }

    private OperacionCtaDao fromOperacionPostToOperacionDao(OperacionCtaPost operacionCtaPost) {
        OperacionCtaDao operacionCtaDao = new OperacionCtaDao();
        BeanUtils.copyProperties(operacionCtaPost,operacionCtaDao);
        return operacionCtaDao;
    }

    private TransferenciaCta fromTransferenciaCtaDaoToTransferenciaCta(TransferenciaCtaDao transferenciaCtaDao) {
        TransferenciaCta transferenciaCta = new TransferenciaCta();
        BeanUtils.copyProperties(transferenciaCtaDao,transferenciaCta);
        return transferenciaCta;
    }

    private TransferenciaCtaDao fromTransferenciaCtaPostToTransferenciaCtaDao(TransferenciaCtaPost transferenciaCtaPost) {
        TransferenciaCtaDao transferenciaCtaDao = new TransferenciaCtaDao();
        BeanUtils.copyProperties(transferenciaCtaPost,transferenciaCtaDao);
        return transferenciaCtaDao;
    }

}
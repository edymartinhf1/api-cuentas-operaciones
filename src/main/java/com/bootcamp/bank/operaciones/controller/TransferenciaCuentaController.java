package com.bootcamp.bank.operaciones.controller;

import com.bootcamp.bank.operaciones.model.TransferenciaCta;
import com.bootcamp.bank.operaciones.model.TransferenciaCtaPost;
import com.bootcamp.bank.operaciones.model.dao.TransferenciaCtaDao;
import com.bootcamp.bank.operaciones.service.TransferenciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 *Clase controller Generacion de Transferencias de cuentas Bancarias
 */
@RestController
@RequestMapping("/operaciones/cuentas")
@RequiredArgsConstructor
@Log4j2
public class TransferenciaCuentaController {

    private final TransferenciaService transferenciaService;

    /**
     * Permite registrar transferencias
     * Permite registrar transferencias por Monedero movil entregable 4 clientes mismo banco
     * Implementar las transferencias bancarias entre cuentas del mismo cliente y cuentas a terceros del mismo banco.
     *
     * @param transferenciaCtaPost
     * @return
     */
    @PostMapping("/transfer")
    public Mono<TransferenciaCta> saveTransferOperation(@RequestBody TransferenciaCtaPost transferenciaCtaPost){
        return transferenciaService.saveTransferOperation(this.fromTransferenciaCtaPostToTransferenciaCtaDao(transferenciaCtaPost)).
                map(this::fromTransferenciaCtaDaoToTransferenciaCta);
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

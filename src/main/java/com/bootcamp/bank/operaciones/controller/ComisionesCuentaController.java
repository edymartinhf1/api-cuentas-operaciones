package com.bootcamp.bank.operaciones.controller;

import com.bootcamp.bank.operaciones.model.Comision;
import com.bootcamp.bank.operaciones.model.reports.RepCuentaComisiones;
import com.bootcamp.bank.operaciones.service.ComisionesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Clase Comisiones
 */
@RestController
@RequestMapping("/comisiones")
@Log4j2
@RequiredArgsConstructor
public class ComisionesCuentaController {

    private final ComisionesService comisionesService;

    /**
     * Permite obtener las comisiones cargadas por id cliente
     * @param idCliente
     * @return
     */
    @GetMapping("/{idCliente}")
    Mono<RepCuentaComisiones> getComisionsCharged(@PathVariable  String idCliente){
        log.info("idcliente = "+idCliente);
        return comisionesService.getComisionsCharged(idCliente);
    }

    @GetMapping
    Flux<Comision> getComisionsCharged(){
        return comisionesService.getComisions();
    }
}

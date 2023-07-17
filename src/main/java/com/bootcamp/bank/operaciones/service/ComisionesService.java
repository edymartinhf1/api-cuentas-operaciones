package com.bootcamp.bank.operaciones.service;

import com.bootcamp.bank.operaciones.model.Comision;
import com.bootcamp.bank.operaciones.model.reports.RepCuentaComisiones;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ComisionesService {
    Mono<RepCuentaComisiones> getComisionsCharged(String idCliente);

    Flux<Comision> getComisionsChargedByAccount(String numeroCuenta);
}

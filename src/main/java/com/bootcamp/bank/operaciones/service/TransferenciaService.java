package com.bootcamp.bank.operaciones.service;

import com.bootcamp.bank.operaciones.model.dao.TransferenciaCtaDao;
import reactor.core.publisher.Mono;

public interface TransferenciaService {
    Mono<TransferenciaCtaDao> saveTransferOperation(TransferenciaCtaDao transferenciaCtaDao);
}

package com.bootcamp.bank.operaciones.model.dao.repository;

import com.bootcamp.bank.operaciones.model.dao.TransferenciaCtaDao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferenciaCuentaRepository extends ReactiveMongoRepository<TransferenciaCtaDao,String> {
}

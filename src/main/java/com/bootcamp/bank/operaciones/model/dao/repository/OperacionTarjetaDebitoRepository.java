package com.bootcamp.bank.operaciones.model.dao.repository;

import com.bootcamp.bank.operaciones.model.dao.OperacionTarjetaDebitoDao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperacionTarjetaDebitoRepository extends ReactiveMongoRepository<OperacionTarjetaDebitoDao,String> {
}

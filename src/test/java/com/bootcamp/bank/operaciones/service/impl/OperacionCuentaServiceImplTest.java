package com.bootcamp.bank.operaciones.service.impl;

import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@Log4j2
class OperacionCuentaServiceImplTest {

    @Mock
    private OperacionesCuentaRepository operacionesCuentaRepository;

    @InjectMocks
    private OperacionCuentaServiceImpl operacionCuentaService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void saveOperation() {

        OperacionCtaDao expected = new OperacionCtaDao();
        expected.setId("1");
        expected.setIdCliente("02");
        expected.setNumeroCuenta("456-789-456");

        OperacionCtaDao operacionCtaDao = new OperacionCtaDao();
        operacionCtaDao.setId("1");
        operacionCtaDao.setIdCliente("02");
        operacionCtaDao.setNumeroCuenta("456-789-456");

        Mockito.when( operacionesCuentaRepository.save(Mockito.any(OperacionCtaDao.class)) )
                .thenReturn( Mono.just(operacionCtaDao) );
        log.info("step 1"+operacionCtaDao.toString());
        OperacionCtaDao actualiza=new OperacionCtaDao();
        actualiza.setId("1");
        actualiza.setIdCliente("02");

        Mono<OperacionCtaDao> actual0 = operacionesCuentaRepository.save(actualiza);
        OperacionCtaDao actual=actual0.block();
        log.info("step 2"+actual.toString());

        Assertions.assertEquals(expected.getId(),actual.getId());
        Assertions.assertEquals(expected.getNumeroCuenta(),actual.getNumeroCuenta());
        Assertions.assertEquals(expected.getNumeroCuenta(),actual.getNumeroCuenta());
        Assertions.assertEquals(expected.getIdCliente(),actual.getIdCliente());
    }

    @Test
    void findAll() {
        OperacionCtaDao operacionCtaDao1=new OperacionCtaDao();
        operacionCtaDao1.setId("1");
        operacionCtaDao1.setFechaOperacionT("2023-01-01");
        operacionCtaDao1.setIdCliente("02");
        operacionCtaDao1.setNumeroCuenta("456-789-456");

        OperacionCtaDao operacionCtaDao2=new OperacionCtaDao();
        operacionCtaDao2.setId("2");
        operacionCtaDao2.setFechaOperacionT("2023-01-02");
        operacionCtaDao2.setIdCliente("02");
        operacionCtaDao2.setNumeroCuenta("456-789-457");

        List<OperacionCtaDao>  expected=new ArrayList<>();
        expected.add(operacionCtaDao1);
        expected.add(operacionCtaDao2);
        log.info("test");
        Mockito.when( operacionesCuentaRepository.save(Mockito.any(OperacionCtaDao.class)) )
                .thenReturn( Mono.just(operacionCtaDao1) );

        Mono<OperacionCtaDao> result1=operacionesCuentaRepository.save(operacionCtaDao1);

        Mockito.when( operacionesCuentaRepository.save(Mockito.any(OperacionCtaDao.class)) )
                .thenReturn( Mono.just(operacionCtaDao2) );

        Mono<OperacionCtaDao> result2=operacionesCuentaRepository.save(operacionCtaDao2);
        result1.subscribe(operacionCtaDao -> log.info(operacionCtaDao.toString()));
        result2.subscribe(operacionCtaDao -> log.info(operacionCtaDao.toString()));

        Mockito.when( operacionesCuentaRepository.findAll())
                .thenReturn( Flux.fromIterable(expected));

        Flux<OperacionCtaDao> obtenidos = operacionesCuentaRepository.findAll();
        List<OperacionCtaDao> actual = obtenidos.map(operacionCtaDao -> operacionCtaDao).collectList().block();

        Assertions.assertEquals(expected.get(0).getId(), actual.get(0).getId());
        Assertions.assertEquals(expected.get(1).getId(), actual.get(1).getId());
    }

    @Test
    void findByNumeroCuenta() {
    }

    @Test
    void findByNumeroCuentaAndTipoOperacion() {
    }

    @Test
    void findOperacionesByIdCliente() {
    }

    @Test
    void getOperationsByMonth() {
    }

    @Test
    void findPagosByNumeroCuentaAndBetweenDates() {
    }

    @Test
    void findmovsByIdClienteAndNumeroTarjetaDebito() {
    }
}
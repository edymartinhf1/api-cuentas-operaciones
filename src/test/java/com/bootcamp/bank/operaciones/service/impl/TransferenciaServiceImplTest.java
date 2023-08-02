package com.bootcamp.bank.operaciones.service.impl;

import com.bootcamp.bank.operaciones.model.dao.TransferenciaCtaDao;
import com.bootcamp.bank.operaciones.model.dao.repository.TransferenciaCuentaRepository;
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
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Log4j2
class TransferenciaServiceImplTest {

    @Mock
    private TransferenciaCuentaRepository transferenciaCuentaRepository;

    @InjectMocks
    private TransferenciaServiceImpl transferenciaService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void saveTransferOperation() {

        TransferenciaCtaDao expected = new TransferenciaCtaDao();
        expected.setId("1");
        expected.setCuentaEmisora("900-0045-0045");
        expected.setCuentaReceptora("900-0045-0046");


        TransferenciaCtaDao transferenciaCtaDao = new TransferenciaCtaDao();
        transferenciaCtaDao.setId("1");
        transferenciaCtaDao.setCuentaEmisora("900-0045-0045");
        transferenciaCtaDao.setCuentaReceptora("900-0045-0046");

        Mockito.when( transferenciaCuentaRepository.save(Mockito.any(TransferenciaCtaDao.class)) )
                .thenReturn( Mono.just(transferenciaCtaDao) );
        log.info("step 1"+transferenciaCtaDao.toString());
        TransferenciaCtaDao transferenciaCtaDao1=new TransferenciaCtaDao();
        transferenciaCtaDao1.setId("1");
        transferenciaCtaDao1.setCuentaEmisora("900-0045-0045");
        transferenciaCtaDao1.setCuentaReceptora("900-0045-0046");

        Mono<TransferenciaCtaDao> actual0 = transferenciaCuentaRepository.save(transferenciaCtaDao1);
        TransferenciaCtaDao actual=actual0.block();
        log.info("step 2"+actual.toString());

        Assertions.assertEquals(expected.getId(),actual.getId());
        Assertions.assertEquals(expected.getCuentaEmisora(),actual.getCuentaEmisora());
        Assertions.assertEquals(expected.getCuentaReceptora(),actual.getCuentaReceptora());


    }
}
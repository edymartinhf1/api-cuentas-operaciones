package com.bootcamp.bank.operaciones.service.impl;

import com.bootcamp.bank.operaciones.clients.ClientApiClientes;
import com.bootcamp.bank.operaciones.clients.ClientApiCuentas;
import com.bootcamp.bank.operaciones.model.dao.TransferenciaCtaDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import com.bootcamp.bank.operaciones.model.dao.repository.TransferenciaCuentaRepository;
import com.bootcamp.bank.operaciones.model.enums.CuentasType;
import com.bootcamp.bank.operaciones.producer.KafkaMonederoMessageSender;
import com.bootcamp.bank.operaciones.service.TransferenciaService;
import com.bootcamp.bank.operaciones.strategy.transferencia.TransferenciaStrategy;
import com.bootcamp.bank.operaciones.strategy.transferencia.TransferenciaStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 *
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class TransferenciaServiceImpl implements TransferenciaService {

    private final TransferenciaCuentaRepository transferenciaCuentaRepository;

    private final TransferenciaStrategyFactory transferenciaStrategyFactory;

    private final OperacionesCuentaRepository operacionesCuentaRepository;

    private final ClientApiClientes clientApiClientes;

    private final ClientApiCuentas clientApiCuentas;

    private final KafkaMonederoMessageSender kafkaMessageSender;

     /**
     * Registro de Transferencias
     * @param transferenciaCtaDao
     * @return
     */
    @Override
    public Mono<TransferenciaCtaDao> saveTransferOperation(TransferenciaCtaDao transferenciaCtaDao) {
        transferenciaCtaDao = operacionTransferCta.apply(transferenciaCtaDao);
        CuentasType cuentasType= setTipoCuenta.apply(transferenciaCtaDao.getTipoTransferencia());
        TransferenciaStrategy strategy = transferenciaStrategyFactory.getStrategy(cuentasType);
        return strategy.registrarTransferencia(
                transferenciaCuentaRepository,
                operacionesCuentaRepository,
                clientApiClientes,
                clientApiCuentas,
                kafkaMessageSender,
                transferenciaCtaDao
        );

    }

    UnaryOperator<TransferenciaCtaDao> operacionTransferCta = cta -> {
        LocalDateTime fecha = LocalDateTime.now();
        cta.setFechaOperacion(fecha);
        return cta;
    };

    Function<String,CuentasType> setTipoCuenta = tipoCuenta  -> {
        CuentasType cuentasType= null;
        switch (tipoCuenta) {
            case "PROP" -> cuentasType= CuentasType.PROPIA;

            case "TERC" -> cuentasType= CuentasType.TERCEROS;

            case "INTB" -> cuentasType= CuentasType.INTERBANCARIA;

            case "MONM" -> cuentasType= CuentasType.MONEDERO_MOVIL;

            default -> cuentasType = CuentasType.INVALIDO;

        }
        return cuentasType;
    };
}

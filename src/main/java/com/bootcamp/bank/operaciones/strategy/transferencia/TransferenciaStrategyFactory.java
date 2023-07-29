package com.bootcamp.bank.operaciones.strategy.transferencia;

import com.bootcamp.bank.operaciones.model.enums.CuentasType;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
@Log4j2
public class TransferenciaStrategyFactory {

    private Map<CuentasType, TransferenciaStrategy> strategies = new EnumMap<>(CuentasType.class);

    public TransferenciaStrategyFactory() {
        initStrategies();
    }

    public TransferenciaStrategy getStrategy(CuentasType cuentasType) {
        if (cuentasType == null || !strategies.containsKey(cuentasType)) {
            throw new IllegalArgumentException("Invalid " + cuentasType);
        }
        return strategies.get(cuentasType);
    }

    private void initStrategies() {
        strategies.put(CuentasType.PROPIA, new TransferenciaPropiasStrategy());
        strategies.put(CuentasType.TERCEROS, new TransferenciaTercerrosStrategy());
        strategies.put(CuentasType.INTERBANCARIA, new TransferenciaInterbancariaStrategy());
        strategies.put(CuentasType.MONEDERO_MOVIL, new TransferenciaMonederoMovilStrategy());
    }
}

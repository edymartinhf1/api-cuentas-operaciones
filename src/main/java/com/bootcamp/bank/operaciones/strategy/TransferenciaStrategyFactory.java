package com.bootcamp.bank.operaciones.strategy;

import com.bootcamp.bank.operaciones.model.enums.CuentasType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
@Slf4j
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
    }
}

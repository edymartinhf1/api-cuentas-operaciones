package com.bootcamp.bank.operaciones.strategy.mediopago;

import com.bootcamp.bank.operaciones.model.enums.MedioPagoType;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
@Component
@Log4j2

public class MedioPagoStrategyFactory {

    private Map<MedioPagoType, MedioPagoStrategy> strategies = new EnumMap<>(MedioPagoType.class);

    public MedioPagoStrategyFactory() {
        initStrategies();
    }

    public MedioPagoStrategy getStrategy(MedioPagoType medioPagoType) {
        if (medioPagoType == null || !strategies.containsKey(medioPagoType)) {
            throw new IllegalArgumentException("Invalid " + medioPagoType);
        }
        return strategies.get(medioPagoType);
    }

    private void initStrategies() {
        strategies.put(MedioPagoType.EFECTIVO, new MedioPagoEfectivo());
        strategies.put(MedioPagoType.TARJETA_DEBITO, new MedioPagoTarjetaDebitoStrategy());
    }
}

package com.bootcamp.bank.operaciones.clients;

import com.bootcamp.bank.operaciones.model.Cuenta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ClientApiCuentas {
    @Autowired
    @Qualifier("clientCuentas")
    private WebClient webClient;

    /**
     * Permite obtener cuenta por numero de cuenta
     * @param numeroCuenta
     * @return
     */
    public Mono<Cuenta> getCuenta(String numeroCuenta) {
        return webClient.get()
                .uri("/cuentas/numero-cuenta/" + numeroCuenta)
                .retrieve()
                .bodyToMono(Cuenta.class);
    }

    public Flux<Cuenta> getCuentasPorIdCliente(String idCliente) {
        return webClient.get()
                .uri("/cuentas/cliente/" + idCliente)
                .retrieve()
                .bodyToFlux(Cuenta.class);
    }
}
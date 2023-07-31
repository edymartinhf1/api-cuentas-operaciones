package com.bootcamp.bank.operaciones.controller;

import com.bootcamp.bank.operaciones.model.monedero.p2p.OperacionP2PAccept;
import com.bootcamp.bank.operaciones.model.monedero.p2p.OperacionP2PRequest;
import com.bootcamp.bank.operaciones.model.Response;
import com.bootcamp.bank.operaciones.model.monedero.p2p.OperacionP2PValidate;
import com.bootcamp.bank.operaciones.service.OperacionP2PService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operaciones/p2p")
@RequiredArgsConstructor
@Log4j2
public class OperacionP2PController {

    private final OperacionP2PService operacionP2PService;

    /**
     * Cualquier usuario puede solicitar la compra de BootCoin especificando el monto y modo de pago (Yanki o transferencia)
     * @param operacionP2PPost
     * @return
     */
    @PostMapping
    public Response buyBootCoins(OperacionP2PRequest operacionP2PPost){
        return operacionP2PService.buyBootCoins(operacionP2PPost);
    }

    /**
     * Permite aceptar el intercambio
     * @return
     */
    @PostMapping("/accept")
    public Response acceptP2PTransaction(OperacionP2PAccept operacionP2PAccept){
        return  operacionP2PService.acceptP2PTransaction(operacionP2PAccept);
    }

    /**
     * @return
     */
    @PostMapping("/validate")
    public Response validateP2PTransaction(OperacionP2PValidate operacionP2PValidate){
        return operacionP2PService.validateP2PTransaction(operacionP2PValidate);
    }







}

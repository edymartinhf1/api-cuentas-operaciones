package com.bootcamp.bank.operaciones.service.impl;

import com.bootcamp.bank.operaciones.model.monedero.p2p.OperacionP2PAccept;
import com.bootcamp.bank.operaciones.model.monedero.p2p.OperacionP2PRequest;
import com.bootcamp.bank.operaciones.model.Response;
import com.bootcamp.bank.operaciones.model.monedero.p2p.OperacionP2PValidate;
import com.bootcamp.bank.operaciones.producer.KafkaMonederoP2PSender;
import com.bootcamp.bank.operaciones.service.OperacionP2PService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperacionP2PServiceImpl implements OperacionP2PService {

    private final KafkaMonederoP2PSender kafkaMonederoP2PSender;

    @Override
    public Response buyBootCoins(OperacionP2PRequest operacionP2PRequest) {
        Response response;
        response = kafkaMonederoP2PSender.sendOperationP2PRequest(operacionP2PRequest);
        return response;
    }

    @Override
    public Response acceptP2PTransaction(OperacionP2PAccept operacionP2PAccept) {
        Response response;
        response = kafkaMonederoP2PSender.sendOperationP2PAccept(operacionP2PAccept);
        return response;
    }

    @Override
    public Response validateP2PTransaction(OperacionP2PValidate operacionP2PValidate) {
        Response response;
        response = kafkaMonederoP2PSender.sendOperationP2PValidate(operacionP2PValidate);
        return response;
    }
}

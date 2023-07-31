package com.bootcamp.bank.operaciones.service;

import com.bootcamp.bank.operaciones.model.monedero.p2p.OperacionP2PAccept;
import com.bootcamp.bank.operaciones.model.monedero.p2p.OperacionP2PRequest;
import com.bootcamp.bank.operaciones.model.Response;
import com.bootcamp.bank.operaciones.model.monedero.p2p.OperacionP2PValidate;

public interface OperacionP2PService {
    Response buyBootCoins(OperacionP2PRequest operacionP2PPost);
    Response acceptP2PTransaction(OperacionP2PAccept operacionP2PAccept);
    Response validateP2PTransaction(OperacionP2PValidate operacionP2PValidate);


}

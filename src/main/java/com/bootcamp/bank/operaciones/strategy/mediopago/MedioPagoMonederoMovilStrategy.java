package com.bootcamp.bank.operaciones.strategy.mediopago;

import com.bootcamp.bank.operaciones.clients.ClientApiCuentas;
import com.bootcamp.bank.operaciones.model.MonederoOperacionPost;
import com.bootcamp.bank.operaciones.model.Response;
import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionTarjetaDebitoRepository;
import com.bootcamp.bank.operaciones.model.dao.repository.OperacionesCuentaRepository;
import com.bootcamp.bank.operaciones.producer.KafkaMonederoMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Clase Monedero movil
 */
@Service
public class MedioPagoMonederoMovilStrategy implements MedioPagoStrategy{
    @Autowired
    private KafkaMonederoMessageSender kafkaMessageSender;
    @Override
    public Mono<OperacionCtaDao> registrarOperacionctaBancaria(OperacionesCuentaRepository operacionesCuentaRepository, OperacionTarjetaDebitoRepository operacionTarjetaDebitoRepository, ClientApiCuentas clientApiCuentas, OperacionCtaDao operacionCtaDao) {


        return operacionesCuentaRepository.save(operacionCtaDao).map(operacion->{
            // Mensajeria Kafka
            if (!operacion.getId().isEmpty()) {
                MonederoOperacionPost monederoOperacion = new MonederoOperacionPost();
                Response response = kafkaMessageSender.sendOperacionMonedero(monederoOperacion);
            }
            return operacion;
        });



    }
}

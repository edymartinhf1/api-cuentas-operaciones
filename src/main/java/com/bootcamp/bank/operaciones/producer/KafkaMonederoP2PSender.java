package com.bootcamp.bank.operaciones.producer;

import com.bootcamp.bank.operaciones.model.Response;
import com.bootcamp.bank.operaciones.model.monedero.p2p.OperacionP2PAccept;
import com.bootcamp.bank.operaciones.model.monedero.p2p.OperacionP2PRequest;
import com.bootcamp.bank.operaciones.model.monedero.p2p.OperacionP2PValidate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class KafkaMonederoP2PSender {
    public static final String MENSAJE_ENVIADO_CORRECTAMENTE = "Mensaje enviado correctamente";
    public static final String ERROR_EN_EL_ENVIO_DE_MENSAJE_A_KAFKA = "Ocurrrio un error en el envio de mensaje a kafka";
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    /**
     * @param operacionP2PRequest
     */
    public Response sendOperationP2PRequest(OperacionP2PRequest operacionP2PRequest) {
        log.info("publish sendOperationP2PRequest");
        Response response=new Response();
        try {
            String OperationP2PAsMessage = objectMapper.writeValueAsString(operacionP2PRequest);
            kafkaTemplate.send("OperationP2PAsMessage", OperationP2PAsMessage);
            response.setCodigo("01");
            response.setMensaje(MENSAJE_ENVIADO_CORRECTAMENTE);
        }catch(JsonProcessingException ex){
            response.setMensaje(ERROR_EN_EL_ENVIO_DE_MENSAJE_A_KAFKA);
            log.error("error "+ex.getMessage());
        }
        return  response;

    }

    public Response sendOperationP2PAccept(OperacionP2PAccept operacionP2PAccept) {
        log.info("publish sendOperationP2PAccept");
        Response response=new Response();
        try {



            String OperationP2PAsMessage = objectMapper.writeValueAsString(operacionP2PAccept);
            kafkaTemplate.send("operacionP2PAccept", OperationP2PAsMessage);
            response.setCodigo("01");
            response.setMensaje(MENSAJE_ENVIADO_CORRECTAMENTE);


        }catch(JsonProcessingException ex){
            response.setMensaje(ERROR_EN_EL_ENVIO_DE_MENSAJE_A_KAFKA);
            log.error("error "+ex.getMessage());
        }
        return  response;

    }

    public Response sendOperationP2PValidate(OperacionP2PValidate operacionP2PValidate) {
        log.info("publish sendOperationP2PValidate");
        Response response=new Response();
        try {
            String OperationP2PAsMessage = objectMapper.writeValueAsString(operacionP2PValidate);
            kafkaTemplate.send("operacionP2PValidate", OperationP2PAsMessage);
            response.setCodigo("01");
            response.setMensaje(MENSAJE_ENVIADO_CORRECTAMENTE);
        }catch(JsonProcessingException ex){
            response.setMensaje(ERROR_EN_EL_ENVIO_DE_MENSAJE_A_KAFKA);
            log.error("error "+ex.getMessage());
        }
        return  response;

    }
}

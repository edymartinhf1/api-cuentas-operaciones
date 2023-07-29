package com.bootcamp.bank.operaciones.producer;

import com.bootcamp.bank.operaciones.model.MonederoOperacionPost;
import com.bootcamp.bank.operaciones.model.Response;
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
public class KafkaMonederoMessageSender {
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    /**
     * @param monederoOperacion
     */
    public Response sendOperacionMonedero(MonederoOperacionPost monederoOperacion) {
        log.info("publish");
        Response response=new Response();
        try {
            String monederoAsMessage = objectMapper.writeValueAsString(monederoOperacion);
            kafkaTemplate.send("monederooperacionmovil", monederoAsMessage);
            response.setCodigo("01");
            response.setMensaje("mensaje enviado correctamente");
        }catch(JsonProcessingException ex){
            response.setMensaje("ocurrrio un error en el envio de mensaje a kafka");
            log.error("error "+ex.getMessage());
        }
        return  response;

    }




}
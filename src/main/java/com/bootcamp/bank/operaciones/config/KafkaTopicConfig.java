package com.bootcamp.bank.operaciones.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Clase generadora de Topico Kafka
 */
@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic monederoTopic() {
        return TopicBuilder.name("monederomovil").build();
    }

    @Bean
    public NewTopic monederoOperacionTopic() {
        return TopicBuilder.name("monederooperacionmovil").build();
    }


}
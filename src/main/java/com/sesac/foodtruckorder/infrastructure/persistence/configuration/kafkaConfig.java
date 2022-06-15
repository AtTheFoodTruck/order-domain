package com.sesac.foodtruckorder.infrastructure.persistence.configuration;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class kafkaConfig {

    private final String kafakaServerHost;
    private final String kafakaServerPort;

    public kafkaConfig(@Value("${kafka.host}") String kafkaServerHost,
                       @Value("${kafka.port}") String kafkaServerPort) {
        this.kafakaServerHost = kafkaServerHost;
        this.kafakaServerPort = kafkaServerPort;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> properties = new HashMap<>();
        // 카프카 클러스터 서버의 Host, Port 지정
       properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafakaServerHost + ":" + kafakaServerPort);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}

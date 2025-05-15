package com.example.demo.services;

import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Map<String, String>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendUserRegistration(Map<String, String> userData) {
        kafkaTemplate.send("user-register-topic", userData);
    }

    public void sendUserData(Map<String, String> userData) {
        kafkaTemplate.send("user-login-topic", userData);
    }
}

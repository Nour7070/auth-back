package com.example.demo.services;

import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.classes.Formateur;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserRegistration(Map<String, String> userData) {
        kafkaTemplate.send("user-register-topic", userData);
    }

    public void sendFormateurPending(Formateur formateur) {
        kafkaTemplate.send("formateur-pending-topic", formateur);

    }

    public void sendUserData(Map<String, String> userData) {
        kafkaTemplate.send("user-login-topic", userData);
    }
}

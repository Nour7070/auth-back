package com.example.demo.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.classes.Formateur;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate ,  ) {
        this.kafkaTemplate = kafkaTemplate;
    }

   public void sendUserRegistration(Map<String, String> userData) {
        try {
            // Convertir explicitement en String JSON
            String json = objectMapper.writeValueAsString(userData);
            kafkaTemplate.send("user-register-topic", json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erreur de sérialisation JSON", e);
        }
    }

    public void sendFormateurPending(Formateur formateur) {
        Map<String, Object> formateurData = new HashMap<>();
        formateurData.put("email", formateur.getEmail());
        formateurData.put("prenom", formateur.getFirstName());
        formateurData.put("nom", formateur.getLastName());
        formateurData.put("status", formateur.getStatus().name());
        formateurData.put("certificats", formateur.getCertificats());
        formateurData.put("experiences", formateur.getExperiences());
    
        kafkaTemplate.send("formateur-pending-topic", formateurData);
    }

    public void sendUserData(Map<String, String> userData) {
        kafkaTemplate.send("user-login-topic", userData);
    }
}

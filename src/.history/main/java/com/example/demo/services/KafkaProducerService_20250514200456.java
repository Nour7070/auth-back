package com.example.demo.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.classes.Apprenant;
import com.example.demo.classes.Formateur;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate , ObjectMapper objectMapper ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper ;
    }

   public void sendUserRegistration(Apprenant apprenant) {

        if (apprenant.getId() == null) {
        throw new IllegalArgumentException("L'apprenant doit avoir un ID avant d'être envoyé à Kafka");
        }
        Map<String, Object> apprenantData = new HashMap<>();
        apprenantData.put("id", apprenant.getId());
        apprenantData.put("email", apprenant.getEmail());
        apprenantData.put("prenom", apprenant.getFirstName());
        apprenantData.put("nom", apprenant.getLastName());
        apprenantData.put("username", apprenant.getUsername());
        kafkaTemplate.send("user-register-topic", apprenantData);
       
    }

    public void sendFormateurPending(Formateur formateur) {
        Map<String, Object> formateurData = new HashMap<>();
        formateurData.put("id", formateur.getId());
        formateurData.put("email", formateur.getEmail());
        formateurData.put("prenom", formateur.getFirstName());
        formateurData.put("nom", formateur.getLastName());
        formateurData.put("username", formateur.getUsername());
        formateurData.put("status", formateur.getStatus().name());
        formateurData.put("certificats", formateur.getCertificats());
        formateurData.put("experiences", formateur.getExperiences());
    
        kafkaTemplate.send("formateur-pending-topic", formateurData);
    }
}

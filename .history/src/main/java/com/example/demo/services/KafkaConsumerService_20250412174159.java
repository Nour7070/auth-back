// KafkaConsumerService.java
package com.example.demo.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.demo.classes.Formateur;
import com.example.demo.classes.UserStatus;
import com.example.demo.repositories.UserRepository;

@Service
public class KafkaConsumerService {
    private final UserRepository userRepository;

    public KafkaConsumerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "formateur-validation-result", groupId = "auth-service-group")
public void handleFormateurValidation(Map<String, String> response) {
    Long formateurId = Long.parseLong(response.get("formateurId"));
    String status = response.get("status");
    
    // 1. Récupérez le User puis castez en Formateur
    User user = userRepository.findById(formateurId).orElseThrow();
    
    if (!(user instanceof Formateur)) {
        throw new IllegalStateException("L'utilisateur n'est pas un formateur");
    }
    
    Formateur formateur = (Formateur) user;
    
    // 2. Mettez à jour le statut
    if ("APPROVED".equals(status)) {
        formateur.setStatus(UserStatus.APPROVED);
    } else {
        formateur.setStatus(UserStatus.REJECTED);
    }
    
    userRepository.save(formateur);
}
}
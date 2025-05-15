// KafkaConsumerService.java
package com.example.demo.services;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.demo.classes.Formateur;
import com.example.demo.classes.User;
import com.example.demo.classes.UserStatus;
import com.example.demo.repositories.UserRepository;

@Service
public class KafkaConsumerService {
    private final UserRepository userRepository;

    public KafkaConsumerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "formateur-validation-result")
    public void handleValidation(Map<String, Object> message) {
        Long formateurId = (Long) message.get("formateurId");
        String action = (String) message.get("action");

        User formateur = userRepository.findById(formateurId)
            .orElseThrow(() -> new RuntimeException("Formateur non trouvé dans AuthService"));

        if ("APPROVE".equals(action)) {
            formateur.setValidated(true);
            userRepository.save(formateur);
        } else {
            userRepository.delete(formateur);
        }
    }
}
// KafkaConsumerService.java
package com.example.demo.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.example.demo.classes.User;
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
        Formateur formateur = userRepository.findById(formateurId).orElseThrow();
        
        if ("APPROVED".equals(status)) {
            formateur.setStatus(UserStatus.APPROVED);
        } else {
            formateur.setStatus("REJECTED");
        }
        userRepository.save(formateur);
    }
}
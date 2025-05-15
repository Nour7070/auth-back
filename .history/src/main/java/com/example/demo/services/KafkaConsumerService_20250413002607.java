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

    /*@KafkaListener(topics = "formateur-validation-result", groupId = "auth-service-group")
    public void handleFormateurValidation(Map<String, String> response) {
        Long formateurId = Long.parseLong(response.get("formateurId"));
        String status = response.get("status");

        // nrecupiri user w ncasstih l formateur
        User user = userRepository.findById(formateurId).orElseThrow();

        if (!(user instanceof Formateur)) {
            throw new IllegalStateException("L'utilisateur n'est pas un formateur");
        }

        Formateur formateur = (Formateur) user;

        if ("APPROVED".equals(status)) {
            formateur.setStatus(UserStatus.APPROVED);
        } else {
            formateur.setStatus(UserStatus.REJECTED);
        }

        userRepository.save(formateur);
    }
*/
@KafkaListener(topics = "formateur-validation-result", groupId = "auth-service-group")
public void handleFormateurValidation(Map<String, Object> response) {  // Changé en Map<String, Object>
    Long formateurId = Long.valueOf(response.get("formateurId").toString());  // Gestion des types
    String status = response.get("status").toString();

    User user = userRepository.findById(formateurId).orElseThrow();
    if (!(user instanceof Formateur)) {
        throw new IllegalStateException("L'utilisateur n'est pas un formateur");
    }

    Formateur formateur = (Formateur) user;
    formateur.setStatus(UserStatus.valueOf(status));  // Conversion explicite
    userRepository.save(formateur);
}
}
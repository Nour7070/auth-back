// KafkaConsumerService.java
package com.example.demo.services;

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
/*@KafkaListener(topics = "formateur-validation-result", groupId = "auth-service-group")
public void handleFormateurValidation(Map<String, Object> response) { 
    Long formateurId = Long.valueOf(response.get("formateurId").toString());  
    String status = response.get("status").toString();

    User user = userRepository.findById(formateurId).orElseThrow();
    if (!(user instanceof Formateur)) {
        throw new IllegalStateException("L'utilisateur n'est pas un formateur");
    }

    Formateur formateur = (Formateur) user;
    formateur.setStatus(UserStatus.valueOf(status));  
    userRepository.save(formateur);
}*/
@KafkaListener(topics = "formateur-validation-result", groupId = "auth-service-group")
public void handleFormateurValidation(Map<String, Object> response) {
    try {
        Long formateurId = Long.valueOf(response.get("formateurId").toString());
        String status = response.get("status").toString();

        var userOpt = userRepository.findById(formateurId);
        if (userOpt.isEmpty()) {
            log.error("Formateur non trouvé avec l'ID: {}", formateurId);
            return;
        }

        User user = userOpt.get();
        if (!(user instanceof Formateur)) {
            log.error("L'utilisateur {} n'est pas un formateur", formateurId);
            return;
        }

        Formateur formateur = (Formateur) user;
        formateur.setStatus(UserStatus.valueOf(status));
        userRepository.save(formateur);
        log.info("Statut du formateur {} mis à jour à: {}", formateurId, status);
        
    } catch (Exception e) {
        log.error("Erreur lors du traitement du message Kafka", e);
    }
}
}
// KafkaConsumerService.java
package com.example.demo.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.demo.classes.User;
import com.example.demo.repositories.UserRepository;

import java.util.Map;

import com.example.demo.classes.UserStatus;

@Service
public class KafkaConsumerService {
    private final UserRepository userRepository;

    public KafkaConsumerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * @KafkaListener(topics = "formateur-validation-result", groupId =
     * "auth-service-group")
     * public void handleFormateurValidation(Map<String, String> response) {
     * Long formateurId = Long.parseLong(response.get("formateurId"));
     * String status = response.get("status");
     * 
     * // nrecupiri user w ncasstih l formateur
     * User user = userRepository.findById(formateurId).orElseThrow();
     * 
     * if (!(user instanceof Formateur)) {
     * throw new IllegalStateException("L'utilisateur n'est pas un formateur");
     * }
     * 
     * Formateur formateur = (Formateur) user;
     * 
     * if ("APPROVED".equals(status)) {
     * formateur.setStatus(UserStatus.APPROVED);
     * } else {
     * formateur.setStatus(UserStatus.REJECTED);
     * }
     * 
     * userRepository.save(formateur);
     * }
     */
    @KafkaListener(topics = "formateur-validation-result")
    public void handleValidation(Map<String, Object> message) {
        Long formateurId = (Long) message.get("formateurId");
        String action = (String) message.get("action");

        User user = userRepository.findById(formateurId).orElseThrow();
         
         if (!(user instanceof Formateur)) {
         throw new IllegalStateException("L'utilisateur n'est pas un formateur");
         }
         
         Formateur formateur = (Formateur) user;
         
         if ("APPROVED".equals(status)) {
         formateur.setStatus(UserStatus.APPROVED);
         } else {
         formateur.setStatus(UserStatus.REJECTED);
        * }
        * 
        * userRepository.save(formateur);
    }
}
// KafkaConsumerService.java
package com.example.demo.services;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.demo.classes.Formateur;
import com.example.demo.classes.User;
import com.example.demo.classes.UserStatus;
import com.example.demo.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    /*
     * @KafkaListener(topics = "formateur-validation-result", groupId =
     * "auth-service-group")
     * public void handleFormateurValidation(Map<String, Object> response) {
     * Long formateurId = Long.valueOf(response.get("formateurId").toString());
     * String status = response.get("status").toString();
     * 
     * User user = userRepository.findById(formateurId).orElseThrow();
     * if (!(user instanceof Formateur)) {
     * throw new IllegalStateException("L'utilisateur n'est pas un formateur");
     * }
     * 
     * Formateur formateur = (Formateur) user;
     * formateur.setStatus(UserStatus.valueOf(status));
     * userRepository.save(formateur);
     * }
     */
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

    @KafkaListener(topics = "new-moderator-created", groupId = "auth-service-group")
    public void handleNewModerator(Map<String, String> message) {
        try {
            User newUser = new User();
            newUser.setId(Long.parseLong(message.get("id")));
            newUser.setEmail(message.get("email"));
            newUser.setPassword(message.get("password"));
            newUser.setUserType(message.get("userType"));
            newUser.setNom(message.get("lastName")); // lastName -> nom
            newUser.setPrenom(message.get("firstName")); // firstName -> prenom
            newUser.setUsername(message.get("username"));
            newUser.setPhoneNumber(message.get("phoneNumber"));
            newUser.setPhoto(message.get("photo"));
            newUser.setAddress(message.get("address"));

            userRepository.save(newUser);
            log.info("Nouveau modérateur créé dans auth-service: {}", newUser.getId());
        } catch (Exception e) {
            log.error("Erreur lors de la création du modérateur dans auth-service", e);
        }
    }
}
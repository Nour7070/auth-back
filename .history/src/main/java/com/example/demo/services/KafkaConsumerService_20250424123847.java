// KafkaConsumerService.java
package com.example.demo.services;

import java.util.Map;
import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.demo.classes.Formateur;
import com.example.demo.classes.Moderateur;
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
            // Long formateurId = Long.valueOf(response.get("formateurId").toString());
            String email = (String) response.get("formateurEmail");
            String status = response.get("status").toString();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Formateur non trouvé avec l'email: " + email));

            if (!(user instanceof Formateur)) {
                throw new IllegalStateException("L'utilisateur n'est pas un formateur");
            }

            Formateur formateur = (Formateur) user;
            formateur.setStatus(UserStatus.valueOf(status));
            userRepository.save(formateur);
            System.out.println("Statut du formateur {} mis à jour à: {} ", email, status);

        } catch (Exception e) {
            System.out.println.error("Erreur lors du traitement du message Kafka", e);
        }
    }

    @KafkaListener(topics = "new-moderator", groupId = "auth-service-group")
    public void handleNewModerator(ConsumerRecord<String, Map<String, Object>> record) {
        try {
            Map<String, Object> moderateurData = record.value();
            System.out.println("Données reçues de Kafka : " + moderateurData);

            String email = (String) moderateurData.get("email");
            String password = (String) moderateurData.get("password");

            if (email == null || password == null) {
                System.err.println("Email ou mot de passe manquant dans les données Kafka");
                return;
            }

            // Vérifier si l'utilisateur existe déjà
            Optional<User> existingUser = userRepository.findByEmail(email);

            if (existingUser.isPresent()) {
                User user = existingUser.get();
                System.out.println("L'utilisateur existe déjà : " + email);
                user.setPhoneNumber((String) moderateurData.get("phone"));
                userRepository.save(user);
                return;
            }

            // User newUser = new User();
            Moderateur newUser = new Moderateur();
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setUsername((String) moderateurData.get("username"));
            newUser.setLastName((String) moderateurData.get("nom"));
            newUser.setFirstName((String) moderateurData.get("prenom"));
            newUser.setPhoneNumber((String) moderateurData.get("phone"));
            newUser.setPhoto((String) moderateurData.get("photo"));
            newUser.setUserType("MODERATEUR");

            userRepository.save(newUser);
            System.out.println("Nouveau modérateur enregistré avec succès: " + email);

        } catch (Exception e) {
            System.err.println("Erreur lors du traitement du message Kafka: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
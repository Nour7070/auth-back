// KafkaConsumerService.java
package com.example.demo.services;

import java.util.Map;
import java.util.Optional;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.demo.classes.Formateur;
import com.example.demo.classes.Moderateur;
import com.example.demo.classes.User;
import com.example.demo.classes.UserStatus;
import com.example.demo.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaConsumerService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(UserRepository userRepository ,
    ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper =objectMapper ;
    }

    @KafkaListener(topics = "formateur-status", groupId = "auth-service-group")
    public void handleFormateurStatusUpdate(Map<String, Object> response) {
        try {
            String email = (String) response.get("email");
            String status = ((String) response.get("status")).toUpperCase();
    
            System.out.println("Réception mise à jour statut pour formateur:" + email + status);
    
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Formateur non trouvé: " + email));
    
            if (!(user instanceof Formateur)) {
                throw new IllegalStateException("L'utilisateur n'est pas un formateur");
            }
    
            Formateur formateur = (Formateur) user;
            formateur.setStatus(UserStatus.valueOf(status)); // Conversion String -> Enum
            userRepository.save(formateur);
            
            System.out.println("Statut mis à jour pour" + email + status);
            
        } catch (IllegalArgumentException e) {
            System.err.println("Statut invalide reçu: {}");
        } catch (Exception e) {
            System.err.println("Erreur traitement mise à jour statut");
        }
    }

    /*@KafkaListener(topics = "new-moderator", groupId = "auth-service-group")
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
    }*/

    @KafkaListener(topics = "new-moderator", groupId = "auth-service-group")
    public void handleNewModerator(Map<String, Object> moderateurData) {
        try {
            System.out.println("Données reçues de Kafka : " + moderateurData);
    
            String email = (String) moderateurData.get("email");
            String password = (String) moderateurData.get("password");
    
    
            if (email == null || password == null) {
                System.err.println("Email ou mot de passe manquant dans les données Kafka");
                return;
            }
    
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                System.out.println("L'utilisateur existe déjà : " + email);
                user.setPhoneNumber((String) moderateurData.get("phone"));
                userRepository.save(user);
                return;
            }
    
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
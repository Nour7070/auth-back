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
import com.fasterxml.jackson.core.type.TypeReference;


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

    /*@KafkaListener(topics = "formateur-status", groupId = "auth-service-group")
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
            formateur.setStatus(UserStatus.valueOf(status)); 
            userRepository.save(formateur);
            
            System.out.println("Statut mis à jour pour" + email + status);
            
        } catch (IllegalArgumentException e) {
            System.err.println("Statut invalide reçu: {}");
        } catch (Exception e) {
            System.err.println("Erreur traitement mise à jour statut");
        }
    }*/

    @KafkaListener(topics = "formateur-status", groupId = "auth-service-group")
    public void handleFormateurStatusUpdate(String message) {
        try {
            // Parse le message JSON en une map (id, email, status)
            Map<String, String> data = objectMapper.readValue(message, new TypeReference<>() {});
            
            Long id = Long.parseLong(data.get("id"));
            String email = data.get("email");
            String status = data.get("status");
    
            Optional<Formateur> optionalFormateur = formateurRepository.findById(id);
            if (optionalFormateur.isPresent()) {
                Formateur formateur = optionalFormateur.get();
    
                // Logique métier selon le status
                if ("APPROVED".equals(status)) {
                    formateur.setStatut("APPROUVÉ");
                } else if ("REJECTED".equals(status)) {
                    formateur.setStatut("REJETÉ");
                }
    
                formateurRepository.save(formateur);
                System.out.printf("✅ Formateur ID %d mis à jour avec le statut : %s%n", id, status);
            } else {
                System.out.printf("❌ Formateur avec ID %d introuvable dans le service Auth%n", id);
            }
        } catch (Exception e) {
            System.out.println("🚨 Erreur lors du traitement du message Kafka : " + e.getMessage());
        }
    }
    

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
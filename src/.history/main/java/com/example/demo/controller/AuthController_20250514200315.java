package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.classes.Formateur;
import com.example.demo.classes.User;
import com.example.demo.services.AuthService;
import com.example.demo.services.KafkaProducerService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public AuthController(AuthService authService, KafkaProducerService kafkaProducerService) {
        this.authService = authService;
        this.kafkaProducerService = kafkaProducerService;
    }
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("phonenumber") String phonenumber,
            @RequestParam("address") String address,
            @RequestParam("userType") String userType,
            @RequestParam(value = "niveauEtude", required = false) String niveauEtude,
            @RequestParam(value = "interets", required = false) String interets,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "certificats", required = false) MultipartFile[] certificatsFiles,
            @RequestPart(value = "experiences", required = false) MultipartFile[] experiencesFiles) {
        
        try {
            Map<String, Object> response = authService.registerUser(
                firstname, lastname, username, email, password,
                phonenumber, address, userType, niveauEtude, interets,
                photo, certificatsFiles, experiencesFiles
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'enregistrement: " + e.getMessage()));
        }
    }

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
    try {
        User foundUser = authService.login(
            loginRequest.get("email"),
            loginRequest.get("password")
        );
        
        if (!foundUser.canLogin()) {
            String errorMessage = "Votre compte n'est pas autorisé à se connecter";
            if (foundUser instanceof Formateur) {
                errorMessage = "Votre compte formateur est en attente d'approbation";
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", errorMessage));
        }
        
        Map<String, String> userData = new HashMap<>();
        userData.put("userId", String.valueOf(foundUser.getId()));
        userData.put("username", foundUser.getUsername() != null ? foundUser.getUsername() : "");
        userData.put("email", foundUser.getEmail() != null ? foundUser.getEmail() : "");
        userData.put("photo", foundUser.getPhoto() != null ? foundUser.getPhoto() : "");
        userData.put("userType", foundUser.getUserType() != null ? foundUser.getUserType() : "");
        
        System.out.println("Réponse utilisateur : " + userData);
        kafkaProducerService.sendUserData(userData);
        return ResponseEntity.ok(userData);
        
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
    }
}
}
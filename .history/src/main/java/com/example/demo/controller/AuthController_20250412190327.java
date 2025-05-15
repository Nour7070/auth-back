package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
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

import com.example.demo.classes.Apprenant;
import com.example.demo.classes.Formateur;
import com.example.demo.classes.Superviseur;
import com.example.demo.classes.User;
import com.example.demo.services.AuthService;
import com.example.demo.services.FileStorageService;
import com.example.demo.services.KafkaProducerService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;
    private final KafkaProducerService kafkaProducerService;
    private final FileStorageService fileStorageService;

    @Autowired
    public AuthController(AuthService authService, KafkaProducerService kafkaProducerService,
            FileStorageService fileStorageService ) {
        this.authService = authService;
        this.kafkaProducerService = kafkaProducerService;
        this.fileStorageService = fileStorageService;
    }

    /*
     * @PostMapping("/register")
     * public ResponseEntity<Map<String, String>> register(
     * 
     * @RequestParam("firstname") String firstname,
     * 
     * @RequestParam("lastname") String lastname,
     * 
     * @RequestParam("username") String username,
     * 
     * @RequestParam("email") String email,
     * 
     * @RequestParam("password") String password,
     * 
     * @RequestParam("phonenumber") String phonenumber,
     * 
     * @RequestParam("address") String address,
     * 
     * @RequestParam("userType") String userType,
     * 
     * @RequestParam(value = "niveauEtude", required = false) String niveauEtude,
     * 
     * @RequestParam(value = "interets", required = false) String interets,
     * 
     * @RequestPart(value = "photo", required = false) MultipartFile photo,
     * 
     * @RequestPart(value = "certificats", required = false) MultipartFile[]
     * certificatsFiles,
     * 
     * @RequestPart(value = "experiences", required = false) MultipartFile[]
     * experiencesFiles
     * ) {
     * User user;
     * 
     * try {
     * switch (userType.toUpperCase()) {
     * case "APPRENANT":
     * user = new Apprenant();
     * ((Apprenant) user).setNiveauEtude(niveauEtude);
     * ((Apprenant) user).setInterets(interets);
     * break;
     * case "FORMATEUR":
     * user = new Formateur();
     * 
     * // Process certificates
     * if (certificatsFiles != null && certificatsFiles.length > 0) {
     * List<String> certificatsUrls = new ArrayList<>();
     * for (MultipartFile file : certificatsFiles) {
     * String fileUrl = fileStorageService.storeFile(file);
     * certificatsUrls.add(fileUrl);
     * }
     * ((Formateur) user).setCertificats(certificatsUrls);
     * }
     * 
     * // Process experiences
     * if (experiencesFiles != null && experiencesFiles.length > 0) {
     * List<String> experiencesUrls = new ArrayList<>();
     * for (MultipartFile file : experiencesFiles) {
     * String fileUrl = fileStorageService.storeFile(file);
     * experiencesUrls.add(fileUrl);
     * }
     * ((Formateur) user).setExperiences(experiencesUrls);
     * }
     * break;
     * case "SUPERVISEUR":
     * user = new Superviseur();
     * break;
     * default:
     * return ResponseEntity.badRequest().body(Map.of("error",
     * "Type d'utilisateur invalide."));
     * }
     * 
     * // Process photo if provided
     * if (photo != null && !photo.isEmpty()) {
     * String photoUrl = fileStorageService.storeFile(photo);
     * user.setPhoto(photoUrl);
     * }
     * 
     * // Set common user properties
     * user.setFirstName(firstname);
     * user.setLastName(lastname);
     * user.setUsername(username);
     * user.setEmail(email);
     * user.setPassword(password);
     * user.setPhoneNumber(phonenumber);
     * user.setAddress(address);
     * user.setUserType(userType);
     * 
     * // Register the user
     * authService.register(user);
     * 
     * return ResponseEntity.ok(Map.of("message",
     * "Utilisateur enregistré avec succès !"));
     * 
     * } catch (Exception e) {
     * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
     * .body(Map.of("error", "Erreur lors de l'enregistrement: " + e.getMessage()));
     * }
     * }
     */
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

        User user;
        try {
            switch (userType.toUpperCase()) {
                case "APPRENANT":
                    user = new Apprenant();
                    ((Apprenant) user).setNiveauEtude(niveauEtude);
                    ((Apprenant) user).setInterets(interets);
                    break;
                case "FORMATEUR":
                    user = new Formateur();

                    if (certificatsFiles != null && certificatsFiles.length > 0) {
                        List<String> certificatsUrls = new ArrayList<>();
                        for (MultipartFile file : certificatsFiles) {
                            String fileUrl = fileStorageService.storeFile(file);
                            certificatsUrls.add(fileUrl);
                        }
                        ((Formateur) user).setCertificats(certificatsUrls);
                    }

                    if (experiencesFiles != null && experiencesFiles.length > 0) {
                        List<String> experiencesUrls = new ArrayList<>();
                        for (MultipartFile file : experiencesFiles) {
                            String fileUrl = fileStorageService.storeFile(file);
                            experiencesUrls.add(fileUrl);
                        }
                        ((Formateur) user).setExperiences(experiencesUrls);
                    }

                    Map<String, String> formateurData = Map.of(
                            "formateurId", user.getId().toString(),
                            "email", user.getEmail(),
                            "nomComplet", user.getFirstName() + " " + user.getLastName(),
                            "certificats", String.join(",", ((Formateur) user).getCertificats()));
                    kafkaProducerService.sendFormateurPending((Formateur) user);
                    break;

                case "SUPERVISEUR":
                    user = new Superviseur();
                    break;
                default:
                    return ResponseEntity.badRequest().body(Map.of("error", "Type d'utilisateur invalide."));
            }

            String photoUrl = null;
            if (photo != null && !photo.isEmpty()) {
                photoUrl = fileStorageService.storeFile(photo);
                user.setPhoto(photoUrl);
            }

            user.setFirstName(firstname);
            user.setLastName(lastname);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setPhoneNumber(phonenumber);
            user.setAddress(address);
            user.setUserType(userType);

            User registeredUser = authService.register(user);

            // Envoi à Kafka uniquement pour les apprenants avec les champs de base
            if (userType.equalsIgnoreCase("APPRENANT")) {
                Map<String, String> userData = Map.of(
                        "username", registeredUser.getUsername(),
                        "email", registeredUser.getEmail(),
                        "photo", photoUrl != null ? photoUrl : "",
                        "userType", registeredUser.getUserType(),
                        "firstName", registeredUser.getFirstName(),
                        "lastName", registeredUser.getLastName()
                // On ne met pas niveauEtude ni interets ici
                );

                kafkaProducerService.sendUserRegistration(userData);
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Utilisateur enregistré avec succès !",
                    "userType", userType));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'enregistrement: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        User foundUser = authService.findByEmail(user.getEmail());

        if (foundUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Email incorrect !"));
        }

        if (!user.getPassword().equals(foundUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Mot de passe incorrect !"));
        }

        Map<String, String> userData = Map.of(
                "username", foundUser.getUsername(),
                "email", foundUser.getEmail(),
                "photo", foundUser.getPhoto(),
                "userType", foundUser.getUserType());

        kafkaProducerService.sendUserData(userData);
        return ResponseEntity.ok(userData);
    }
}
package com.example.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public AuthController(AuthService authService, KafkaProducerService kafkaProducerService, FileStorageService fileStorageService) {
        this.authService = authService;
        this.kafkaProducerService = kafkaProducerService;
        this.fileStorageService = fileStorageService;
    }
    
    /*@PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
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
            @RequestPart(value = "experiences", required = false) MultipartFile[] experiencesFiles
    ) {
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
                    
                    // Process certificates
                    if (certificatsFiles != null && certificatsFiles.length > 0) {
                        List<String> certificatsUrls = new ArrayList<>();
                        for (MultipartFile file : certificatsFiles) {
                            String fileUrl = fileStorageService.storeFile(file);
                            certificatsUrls.add(fileUrl);
                        }
                        ((Formateur) user).setCertificats(certificatsUrls);
                    }
                    
                    // Process experiences
                    if (experiencesFiles != null && experiencesFiles.length > 0) {
                        List<String> experiencesUrls = new ArrayList<>();
                        for (MultipartFile file : experiencesFiles) {
                            String fileUrl = fileStorageService.storeFile(file);
                            experiencesUrls.add(fileUrl);
                        }
                        ((Formateur) user).setExperiences(experiencesUrls);
                    }
                    break;
                case "SUPERVISEUR":
                    user = new Superviseur();
                    break;
                default:
                    return ResponseEntity.badRequest().body(Map.of("error", "Type d'utilisateur invalide."));
            }
            
            // Process photo if provided
            if (photo != null && !photo.isEmpty()) {
                String photoUrl = fileStorageService.storeFile(photo);
                user.setPhoto(photoUrl);
            }
            
            // Set common user properties
            user.setFirstName(firstname);
            user.setLastName(lastname);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setPhoneNumber(phonenumber);
            user.setAddress(address);
            user.setUserType(userType);
            
            // Register the user
            authService.register(user);
            
            return ResponseEntity.ok(Map.of("message", "Utilisateur enregistré avec succès !"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'enregistrement: " + e.getMessage()));
        }
    }*/
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
        @RequestPart(value = "experiences", required = false) MultipartFile[] experiencesFiles
) {
    try {
        User user = createUserBasedOnType(userType, niveauEtude, interets, certificatsFiles, experiencesFiles);
        
        String photoUrl = storePhotoIfPresent(photo);
        if (photoUrl != null) {
            user.setPhoto(photoUrl);
        }
        
        setCommonUserProperties(user, firstname, lastname, username, email, password, phonenumber, address, userType);
        
        User registeredUser = authService.register(user);
        
        if ("APPRENANT".equalsIgnoreCase(userType)) {
            kafkaProducerService.sendUserRegistrationEvent(registeredUser, photoUrl);
        }
        
        return ResponseEntity.ok(Map.of(
            "message", "Utilisateur enregistré avec succès !",
            "userType", userType
        ));
        
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors de l'enregistrement: " + e.getMessage()));
    }
}

private User createUserBasedOnType(String userType, String niveauEtude, String interets, 
                                 MultipartFile[] certificatsFiles, MultipartFile[] experiencesFiles) {
    return switch (userType.toUpperCase()) {
        case "APPRENANT" -> {
            Apprenant apprenant = new Apprenant();
            apprenant.setNiveauEtude(niveauEtude);
            apprenant.setInterets(interets);
            yield apprenant;
        }
        case "FORMATEUR" -> {
            Formateur formateur = new Formateur();
            if (certificatsFiles != null) {
                formateur.setCertificats(storeFiles(certificatsFiles));
            }
            if (experiencesFiles != null) {
                formateur.setExperiences(storeFiles(experiencesFiles));
            }
            yield formateur;
        }
        case "SUPERVISEUR" -> new Superviseur();
        default -> throw new IllegalArgumentException("Type d'utilisateur invalide.");
    };
}

private List<String> storeFiles(MultipartFile[] files) {
    return Arrays.stream(files)
            .map(fileStorageService::storeFile)
            .collect(Collectors.toList());
}

private String storePhotoIfPresent(MultipartFile photo) throws IOException {
    return (photo != null && !photo.isEmpty()) ? fileStorageService.storeFile(photo) : null;
}

private void setCommonUserProperties(User user, String firstname, String lastname, 
                                   String username, String email, String password,
                                   String phonenumber, String address, String userType) {
    user.setFirstName(firstname);
    user.setLastName(lastname);
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(password);
    user.setPhoneNumber(phonenumber);
    user.setAddress(address);
    user.setUserType(userType);
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
                "userType", foundUser.getUserType()
        );
        
        kafkaProducerService.sendUserData(userData);
        return ResponseEntity.ok(userData);
    }
}
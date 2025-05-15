package  com.example.demo.services;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.classes.Apprenant;
import com.example.demo.classes.Formateur;
import com.example.demo.classes.Superviseur;
import com.example.demo.classes.User;
import com.example.demo.classes.UserStatus;
import com.example.demo.repositories.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FileStorageService fileStorageService;

    public AuthService(UserRepository userRepository ,
    KafkaProducerService kafkaProducerService ,
    KafkaTemplate<String, Object> kafkaTemplate ,
    FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.kafkaTemplate =kafkaTemplate ;
        this.fileStorageService =fileStorageService ;
    }

   @Transactional
    public Map<String, Object> registerUser(
            String firstname, String lastname, String username, String email,
            String password, String phonenumber, String address, String userType,
            String niveauEtude, String interets, MultipartFile photo,
            MultipartFile[] certificatsFiles, MultipartFile[] experiencesFiles) {

        User user;
        switch (userType.toUpperCase()) {
            case "APPRENANT":
                user = new Apprenant();
                ((Apprenant) user).setNiveauEtude(niveauEtude);
                ((Apprenant) user).setInterets(interets);
                break;
            case "FORMATEUR":
                user = new Formateur();
                ((Formateur) user).setStatus(UserStatus.PENDING);

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
                break;
            case "SUPERVISEUR":
                user = new Superviseur();
                break;
            default:
                throw new RuntimeException("Type d'utilisateur invalide.");
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

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Nom d'utilisateur déjà utilisé");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        User registeredUser = userRepository.save(user);

        if (userType.equalsIgnoreCase("FORMATEUR")) {
            Formateur registeredFormateur = (Formateur) registeredUser;
            Map<String, Object> formateurData = Map.of(
                "email", registeredFormateur.getEmail(),
                "prenom", registeredFormateur.getFirstName(),
                "nom", registeredFormateur.getLastName(),
                "status", registeredFormateur.getStatus().name()
            );
            kafkaTemplate.send("formateur-pending-topic", formateurData);
        }

        if (userType.equalsIgnoreCase("APPRENANT")) {
            Map<String, String> userData = Map.of(
                "username", registeredUser.getUsername(),
                "email", registeredUser.getEmail(),
                "photo", photoUrl != null ? photoUrl : "",
                "userType", registeredUser.getUserType(),
                "firstName", registeredUser.getFirstName(),
                "lastName", registeredUser.getLastName()
            );
            kafkaProducerService.sendUserRegistration(userData);
        }

        return Map.of(
            "message", "Utilisateur enregistré avec succès !",
            "userType", userType
        );
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password)) // Comparaison directe du mot de passe (⚠️ non sécurisé)
                .orElse(null);
    }
}

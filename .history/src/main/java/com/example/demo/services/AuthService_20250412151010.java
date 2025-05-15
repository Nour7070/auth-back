package  com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.classes.User;
import com.example.demo.repositories.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*@Transactional
    public String register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "Nom d'utilisateur déjà utilisé";
        }

        // On stocke directement le mot de passe sans hashage (pas sécurisé mais demandé)
        /*userRepository.save(user);
        return "Utilisateur enregistré avec succès";*/
        return userRepository.save(user);
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

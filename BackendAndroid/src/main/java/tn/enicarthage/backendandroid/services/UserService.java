package tn.enicarthage.backendandroid.services;


import tn.enicarthage.backendandroid.dto.request.SignupRequest;
import tn.enicarthage.backendandroid.models.User;
import tn.enicarthage.backendandroid.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String registerUser(SignupRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return "Les mots de passe ne correspondent pas.";
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return "Cet email est déjà utilisé.";
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setBirthDate(request.getBirthDate());
        userRepository.save(user);

        return "Utilisateur enregistré avec succès.";
    }
}


package tn.enicarthage.backendandroid.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.enicarthage.backendandroid.dto.request.LoginRequest;
import tn.enicarthage.backendandroid.dto.request.SignupRequest;
import tn.enicarthage.backendandroid.models.User;
import tn.enicarthage.backendandroid.repositories.UserRepository;
import tn.enicarthage.backendandroid.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        String result = userService.registerUser(request);

        Map<String, Object> response = new HashMap<>();
        if (result.equals("Utilisateur enregistré avec succès.")) {
            response.put("message", result);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", result);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();
        if (!userRepository.existsByEmail(loginRequest.getEmail())) {
            response.put("error", "Utilisateur non trouvé");
            return ResponseEntity.status(404).body(response);
        }

        User user = userRepository.findByEmail(loginRequest.getEmail()).get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            response.put("error", "Mot de passe incorrect");
            return ResponseEntity.status(401).body(response);
        }

        response.put("message", "Connexion réussie");
        return ResponseEntity.ok(response);
    }



}


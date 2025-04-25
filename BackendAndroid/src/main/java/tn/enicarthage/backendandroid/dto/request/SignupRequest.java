package tn.enicarthage.backendandroid.dto.request;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String email;
    private String password;
    private String confirmPassword;
    private String phoneNumber;
    private LocalDate birthDate;
}


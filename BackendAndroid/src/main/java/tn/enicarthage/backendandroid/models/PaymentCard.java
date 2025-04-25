package tn.enicarthage.backendandroid.models;

import tn.enicarthage.backendandroid.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(nullable = true)
    private String cardToken;

    private String last4Digits;

    private String expiryDate;


    private String cardHolderName;
}
